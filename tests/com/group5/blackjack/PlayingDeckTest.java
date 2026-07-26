package com.group5.blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class PlayingDeckTest {

	@Test
	void testPlayingDeck() {
		int numOfDecks = 3;
		PlayingDeck playingDeck = new PlayingDeck(numOfDecks);
		assertNotNull(playingDeck);
		assertEquals(numOfDecks * 52, playingDeck.getRemainingCards());
	}

	@Test
	void testDealACard() {
		int numOfDecks = 3;
		PlayingDeck playingDeck = new PlayingDeck(numOfDecks);

		int before = playingDeck.getRemainingCards();
		Card card = playingDeck.dealACard();
		assertNotNull(card);
		assertEquals(before - 1, playingDeck.getRemainingCards());
	}

	@Test
	void testShuffleDeck() {
		int numOfDecks = 3;
		PlayingDeck playingDeck = new PlayingDeck(numOfDecks);

		playingDeck.dealACard();
		playingDeck.dealACard();
		playingDeck.dealACard();

		playingDeck.shuffleDeck();
		assertEquals(numOfDecks * 52, playingDeck.getRemainingCards());
	}
}
