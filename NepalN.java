/**
 * Developer: Niraj Nepal
 * Project: Card Game Strategy Implementation
 * Description: This class, 'NepalN', implements a strategic card game player based on the alpha-beta pruning algorithm,
 * an optimization of the minimax algorithm for decision-making in game theory. The strategy aims to choose the optimal card
 * to play from a hand, taking into account the current state of the game, including cards already played, the number of cards on
 * the table, the current score, and the potential for future moves.
 *
 * Note: Algorithm Adapted from book code.
 */


import java.util.ArrayList;

public class NepalN {

    public static Player getPlayer() { return new NepPlayer(); }

    public static class NepPlayer implements Player {
        int value(Card card) {
            return card.value == Card.Value.ACE ? 1 : card.value.ordinal() + 2;
        }

        public Card playCard(ArrayList<Card> hand, ArrayList<Card> playedCards, int numOnTable, int onTable, int score1, int score2) {
            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;
            int bestValue = Integer.MIN_VALUE;
            Card bestCard = null;

            long startTime = System.currentTimeMillis();
            long duration = 8500;
            while (System.currentTimeMillis() - startTime < duration) {
                for (Card card : hand) {
                    int newOnTable = onTable + value(card);
                    ArrayList<Card> newPlayedCards = new ArrayList<>(playedCards);
                    newPlayedCards.add(card);

                    int newNumOnTable = numOnTable + 1;
                    int tempScore1 = score1;
                    if (isPerfectSquare(newOnTable)) {
                        tempScore1 += newNumOnTable;
                        newNumOnTable = 0;
                    }

                    int value = minValue(newPlayedCards, newNumOnTable, newOnTable, alpha, beta, tempScore1, score2);

                    if (value > bestValue) {
                        bestValue = value;
                        bestCard = card;
                    }

                    alpha = Math.max(alpha, value);
                }
            }

            return bestCard;
        }

        private boolean isPerfectSquare(int number) {
            int sqrt = (int) Math.sqrt(number);
            return sqrt * sqrt == number;
        }

        private int minValue(ArrayList<Card> playedCards, int numOnTable, int onTable, int alpha, int beta, int score1, int score2) {
            if (isTerminal(playedCards, onTable)) {
                return calculateUtility(score1, score2);
            }
            int value = Integer.MAX_VALUE;
            for (Card card : playedCards) {
                int newOnTable = onTable + value(card);
                ArrayList<Card> newHand = new ArrayList<>(playedCards);
                newHand.remove(card);
                int tempScore2 = score2;
                if (isPerfectSquare(newOnTable)) {
                    tempScore2 += numOnTable + 1;
                    numOnTable = 0;
                }
                value = Math.min(value, maxValue(newHand, numOnTable, newOnTable, alpha, beta, score1, tempScore2));
                if (value <= alpha) {
                    return value;
                }
                beta = Math.min(beta, value);
            }
            return value;
        }

        private int maxValue(ArrayList<Card> hand, int numOnTable, int onTable, int alpha, int beta, int score1, int score2) {
            if (isTerminal(hand, onTable)) {
                return calculateUtility(score1, score2);
            }
            int value = Integer.MIN_VALUE;
            for (Card card : hand) {
                int newOnTable = onTable + value(card);
                int newNumOnTable = numOnTable + 1;
                int tempScore1 = score1;
                if (isPerfectSquare(newOnTable)) {
                    tempScore1 += newNumOnTable;
                    newNumOnTable = 0;
                }
                ArrayList<Card> newHand = new ArrayList<>(hand);
                newHand.remove(card);
                value = Math.max(value, minValue(newHand, newNumOnTable, newOnTable, alpha, beta, tempScore1, score2));
                if (value >= beta) {
                    return value;
                }
                alpha = Math.max(alpha, value);
            }
            return value;
        }

        private boolean isTerminal(ArrayList<Card> hand, int onTable) {
            return hand.isEmpty();
        }
        private int calculateUtility(int score1, int score2) {
            return score1 - score2;
        }
    }

}
