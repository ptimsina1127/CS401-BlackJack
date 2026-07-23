package com.group5.blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TableTest {

	@Test
	void testTable() {
		Dealer dealer = new Dealer("Billy", 1000);
		List<Player> testPlayers = new ArrayList<Player>();
		Player testPlayer = new Player("Bob", 1000);
		testPlayers.add(testPlayer);

		Table table = new Table(dealer, testPlayers);
		assertNotNull(table.getPlayingDeck());
		assertEquals(1, table.getPlayers().size());
	}

	@Test
	void testShuffleCards() {
		Table table = new Table();
		table.shuffleCards();
		assertNotNull(table.getPlayingDeck());
	}

	@Test
	void testDeal() {
		Dealer dealer = new Dealer("Billy", 1000);
		List<Player> testPlayers = new ArrayList<Player>();
		testPlayers.add(new Player("Bob", 1000));
		Table table = new Table(dealer, testPlayers);

		Card card = table.deal();
		assertNotNull(card);
	}

	@Test
	void testAddCardToPlayerHand() {
		Player player = new Player("Bob", 1000);
		Card card = new Card(Suit.Hearts, Rank.Queen);
		assertEquals(0, player.getPlayerHand().size());
		player.getPlayerHand().add(card);
		assertEquals(1, player.getPlayerHand().size());
	}

	@Test
	void testAddCardToDealerHand() {
		Dealer dealer = new Dealer("Billy", 1000);
		Card card = new Card(Suit.Hearts, Rank.Queen);
		assertEquals(0, dealer.getDealerHand().size());
		dealer.getDealerHand().add(card);
		assertEquals(1, dealer.getDealerHand().size());
	}

	@Test
	void testClearAllHands() {
		Dealer dealer = new Dealer("Billy", 1000);
		List<Player> testPlayers = new ArrayList<Player>();
		Player player = new Player("Bob", 1000);
		testPlayers.add(player);

		Table table = new Table(dealer, testPlayers);

		player.getPlayerHand().add(new Card(Suit.Hearts, Rank.Ace));
		player.getPlayerHand().add(new Card(Suit.Spades, Rank.King));
		dealer.getDealerHand().add(new Card(Suit.Diamonds, Rank.Queen));

		assertEquals(2, player.getPlayerHand().size());
		assertEquals(1, dealer.getDealerHand().size());

		table.clearAllHands();
		assertEquals(0, player.getPlayerHand().size());
		assertEquals(0, dealer.getDealerHand().size());
	}

	@Test
	void testClearPlayerHand() {
		Player player = new Player("Bob", 1000);
		player.getPlayerHand().add(new Card(Suit.Hearts, Rank.Ace));
		player.getPlayerHand().add(new Card(Suit.Spades, Rank.King));

		Table table = new Table();
		table.clearPlayerHand(player);
		assertEquals(0, player.getPlayerHand().size());
	}

	@Test
	void testClearDealerHand() {
		Dealer dealer = new Dealer("Billy", 1000);
		dealer.getDealerHand().add(new Card(Suit.Hearts, Rank.Ace));

		Table table = new Table();
		table.clearDealerHand(dealer);
		assertEquals(0, dealer.getDealerHand().size());
	}
}
