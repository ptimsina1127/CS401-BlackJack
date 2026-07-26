package com.group5.blackjack;

import java.util.ArrayList;
import java.util.List;

public class Dealer {
	String name;
	List<Card> hand;
	double casinoFunds;
	double casinoBet;
	Table table;
	boolean hasBlackJack;

	public Dealer(String name, double funds) {
		this.name = name;
		this.casinoFunds = funds;
		casinoBet = 0;

		this.hand = new ArrayList<Card>();
	}

	public String getDealerName() {
		return name;
	}

	public List<Card> getDealerHand() {
		return hand;
	}

	public double getCasinoFunds() {
		return casinoFunds;
	}

	public double getBet() {
		return casinoBet;
	}

	public void setBet(double bet) {
		casinoBet = bet;
	}

	public void wonBet() {
		casinoFunds += casinoBet;
		casinoBet = 0;
	}

	public void lostBet() {
		casinoFunds -= casinoBet;
		casinoBet = 0;
	}

	public Table getTable() {
		return table;
	}

	public boolean doesTheDealerHaveBlackJack() {
		return hasBlackJack;
	}

	public int calculateHandTotal() {
		int handTotal = 0;
		boolean cardIsAce = false;

		for (int i = 0; i < hand.size(); i++) {
			int value = hand.get(i).getCardValue();
			if (value > 10) {
				value = 10;
			} else if (value == 1) {
				cardIsAce = true;
			}
			handTotal += value;
		}
		if (cardIsAce && handTotal + 10 <= 21) {
			handTotal += 10;
		}
		return handTotal;
	}

	public boolean peek() {
		if (hand.isEmpty()) return false;
		Card faceUpCard = hand.get(0);
		int value = faceUpCard.getCardValue();
		return value == 1 || value == 10;
	}

	public void clearHand() {
		hand.clear();
	}
	
	
	public String toStringDealersHand() {
		if (hand.size() < 1) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hand.size(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(hand.get(i).toString());
		}

		return sb.toString();
	}

	public void setHasBlackJack(boolean hasBlackJack) {
		this.hasBlackJack = hasBlackJack;
	}
	
}
