package cards;

public class Mushroom extends EdibleItem {
	int sticksPerMushroom;


	public Mushroom(CardType type, String cardName) {
		super(type, cardName);
	}


	public int getSticksPerMushroom() {
		return sticksPerMushroom;
	}

}
