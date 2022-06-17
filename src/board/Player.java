package board;

import java.util.ArrayList;

import cards.*;

public class Player {
	private Hand h = new Hand();
	private Display d = new Display();
	private int score = 0;
	private int handlimit = 8;
	private int sticks = 0;

	public Player() {
		d.add(new Pan());
	}
	
	public int getScore() {
		return score;
	}
	
	public int getHandLimit() {
		return handlimit;
	}
	
	public int getStickNumber() {
		return sticks;
	}
	
	public  void addSticks(int number) {
		this.sticks += number; 
		
		for (int i = 0; i < number; i++) {
			d.add(new Stick());
		}
	}
	
	public void removeSticks(int number) {
		this.sticks -= number;
		
		for (int i = d.size() - 1; i >= 0; i--) {
			Card card = d.getElementAt(i);
			if (card.getType() == CardType.STICK) {
				d.removeElement(i);
				number--;
				if (number <= 0)
					break;
			}
		}
	}
	
	public Hand getHand() {
		return h;
	}
	
	public Display getDisplay() {
		return d;
	}
	public void addCardtoHand(Card card) {
		h.add(card);
		
		if (card.getType() == CardType.BASKET)
			handlimit += 3; 
	}
	
	public void addCardtoDisplay(Card card) {
		d.add(card);
		
		if (card.getType() == CardType.BASKET)
			handlimit += 2; 
	}
	
	public boolean takeCardFromTheForest(int position) {
		if (position < 1 || position > Board.getForest().size())
			return false;
		
		if (position > 2 && this.getStickNumber() < position - 2)
			return false;
		
		int index = Board.getForest().size() - position;
		Card card = Board.getForest().getElementAt(index);
		
		if (card.getType() == CardType.BASKET) {
			this.addCardtoDisplay(card);
			Board.getForest().removeCardAt(index);
		}
		else {
			if (h.size() >= this.getHandLimit())
				return false;
			this.addCardtoHand(card);
			Board.getForest().removeCardAt(index);
		}
		
		if (position > 2)
			this.removeSticks(position - 2);
		return true;
	}
	
	public boolean takeFromDecay() {
		int limitToAdd = 0;
		int cardToAdd = 0;
		
		for (int i = 0; i < Board.getDecayPile().size(); i++) {
			Card card = Board.getDecayPile().get(i);
			if (card.getType() == CardType.BASKET)
				limitToAdd += 2;
			else
				cardToAdd++;
		}
		
		if (h.size() + cardToAdd > handlimit + limitToAdd)
			return false;
		
		for (int i = 0; i < Board.getDecayPile().size(); i++) {
			Card card = Board.getDecayPile().get(i);
			if (card.getType() == CardType.BASKET)
				this.addCardtoDisplay(card);
			else
				this.addCardtoHand(card);
		}
		
		Board.getDecayPile().clear();
		
		return true;
	}
	
	public boolean cookMushrooms(ArrayList<Card> cards) {
		int panNumber = 0;
		int mushroomNumber = 0;
		int butterNumber = 0;
		int ciderNumber = 0;
		String mushroomName = null;
		
		for (int i = 0; i < cards.size(); i++) {
			Card card = cards.get(i);
			if (card.getType() == CardType.PAN)
				panNumber++;
			else if (card.getType() == CardType.BUTTER)
				butterNumber++;
			else if (card.getType() == CardType.CIDER)
				ciderNumber++;
			else if (card.getType() == CardType.DAYMUSHROOM || card.getType() == CardType.NIGHTMUSHROOM) {
				if (mushroomName == null) {
					mushroomName = card.getName();
				}
				else if (!card.getName().equals(mushroomName)) {
					return false;
				}
				if (card.getType() == CardType.DAYMUSHROOM)
					mushroomNumber++;
				else 
					mushroomNumber += 2;
			}
			else
				return false;
		}
		
		int panIndexInDisplay = -1;
		if (panNumber != 1) {
			for (int i = 0; i < this.getDisplay().size(); i++) {
				if (this.getDisplay().getElementAt(i).getType() == CardType.PAN) {
					panIndexInDisplay = i;
					break;
				}
			}
			if (panIndexInDisplay == -1)
				return false;
		}
		
		int totalMushroomsNeeded = butterNumber * 4 + ciderNumber * 5;
		if (totalMushroomsNeeded > 0) {
			if (mushroomNumber < totalMushroomsNeeded)
				return false;
		}
		else if (mushroomNumber < 3)
			return false;
		
		for (int i = 0; i < cards.size(); i++) {
			Card card = cards.get(i);
			for (int j = 0; j < this.getHand().size(); j++) {
				if (this.getHand().getElementAt(j).equals(card)) {
					this.getHand().removeElement(j);
					break;
				}
			}
			
			if (card.getType() == CardType.DAYMUSHROOM ||card.getType() == CardType.NIGHTMUSHROOM || 
				card.getType() == CardType.BUTTER || card.getType() == CardType.CIDER) {
				EdibleItem item = (EdibleItem) card;
				if (card.getType() == CardType.NIGHTMUSHROOM) {
					this.score += item.getFlavourPoints() * 2;
				}
				else {
					this.score += item.getFlavourPoints();
				}
			}
		}
		
		if (panIndexInDisplay != -1)
			this.getDisplay().removeElement(panIndexInDisplay);
		return true;
	}
	
	public boolean sellMushrooms(String name, int number) {
		if (number < 2)
			return false;
		
		String canonicalName = name.replace(" ", "").replace("'", "").toLowerCase();
		ArrayList<Card> cardsToSell = new ArrayList<Card>();
		
		for (int i = 0; i < this.getHand().size(); i++) {
			Card card = this.getHand().getElementAt(i);
			if (card.getName().equals(canonicalName) && card.getType() == CardType.NIGHTMUSHROOM) {
				cardsToSell.add(card);
				number -= 2;
				if (number < 2)
					break;
			}
		}
		
		if (number > 0) {
			for (int i = 0; i < this.getHand().size(); i++) {
				Card card = this.getHand().getElementAt(i);
				if (card.getName().equals(canonicalName) && card.getType() == CardType.DAYMUSHROOM) {
					cardsToSell.add(card);
					number --;
					if (number == 0)
						break;
				}
			}
		}
		
		if (number > 0)
			return false;
		
		int sticksToAdd = 0;
		for (int i = 0; i < cardsToSell.size(); i++) {
			Card card = cardsToSell.get(i);
			for (int j = 0; j < this.getHand().size(); j++) {
				if (this.getHand().getElementAt(j).equals(card)) {
					this.getHand().removeElement(j);
					if (card.getType() == CardType.NIGHTMUSHROOM) {
						sticksToAdd += ((Mushroom)card).getSticksPerMushroom() * 2;
					}
					else {
						sticksToAdd += ((Mushroom)card).getSticksPerMushroom();
					}
				}
			}
		}
		
		this.addSticks(sticksToAdd);
		return true;
	}
	
	public boolean putPanDown() {
		for (int i = 0; i < this.getHand().size(); i++) {
			Card card = this.getHand().getElementAt(i);
			if (card.getType() == CardType.PAN) {
				this.getHand().removeElement(i);
				this.getDisplay().add(card);
				return true;
			}
		}
		return false;
	}
}
