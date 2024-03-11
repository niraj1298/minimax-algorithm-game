import java.util.ArrayList;

public interface Player {

    public Card playCard(ArrayList<Card> hand, ArrayList<Card> playedCards, int numOnTable,
			 int onTable, int score1, int score2);
}
