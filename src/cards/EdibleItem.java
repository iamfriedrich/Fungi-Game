package cards;

public class EdibleItem extends Card {
	int flavourPoints;

	
	public EdibleItem(CardType type, String cardName) {
		super(type, cardName);
	}
	
	public int getFlavourPoints() {
		return flavourPoints;
	}
}
