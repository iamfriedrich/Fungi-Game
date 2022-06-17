package board;

import java.util.Stack;

import cards.Card;

public class CardPile {
	private Stack<Card> cPile;
	

	public CardPile() {
		cPile = new Stack<Card>();
	}
	
	public void addCard(Card card) {
		cPile.add(card);
	}
	
	public Card drawCard() {
		return cPile.pop();
	}
	
	public void shufflePile() {
		java.util.Collections.shuffle(cPile);
	}
	
	public int pileSize() {
		return cPile.size();
	}
	
	public boolean isEmpty() {
		return cPile.isEmpty();
	}

}
	
