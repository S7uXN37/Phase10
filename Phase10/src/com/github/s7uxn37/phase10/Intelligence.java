package com.github.s7uxn37.phase10;

import com.github.s7uxn37.phase10.constructs.Card;
import com.github.s7uxn37.phase10.constructs.Move;
import com.github.s7uxn37.phase10.constructs.Target;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
                "updateListener called"
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

    public void updateDesires(ArrayList<Target> targets) {
        // TODO update desired cards
        // TODO update desired moves
    }

    public void updateCard(Card c, Move m) {
        // TODO update card location
        // from facedown -> reduce by one
        // from faceup -> if known at both: remove specific card, else invalidate stack and reduce by one
        // from player -> if known at both: remove specific card, else invalidate stack and reduce by one
        // to faceup -> know first
        // to facedown -> wtf, but invalidate stack
        // to player -> add

        if (handleFrom(c, m)) {
            handleTo(c, m);
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
                boolean removeUnknownCard = false;

                if (!c.isUnknown()) { // card known
                    int i = faceUp.indexOf(c);  // try to find card
                    boolean found = (i != -1);
                    if (!found) {                // if not found, treat like unknown card
                        removeUnknownCard = true;
                        log(
                                "known face up card not found, treating like unknown..."
                        );
                    } else {
                        try {
                            faceUp.remove(i);       // remove found card
                            log(
                                    "known face up card removed"
                            );
                        } catch (IndexOutOfBoundsException e) {
                            log(
                                    "face up card not found, all cards known"
                            );
                            return false;
                        }
                    }
                }

                if (c.isUnknown() || removeUnknownCard) { // card unknown
                    // decrease probability of all cards except first (if he had taken that one, we'd know)
                    for (int i = 1; i < faceUp.size(); i++) {
                        faceUp.get(i).prob *= 1f - 1f/(faceUp.size() - 1);
                    }
                    log(
                            "unknown face up card removed, probabilities decreased"
                    );
                }
                break;
            case SELF: // all cards known
                try {
                    player.remove(player.indexOf(c));
                    log(
                            "own card removed"
                    );
                } catch (IndexOutOfBoundsException e) {
                    log(
                            "own card not found, all cards known"
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
            if (c.isUnknown()) { // card unknown
                // decrease probability of all cards
                for (int i = 0; i < opponents[opponentIndex].size(); i++) {
                    opponents[opponentIndex].get(i).prob *= 1f - 1f/(opponents[opponentIndex].size());
                }
                log(
                        "unknown opponent card removed, probabilities decreased"
                );
            } else { // card known
                int i = opponents[opponentIndex].indexOf(c);  // try to find card
                boolean found = (i != -1);
                if (!found)                 // if not found, find an unknown card
                    i = opponents[opponentIndex].indexOf(new Card());
                try {
                    opponents[opponentIndex].remove(i);       // remove found card
                    log(
                            found ? "known opponent card removed" : "known opponent card not found, unknown card removed"
                    );
                } catch (IndexOutOfBoundsException e) {
                    log(
                            "opponent card not found, all cards known"
                    );
                    return false;
                }
            }
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

    static int countCards(ArrayList<Card> list) {
	    float count = 0;
	    for (Card c : list) {
	        count += c.prob;
        }
        return (int) count;
    }

    static void log(String message) {
		System.out.println(message);
	}
    static void log(String format, Object... args) {
        log(String.format(format, args));
    }
}