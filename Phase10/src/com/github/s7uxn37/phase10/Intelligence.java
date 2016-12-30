package com.github.s7uxn37.phase10;

import com.github.s7uxn37.phase10.constructs.Card;
import com.github.s7uxn37.phase10.constructs.Move;
import com.github.s7uxn37.phase10.constructs.Target;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.*;

public class Intelligence {
    public enum TARGET_TYPE {SAME_COLOR, SAME_NUMBER, RUN}
    public enum FIELD_TYPE {TAKE_FREE, CHOOSE_FACE_UP, ASK, JOKER, DISCARD_AND_TAKE, ALL_TAKE_ONE, TAKE_AND_ROLL, ANY_FIELD}
    public enum CARD_LOCATION {FACE_DOWN, FACE_UP, PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4, PLAYER_5, SELF}

	public int numOpponents = 2;

    public ArrayList<Card> faceDown;
    public ArrayList<Card> faceUp;
    public ArrayList<Card>[] opponents;
    public ArrayList<Card> player;
    public ArrayList<Card> desired;
    public HashMap<FIELD_TYPE, Integer> fieldScores;

	private ActionListener updateListener;

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
        this.desired = Card.getListUnknown(5);
        this.fieldScores = new HashMap<>();
        for (FIELD_TYPE t : FIELD_TYPE.values())
            this.fieldScores.put(t, 0);

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
    }

    public void updateDesires(ArrayList<Target> targets) {
        // TODO update desired cards:
        // TODO find best card partitioning for targets
        // TODO find best missing cards -> desired

        // TODO update desired moves
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

    static void log(String message) {
		System.out.println(message);
	}
    static void log(String format, Object... args) {
        log(String.format(format, args));
    }
}