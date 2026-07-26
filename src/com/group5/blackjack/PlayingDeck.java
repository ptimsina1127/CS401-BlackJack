package com.group5.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayingDeck {

	private List<Card> cards;
	private int nextCardIndex;
	private int numDecks;

	public PlayingDeck(int numOfDecks) {
		this.numDecks = numOfDecks;
		this.cards = new ArrayList<>();
		generatePlayingDeck(numOfDecks);
		Collections.shuffle(cards);
	}

	public void generatePlayingDeck(int numOfDecks) {
		cards.clear();
		for (int i = 0; i < numOfDecks; i++) {
			Deck deck = new Deck();
			cards.addAll(deck.getCards());
		}
	}

	public void shuffleDeck() {
		Collections.shuffle(cards);
		nextCardIndex = 0;
	}

	public String toString() {
		return "PlayingDeck [" + cards.size() + " cards, next index: " + nextCardIndex + "]";
	}

	public Card dealACard() {
		if (nextCardIndex >= cards.size()) {
			System.out.println("Deck exhausted, reshuffling...");
			shuffleDeck();
		}
		return cards.get(nextCardIndex++);
	}

	public int getRemainingCards() {
		return cards.size() - nextCardIndex;
	}
}
