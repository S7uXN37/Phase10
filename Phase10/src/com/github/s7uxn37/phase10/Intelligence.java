package com.github.s7uxn37.phase10;

import com.github.s7uxn37.phase10.constructs.Card;
import com.github.s7uxn37.phase10.constructs.PartialTarget;
import com.github.s7uxn37.phase10.constructs.Move;
import com.github.s7uxn37.phase10.constructs.Target;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.function.Predicate;

public class Intelligence {
    public enum TARGET_TYPE {SAME_COLOR, SAME_NUMBER, RUN}
    public enum FIELD_TYPE {TAKE_FACE_DOWN, CHOOSE_FACE_UP, ASK, JOKER, DISCARD_AND_TAKE, ALL_TAKE_ONE, TAKE_AND_ROLL, ANY_FIELD}
    public enum CARD_LOCATION {FACE_DOWN, FACE_UP, PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4, PLAYER_5, SELF}

	public int numOpponents = 2;

    public ArrayList<Card> faceDown;
    public ArrayList<Card> faceUp;
    public ArrayList<Card>[] opponents;
    public ArrayList<Card> player;
    public ArrayList<PartialTarget> partialTargets;
    public HashMap<FIELD_TYPE, String> fieldInfo;

	private ActionListener updateListener;
    private HashMap<Integer, ArrayList<Card>> unknownPoolCache = new HashMap<>();

    private static final List<Card> allExistingCards;
    static {
        ArrayList<Card> allCards = new ArrayList<>();
        // Add all cards in game
        for (int color = 0; color < 4; color++) {
            for (int number = 1; number <= 12; number++) {
                Card c = new Card();
                c.colorIndex = color;
                c.number = number;
                for (int i = 0; i < 2; i++) {
                    allCards.add(c);
                }
            }
        }
        // Save cards as unmodifiableList in static final variable
        allExistingCards = Collections.unmodifiableList(allCards);
    }

	public void setUpdateListener(ActionListener listener) {
		updateListener = listener;
        log(
                "updateListener set"
        );
		causeUpdate();
		log(
                "waiting for first input..."
		);
	}

	void causeUpdate() {
        SwingUtilities.invokeLater(() -> updateListener.actionPerformed(null));
        log(
                "updateListener called\n"
        );
    }

	public void init(int numPlayers, Card[] playerCards) {
	    this.numOpponents = numPlayers - 1;

		this.faceDown = Card.getListUnknown(96 - 10*numOpponents - playerCards.length - 5);
		this.faceUp = Card.getListUnknown(5);
        //noinspection unchecked
        this.opponents = (ArrayList<Card>[]) new ArrayList[numOpponents];
		for (int i = 0; i < numOpponents; i++) {
			this.opponents[i] = Card.getListUnknown(10);
		}
		this.player = new ArrayList<>();
        Collections.addAll(player, playerCards);
        this.partialTargets = new ArrayList<>();
        this.fieldInfo = new HashMap<>();
        for (FIELD_TYPE t : FIELD_TYPE.values())
            this.fieldInfo.put(t, "not calculated!");

		log(
				"playerNumber set\n" +
				"card pool initialized\n" +
				"hands initialized\n" +
                "player's hand set"
		);
	}

    public void updateFaceUp(Card[] cards) {
        faceUp.clear();
        faceUp.addAll(Arrays.asList(cards));

        log(
                "face up cards updated"
        );

        causeUpdate();
    }

    public void updateDesires(ArrayList<Target> targets) {
	    // ############# create PartialTargets with best partitioning and find missing cards
        // find best card partitioning for targets
        int[] partitioning = MultiOptimizer.partition(player, targets);

        // update partialTargets with partitioning
        ArrayList<Card>[] splitCards = MultiOptimizer.split(partitioning, player.toArray(new Card[0]), targets.size());
        partialTargets.clear();
        for (int i = 0; i < targets.size(); i++) {
            partialTargets.add(PartialTarget.getPartialTarget(targets.get(i), splitCards[i]));
        }

        // ############# update partialTargets moves:
        // combine missing cards from all targets
        ArrayList<Card> combinedMissing = new ArrayList<>();
        for (PartialTarget pt : partialTargets)
            combinedMissing.addAll(pt.desiredCards);
        ArrayList<Card> combinedUsed = new ArrayList<>();
        for (PartialTarget pt : partialTargets)
            Collections.addAll(combinedUsed, pt.cards);

        // sum desirable card for each CARD_LOCATION: how many useful cards are in this location?
        HashMap<CARD_LOCATION, Double> desirableCardAmount = new HashMap<>();
        double[] faceUpTopCardsPerc = new double[3]; // saves target achievement percentage for top 3 cards of faceUp
        for (CARD_LOCATION card_location : CARD_LOCATION.values()) {
            ArrayList<Card> locationCards = getCardsInLocation(card_location);
            if (locationCards.size() == 0) // -> self or opponent not found
                continue; // Don't count cards, there will always be 0 and we'll assume that later using getOrDefault() anyway

            // count matches for each missing card in each CARD_LOCATION: How many of this card are in this location?
            for (Card desired : combinedMissing) {
                double amount = 0;
                ArrayList<Integer> indicesAddedToTopCardsPerc = new ArrayList<>();
                // first calculate matching cards
                if (desired.isUnknown()) { // completely unknown -> match any card
                    amount += countCards(locationCards);
                } else if (desired.colorIndex == -1) { // only number known -> match all with same number
                    for (Card c : locationCards) {
                        double deltaAmount = 0;
                        if (c.number == desired.number) // c has matching number
                            deltaAmount = c.prob;
                        else if (c.number == -1) { // c has unknown number
                            // deltaAmount = probToFindThat Number In AnyUnknownCard;
                            deltaAmount = unknownCardMatchProb(
                                    card -> card.number == desired.number,
                                    card -> true
                            );
                        }
                        if (card_location == CARD_LOCATION.FACE_UP) {
                            tryUpdateTopCardsPerc(locationCards, c, indicesAddedToTopCardsPerc, deltaAmount, faceUpTopCardsPerc);
                        }
                        amount += deltaAmount;
                    }
                } else if (desired.number == -1) { // only color known -> match all with same color
                    for (Card c : locationCards) {
                        double deltaAmount = 0;
                        if (c.colorIndex == desired.colorIndex) // c has matching color
                            deltaAmount = c.prob;
                        else if (c.colorIndex == -1) { // c has unknown color
                            // deltaAmount = probToFindThat Color In AnyUnknownCard;
                            deltaAmount = unknownCardMatchProb(
                                    card -> card.colorIndex == desired.colorIndex,
                                    card -> true
                            );
                        }
                        if (card_location == CARD_LOCATION.FACE_UP) {
                            tryUpdateTopCardsPerc(locationCards, c, indicesAddedToTopCardsPerc, deltaAmount, faceUpTopCardsPerc);
                        }
                        amount += deltaAmount;
                    }
                } else { // number and color known -> only match exactly
                    for (Card c : locationCards) {
                        double deltaAmount = 0;
                        if (desired.number == c.number && desired.colorIndex == c.colorIndex) {
                            deltaAmount += c.prob;
                        } else {
                            if (c.colorIndex == -1) { // c has unknown color
                                if (c.number == desired.number) {
                                    // deltaAmount = probToFindThat Color In CardWithNumber;
                                    deltaAmount = unknownCardMatchProb(
                                            card -> card.colorIndex == desired.colorIndex,
                                            card -> card.number == desired.number
                                    );
                                }
                            } else if (c.number == -1) { // c has unknown number
                                if (c.colorIndex == desired.colorIndex) {
                                    // deltaAmount = probToFindThat Number In CardWithColorIndex;
                                    deltaAmount = unknownCardMatchProb(
                                            card -> card.number == desired.number,
                                            card -> card.colorIndex == desired.colorIndex
                                    );
                                }
                            } else { // c is completely unknown
                                // deltaAmount = probToFindThat ColorAndNumber In AnyUnknownCard;
                                deltaAmount = unknownCardMatchProb(
                                        card -> card.colorIndex == desired.colorIndex && card.number == desired.number,
                                        card -> true
                                );
                            }
                        }

                        if (card_location == CARD_LOCATION.FACE_UP) {
                            tryUpdateTopCardsPerc(locationCards, c, indicesAddedToTopCardsPerc, deltaAmount, faceUpTopCardsPerc);
                        }
                        amount += deltaAmount;
                    }
                }

                double prev = desirableCardAmount.getOrDefault(card_location, 0d);
                desirableCardAmount.put(card_location, prev + amount);
            }
        }

        // link FIELD_TYPEs to CARD_LOCATIONS, set info/score accordingly
        for (FIELD_TYPE field_type : FIELD_TYPE.values()) {
            switch (field_type) {
                case TAKE_FACE_DOWN: { // outputs average probability if one card is taken from top
                    // all cards unknown -> probability = 1/locationAmount * desirableAmount * 1/missingAmount
                    double probFaceDown = desirableCardAmount.getOrDefault(CARD_LOCATION.FACE_DOWN, 0d) / (countCards(faceDown) * countCards(combinedMissing));
                    int percentDown = (int) (probFaceDown * 100);
                    fieldInfo.put(field_type, (percentDown) + "%");
                    break;
                } case CHOOSE_FACE_UP: {
                    // probability = desirableAmount * 1/missingAmount
                    double probFaceUp = desirableCardAmount.getOrDefault(CARD_LOCATION.FACE_UP, 0d) / countCards(combinedMissing);
                    int percent = (int) (probFaceUp * 100);
                    fieldInfo.put(field_type, percent + "%");
                    break;
                } case ASK: {
                    double locationAmount = 0;
                    double desirableAmount = 0;
                    CARD_LOCATION[] locations = new CARD_LOCATION[]{CARD_LOCATION.PLAYER_1, CARD_LOCATION.PLAYER_2, CARD_LOCATION.PLAYER_3, CARD_LOCATION.PLAYER_4, CARD_LOCATION.PLAYER_5};
                    for (CARD_LOCATION location : locations) {
                        locationAmount += countCards(getCardsInLocation(location));
                        desirableAmount += desirableCardAmount.getOrDefault(location, 0d);
                    }
                    // probability = 1/locationAmount * desirableAmount * 1/missingAmount
                    double probability = desirableAmount / (locationAmount * countCards(combinedMissing));
                    int percent = (int) (probability * 100);
                    fieldInfo.put(field_type, percent + "%");
                    break;
                } case JOKER: {
                    // probability = 1/missingAmount
                    int percent = (int) ((1d / countCards(combinedMissing)) * 100);
                    fieldInfo.put(field_type, percent + "%");
                    break;
                } case DISCARD_AND_TAKE: {
                    // probability = 1/locationAmount * desirableAmount * 1/missingAmount
                    double probFaceDown = desirableCardAmount.getOrDefault(CARD_LOCATION.FACE_DOWN, 0d) / (countCards(faceDown) * countCards(combinedMissing));
                    // totProb = (amountUseless + 1) * probability
                    double amountUseless = Math.min(4, countCards(player) - countCards(combinedUsed));
                    int percent = (int) ((amountUseless + 1) * probFaceDown * 100);
                    fieldInfo.put(field_type, percent + "%; discard " + (int)Math.round(amountUseless));
                    break;
                } case ALL_TAKE_ONE: {
                    // probability = 1/locationAmount * desirableAmount * 1/missingAmount
                    double probFaceDown = desirableCardAmount.getOrDefault(CARD_LOCATION.FACE_DOWN, 0d) / (countCards(faceDown) * countCards(combinedMissing));
                    int percent = (int) (probFaceDown * 100);
                    fieldInfo.put(field_type, percent + "%");
                    break;
                } default:
                    fieldInfo.put(field_type, "not calculated!");
                    break;
            }
        }

        // TAKE_AND_ROLL
        // Sum up averages of all pairs of fieldTypes -> approx. of next roll total probability
        // Divide by combinationAmount to get average probability for any two possible fields
        double percentSum = 0;
        int combAmount = 0;
        for (FIELD_TYPE field_type : FIELD_TYPE.values()) {
            if (field_type == FIELD_TYPE.ANY_FIELD)
                continue;
            int percent = getPercentFromFieldInfo(field_type);
            if (percent == -1)
                continue;
            for (FIELD_TYPE field_type2 : FIELD_TYPE.values()) {
                if (field_type2 == FIELD_TYPE.ANY_FIELD || field_type == field_type2)
                    continue;
                int percent2 = getPercentFromFieldInfo(field_type2);
                if (percent2 == -1)
                    continue;
                percentSum += (double)(percent + percent2)/2d;
                combAmount++;
            }
        }
        int percentRoll = (int) (percentSum / (double)combAmount);
        // Add percentTake, output to fieldInfo
        int percentTake = getPercentFromFieldInfo(FIELD_TYPE.TAKE_FACE_DOWN);
        fieldInfo.put(FIELD_TYPE.TAKE_AND_ROLL, (percentRoll + percentTake) + "%");

        // ANY_FIELD
        int maxPercent = 0;
        ArrayList<FIELD_TYPE> bestFields = new ArrayList<>();
        bestFields.add(FIELD_TYPE.ANY_FIELD);
        for (FIELD_TYPE field_type : FIELD_TYPE.values()) {
            int percent = getPercentFromFieldInfo(field_type);
            if (percent == -1)
                continue;
            if (percent > Math.min(maxPercent, 100)) {
                maxPercent = percent;
                bestFields.clear();
                bestFields.add(field_type);
            } else if (percent == Math.min(maxPercent, 100))
                bestFields.add(field_type);
        }
        fieldInfo.put(FIELD_TYPE.ANY_FIELD, maxPercent + "%; " + bestFields.toString());

        causeUpdate();
    }

    private void tryUpdateTopCardsPerc(ArrayList<Card> locationCards, Card c, ArrayList<Integer> indicesAddedToTopCardsPerc, double deltaAmount, double[] faceUpTopCardsPerc) {
        int index = locationCards.indexOf(c);
        while_loop:
        while (indicesAddedToTopCardsPerc.contains(index)) {
            for (int i = index+1; i < locationCards.size(); i++) {
                if (locationCards.get(i).equals(c)) {
                    index = i;
                    continue while_loop;
                }
            }
            index = -1;
            break;
        }
        if (index >= 0 && index < faceUpTopCardsPerc.length) {
            indicesAddedToTopCardsPerc.add(index);
            faceUpTopCardsPerc[index] += deltaAmount;
        }
    }

    private int getPercentFromFieldInfo(FIELD_TYPE field_type) {
        String info = fieldInfo.getOrDefault(field_type, "0%");
        int endIndex = info.indexOf('%');
        if (endIndex == -1)
            return -1;
        return Integer.parseInt(info.substring(0, endIndex));
    }

    private ArrayList<Card> getCardsInLocation(CARD_LOCATION card_location) {
	    ArrayList<Card> locationCards;
        try {
            switch (card_location) {
                case FACE_UP:
                    locationCards = faceUp;
                    break;
                case FACE_DOWN:
                    locationCards = faceDown;
                    break;
                case PLAYER_1:
                    locationCards = opponents[0];
                    break;
                case PLAYER_2:
                    locationCards = opponents[1];
                    break;
                case PLAYER_3:
                    locationCards = opponents[2];
                    break;
                case PLAYER_4:
                    locationCards = opponents[3];
                    break;
                case PLAYER_5:
                    locationCards = opponents[4];
                    break;
                case SELF:
                    locationCards = player;
                    break;
                default:
                    locationCards = new ArrayList<>();
                    break;
            }
        } catch (IndexOutOfBoundsException e) {
            locationCards = new ArrayList<>();
        }
        return locationCards;
    }

    public void updateCard(Card c, Move m) {
        if (handleFrom(c, m)) {
            handleTo(c, m);

            faceUp.removeIf(Card::probIsZero);

            causeUpdate();
        } else {
            log(
                    "ABORTED!"
            );
        }
	}

    private boolean handleFrom(Card c, Move m) {
        int opponentIndex = -1;
        switch (m.from) {
            case FACE_DOWN: // all cards unknown
                faceDown.remove(0);
                log(
                        "unknown face down card removed, stack shortened"
                );
                break;
            case FACE_UP:
                if (!handleRemove(faceUp, "face up", c, 1))
                    return false;
                break;
            case SELF: // all cards known
                try {
                    player.remove(player.indexOf(c));
                    log(
                            "own card removed"
                    );
                } catch (IndexOutOfBoundsException e) {
                    log(
                            "ERROR: own card not found, although all cards known"
                    );
                    return false;
                }
                break;
            case PLAYER_1:
                opponentIndex = 0;
                break;
            case PLAYER_2:
                opponentIndex = 1;
                break;
            case PLAYER_3:
                opponentIndex = 2;
                break;
            case PLAYER_4:
                opponentIndex = 3;
                break;
            case PLAYER_5:
                opponentIndex = 4;
                break;
            default:
                // useless, but stops warnings ;)
                break;
        }

        if (opponentIndex != -1) {
            if (!handleRemove(opponents[opponentIndex], "opponent", c, 0))
                return false;
        }

        return true;
	}

    public static boolean handleRemove(ArrayList<Card> cards, String collLabel, Card c, int startIndex) {
	    return handleRemove(cards, collLabel, c, startIndex, true);
    }
	public static boolean handleRemove(ArrayList<Card> cards, String collLabel, Card c, int startIndex, boolean verbose) {
        int i = cards.indexOf(c);  // try to find card
        boolean found = !c.isUnknown() && (i != -1); // finding an unknown card must always fail
        if (!found) {                // if not found, treat like unknown card
            if(verbose) log(
                    "known " + collLabel + " card not found, treating like unknown..."
            );
        } else {
            try {
                cards.remove(i);       // remove found card
                if(verbose) log(
                        "known " + collLabel + " card removed"
                );
            } catch (IndexOutOfBoundsException e) {
                if(verbose) log(
                        collLabel + " card not found, all cards known"
                );
                return false;
            }
        }

        if (!found) { // card unknown
            // decrease probability of all cards, starting at startIndex
            double sum = 0;
            for (int j = startIndex; j < cards.size(); j++) {
                sum += cards.get(j).prob;
            }
            double multiplier = 1 - 1/sum;
            for (int j = startIndex; j < cards.size(); j++) {
                cards.get(j).prob *= multiplier;
            }
            if(verbose) log(
                    "unknown " + collLabel + " card removed, probabilities decreased"
            );
        }

        return true;
    }

    private void handleTo(Card c, Move m) {
        int opponentIndex = -1;
        switch (m.to) {
            case FACE_DOWN: // wtf, add new unknown card
                faceDown.add(new Card());
                log(
                        "unknown card added to face down"
                );
                break;
            case FACE_UP:
                faceUp.add(0, c);
                log(
                        "card added to face up"
                );
                break;
            case SELF: // all cards known
                player.add(c);
                log(
                        "card added to player"
                );
                break;
            case PLAYER_1:
                opponentIndex = 0;
                break;
            case PLAYER_2:
                opponentIndex = 1;
                break;
            case PLAYER_3:
                opponentIndex = 2;
                break;
            case PLAYER_4:
                opponentIndex = 3;
                break;
            case PLAYER_5:
                opponentIndex = 4;
                break;
            default:
                // useless, but stops warnings ;)
                break;
        }

        if (opponentIndex != -1) {
            opponents[opponentIndex].add(c);
            log(
                    "unknown card added to opponent[%1$d]", opponentIndex
            );
        }
    }

    public static double countCards(ArrayList<Card> list) {
	    double count = 0;
	    for (Card c : list) {
	        count += c.prob;
        }
        return count;
    }

    public double unknownCardMatchProb(Predicate<Card> isMatch, Predicate<Card> isValidSample) {
	    // create list of all possible unknown cards (cache it?)
        ArrayList<Card> known = new ArrayList<>();
        for (CARD_LOCATION card_location : CARD_LOCATION.values()) {
            ArrayList<Card> locationCards = getCardsInLocation(card_location);
            for (Card card : locationCards) {
                if (card.probIsOne())
                    known.add(card);
            }
        }

        ArrayList<Card> samples = new ArrayList<>();
        for (Card card : allExistingCards) {
            int index = known.indexOf(card);
            if (index != -1) { // card has known location, remove from known
                known.remove(index);
            } else { // couldn't find card, add to unknown
                samples.add(card);
            }
        }

        // filter list by isValidSample
        samples.removeIf(isValidSample.negate());

        // count entries with isMatch
        int matches = 0;
        for (Card card : samples) {
            if (isMatch.test(card))
                matches += card.prob;
        }

        // return matches/entries
        return (double)matches / countCards(samples);
    }

    static void log(String message) {
		System.out.println(message);
	}
    static void log(String format, Object... args) {
        log(String.format(format, args));
    }
}