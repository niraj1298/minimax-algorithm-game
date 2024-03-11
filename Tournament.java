import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.Callable;

public class Tournament {

    private static final int maxTime = 10;    // number of seconds allowed per card.
    private static final int overTime = 60;   // number of seconds before player forfeits all hands.
    private static final int numMatches = 50; // number of hands between each player pair.
    private static final Player LOSER = LoserP.getPlayer();

    private static class Contestant implements Comparable<Contestant> {
	Player player;
	String name;
	int wins;
	int matchWins;

	public Contestant(Player player, String name) {
	    this.player = player; this.name = name; wins = 0; matchWins = 0;
	}
	public int compareTo(Contestant other) { return other.wins - wins; }
	public void won() { wins++; matchWins++; }
	public void reset() { matchWins = 0; }
	public String toString() { return name + "(" + matchWins + ":" + wins + ")"; }
    }

    private static final Contestant[] contestants = {
	new Contestant(RandyP.getPlayer(), "RandyP"),
//	new Contestant(RandyP.getPlayer(), "RandyP2"),
	// new Contestant(DonaldS.getPlayer(), "DonaldS"),	
	// new Contestant(BenjaminA.getPlayer(), "BenjaminA"),
	// new Contestant(DanY.getPlayer(), "DanY"),
	// new Contestant(AidanB.getPlayer(), "AidanB"),
	// new Contestant(HunterD.getPlayer(), "HunterD"),
	// new Contestant(ChristianG.getPlayer(), "ChristianG"),
	 new Contestant(LoganH.getPlayer(), "LoganH"),
	// new Contestant(VincentL.getPlayer(), "VincentL"),
	// new Contestant(JulesM.getPlayer(), "JulesM"),
	// new Contestant(OwenM.getPlayer(), "OwenM"),
	// new Contestant(ZaneM.getPlayer(), "ZaneM"),
	 new Contestant(NepalN.getPlayer(), "NepalN"),
	// new Contestant(NicholasP.getPlayer(), "NicholasP"),
	// new Contestant(AllisonP.getPlayer(), "AllisonP"),
	// new Contestant(JessieG.getPlayer(), "JessieG"),
	 new Contestant(KeithR.getPlayer(), "KeithR"),
	// new Contestant(BenjaminS.getPlayer(), "BenjaminS"),
	// new Contestant(CamilleS.getPlayer(), "CamilleS"),
	// new Contestant(AndreW.getPlayer(), "AndreW"),
	// new Contestant(MaggieW.getPlayer(), "MaggieW"),
	 new Contestant(WyattW.getPlayer(), "WyattW"),
	 new Contestant(MaxiH.getPlayer(), "max")
	};

    public static class PlayerCallable implements Callable<Card> {
	private Player player;
	private ArrayList<Card> hand;
	private ArrayList<Card> playedCards;
	private int numOnTable;
	private int onTable;
	private int score1, score2;
	private boolean first;

	public PlayerCallable(Player player, ArrayList<Card> hand, ArrayList<Card> playedCards, int numOnTable,
			      int onTable, int score1, int score2, boolean first) {
	    this.player = player;
	    this.hand = hand;
	    this.playedCards = playedCards;
	    this.numOnTable = numOnTable;
	    this.onTable = onTable;
	    this.score1 = score1;
	    this.score2 = score2;
	    this.first = first;
	}

	public Card call() {
	    return player.playCard(hand, playedCards, numOnTable, onTable, score1, score2);
	}
    }

    public static Card timePlayer(Contestant contestant, ArrayList<Card> hand, ArrayList<Card> playedCards, 
				  int numOnTable, int onTable, int score1, int score2, boolean first) {
	ArrayList<Card> handCopy = new ArrayList<Card>(); // copy in case playCard changes them.
	for(Card c : hand)
	    handCopy.add(new Card(c));
	ArrayList<Card> playedCopy = new ArrayList<Card>();
	for(Card c : playedCards)
	    playedCopy.add(new Card(c));
	Card card = null;
	final ExecutorService service = Executors.newSingleThreadExecutor();
	try {
	    final Future<Card> f = service.submit(new PlayerCallable(contestant.player, handCopy, playedCopy, 
								     numOnTable, onTable, score1, score2, first));
	    card = f.get(maxTime, TimeUnit.SECONDS);
	    //System.out.println(contestant.name + hand.size() + ") took " + 
	    //((System.nanoTime() - startTime) / 1000000000.0) + " seconds.");

	    if(card == null)
		System.out.println(contestant.name + " returned a null card.");
	    else if(hand.indexOf(card) == -1) { 
		System.out.println(contestant.name + " forfeits - no such card");
		card = null;
	    }
	}
	catch (final TimeoutException e) {
	    System.out.println(contestant.name + " (playing " + (first ? "first" : "second") + ") took too long.");
	    card = null;

	}
	catch (final Exception e) {
	    System.out.println(contestant.name + " caused an exception: " + e.getCause());
	    card = null;
	}
	finally {
	    service.shutdownNow();
	    try {
		if(!service.awaitTermination(overTime, TimeUnit.SECONDS)) {
		    System.out.println(contestant.name + " has taken excessive time and forfeits all hands.");
		    contestant.player = LOSER;
		}
	    }
	    catch (InterruptedException e) {}
	    return card;
	}
    }

    public static boolean isSquare(int i) {
	return i == 1 || i == 4 || i == 9 || i == 16 || i == 25 || i == 36 || i == 49 || i == 64 || i == 81
	    || i == 100 || i == 121; // 4 10's, 4 9's, 4 8's, 2 7's  = 122.
    }

    private static int value(Card card) {
	return card.value == Card.Value.ACE ? 1 : card.value.ordinal() + 2;
    }
    
    private static int playHand(Contestant c1, Contestant c2) {
	Deck deck = new Deck();
	deck.shuffle();
	int numCards = 7;  // deal two hands of 7 cards
	ArrayList<Card> hand1 = new ArrayList<Card>();
	ArrayList<Card> hand2 = new ArrayList<Card>();
	ArrayList<Card> playedCards = new ArrayList<Card>();
	for(int i=0; i<numCards; i++) {
	    hand1.add(deck.get(2*i));
	    hand2.add(deck.get(2*i+1));
	}
	int numOnTable = 0;
	int onTable = 0;
	int score1 = 0, score2 = 0;
	while(!hand2.isEmpty()) {  // play hand
	    Card card1 = timePlayer(c1, hand1, playedCards, numOnTable, onTable, score1, score2, true);
	    if(card1 == null) {
		score1 = 0; score2 = 1; 
		break; 
	    }
	    if(isSquare(onTable + value(card1))) {
		score1 += numOnTable +1;
		numOnTable = 0;
		onTable = 0;
	    }
	    else {
		numOnTable++;
		onTable += value(card1);
	    }
	    hand1.remove(card1);
	    playedCards.add(card1);
	    Card card2 = timePlayer(c2, hand2, playedCards, numOnTable, onTable, score1, score2, false);
	    if(card2 == null) {
		score1 = 1; score2 = 0; 
		break; 
	    }
	    if(isSquare(onTable + value(card2))) {
		score2 += numOnTable +1;
		numOnTable = 0;
		onTable = 0;
	    }
	    else {
		numOnTable++;
		onTable += value(card2);
	    }
	    hand2.remove(card2);
	    playedCards.add(card2);
	}
	if(score1 > score2) {
	    c1.won(); 
	    //System.out.println(c1.name + " wins the hand.");  
	    return 1;
	}
	else if(score2 > score1) { 
	    c2.won(); 
	    //System.out.println(c2.name + " wins the hand.");
	    return -1;
	}
	else {
	    //System.out.println("the hand was a tie.");
	    return 0;
	}
    }

    public static void games(Contestant contestant1, Contestant contestant2) {
	int c1wins = 0, c2wins = 0;
	//System.out.println(contestant1.name + " vs. " + contestant2.name);
	for(int n=0; n<numMatches; n++) {
	    System.out.print(".");
	    //System.out.println((++k));
	    int win1 = playHand(contestant1, contestant2);
	    if(win1 == 1)
		c1wins++;
	    else if(win1 == -1)
		c2wins++;
	    int win2 = playHand(contestant2, contestant1);
	    if(win2 == 1)
		c2wins++;
	    else if(win2 == -1)
		c1wins++;
	    //System.out.println(contestant1 + " " + contestant2);
	}
	System.out.println();
	System.out.println(contestant1 + " " + c1wins + " vs. " + contestant2 + " " + c2wins);
    }
	
    
    public static void main(String[] args) {
	int k = 0;
	for(int i=0; i<contestants.length; i++)
	    for(int j=i+1; j<contestants.length; j++) {
		System.out.println(contestants[i].name + " vs. " + contestants[j].name);
		contestants[i].reset(); contestants[j].reset();
		for(int n=0; n<numMatches; n++) {
		    System.out.print((++k) + ": ");
		    playHand(contestants[i], contestants[j]);
		    playHand(contestants[j], contestants[i]);
		    System.out.println(contestants[i] + " " + contestants[j]);
		}
	    }
	Arrays.sort(contestants);
	for(int i=0; i<contestants.length; i++)
	    System.out.println(contestants[i]);
    }
}
