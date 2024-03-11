import java.util.Random;
import java.util.ArrayList;

public class LoserP {

    public static Player getPlayer() { return new LoserPlayer(); }

    public static class LoserPlayer implements Player {


	public Card playCard(ArrayList<Card> hand, ArrayList<Card> playedCards, int numOnTable, int onTable,
			     int score1, int score2) {
	    return null;
	}
    }
}
