package cards;

public class Card {
	CardType type;
	String cardName;
	
	public Card(CardType type, String cardName) {
		this.type = type;
		this.cardName = cardName;
	}

	public CardType getType() {
		return type;
	}

	public String getName() {
		return cardName;
	}
}


