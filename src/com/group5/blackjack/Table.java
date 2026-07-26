package com.group5.blackjack;

import java.util.ArrayList;
import java.util.List;

public class Table {
	Dealer dealer;
	PlayingDeck deck;
	List<Player> players;
	double payout;

	public Table(Dealer dealer, List<Player> players) {
		this.dealer = dealer;
		deck = new PlayingDeck(3);

		this.players = new ArrayList<Player>();
		for (Player player : players) {
			this.players.add(new Player(player.getPlayerName(), player.getPlayerFunds()));
		}
		payout = 6.0 / 5.0;
	}

	public Table() {
		this.dealer = null;
		deck = new PlayingDeck(3);
		players = new ArrayList<>();
		payout = 6.0 / 5.0;
	}

	public String getPlayingDeck() {
		return deck.toString();
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void shuffleCards() {
		deck.shuffleDeck();
	}

	public void dealCards() {
		for (int i = 0; i < 2; i++) {
			for (Player player : players) {
				addCardToPlayerHand(player, deal());
			}
			addCardToDealerHand(dealer, deal());
		}
	}

	public Card deal() {
		return deck.dealACard();
	}

	public void addCardToPlayerHand(Player player, Card card) {
		player.getPlayerHand().add(card);
	}

	public void addCardToDealerHand(Dealer dealer, Card card) {
		dealer.getDealerHand().add(card);
	}

	public void clearAllHands() {
		for (Player player : players) {
			player.clearHand();
		}
		if (dealer != null) {
			dealer.clearHand();
		}
	}

	public void clearPlayerHand(Player player) {
		player.clearHand();
	}

	public static int pickRandomIndex(int Min, int Max) {
		return (int) (Math.random() * (Max - Min)) + Min;
	}

	public void clearDealerHand(Dealer dealer) {
		dealer.clearHand();
	}
}
