package board;

import java.util.ArrayList;

import cards.Card;

public class Hand implements Displayable {
	private ArrayList<Card> handList = new ArrayList<Card>();


	public void add(Card card) {
		handList.add(card);
	}

	public int size() {
		return handList.size();
	}

	public Card getElementAt(int index) {
		return handList.get(index);
	}

	public Card removeElement(int index) {
		return handList.remove(index);
	}

}
