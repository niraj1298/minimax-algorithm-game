import java.util.Random;
import java.util.ArrayList;

public class RandyP {

	public static Player getPlayer() { return new RandomPlayer(); }

	public static class RandomPlayer implements Player {

		private final static Random random = new Random();

		public Card playCard(ArrayList<Card> hand, ArrayList<Card> playedCards, int numOnTable, int onTable, int score1, int score2) {
			return hand.get(random.nextInt(hand.size()));
		}
	}
}
