package com.group5.blackjack;

import java.util.ArrayList;
import java.util.List;

public class Game {
	private Table table;
	private Dealer dealer;
	private TableStatus tableStatus;
	private List<Player> lobby;
	private String timeStamp;

	private static int count = 1;
	private final String id;

	public Game(Dealer dealer, List<Player> lobby) {
		this.dealer = dealer;
		this.lobby = lobby;
		table = new Table(dealer, lobby);
		tableStatus = TableStatus.Open;
		this.timeStamp = new Date().getCurrentDate();
		this.id = String.valueOf(count++);
	}

	public Game() {
		Dealer nullDealer = new Dealer(" ", 0);
		this.dealer = nullDealer;
		this.lobby = new ArrayList<>();
		table = new Table();
		tableStatus = TableStatus.NeedDealer;
		this.timeStamp = new Date().getCurrentDate();
		this.id = String.valueOf(count++);
	}

	public Table getTable() {
		return table;
	}

	public Dealer getDealer() {
		return dealer;
	}

	public TableStatus getTableStatus() {
		return tableStatus;
	}

	public List<Player> getLobby() {
		return lobby;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public String getID() {
		return id;
	}

	public boolean isTableFull() {
		return tableStatus == TableStatus.Full;
	}

	public boolean isGameOpen() {
		return tableStatus == TableStatus.Open;
	}

	public void setDealer(Dealer dealer) {
		this.dealer = dealer;
		table.dealer = dealer;
		tableStatus = TableStatus.Open;
	}

	public void removePlayer(Player player) {
		if (tableStatus == TableStatus.Full) {
			tableStatus = TableStatus.Open;
		}
		table.clearPlayerHand(player);
		table.players.remove(player);
		lobby.remove(player);
	}

	public void removeDealer(Dealer dealer) {
		tableStatus = TableStatus.NeedDealer;
		table.clearDealerHand(dealer);
		table.dealer = null;
	}

	public void addPlayer(Player player) {
		if (tableStatus != TableStatus.Full && tableStatus != TableStatus.NeedDealer) {
			lobby.add(player);
			table.players.add(player);
			if (table.players.size() >= 7) {
				tableStatus = TableStatus.Full;
			}
		}
	}

	public void getBets(String message) {
		String[] serverMessages = message.split("\n");
		for (String playerBet : serverMessages) {
			String[] playerStats = playerBet.split(":");
			if (playerStats.length < 2) continue;
			String playerName = playerStats[0];
			double betAmount = Double.parseDouble(playerStats[1]);

			for (Player p : table.players) {
				if (p.getPlayerName().equals(playerName)) {
					if (betAmount > 0 && betAmount <= p.getPlayerFunds()) {
						p.setBet(betAmount);
					}
					break;
				}
			}
		}
	}

	public boolean checkBlackjack() {
		dealer.setHasBlackJack(false);

		if (dealer.calculateHandTotal() == 21 && dealer.getDealerHand().size() == 2) {
			dealer.setHasBlackJack(true);
			return true;
		}
		return false;
	}

	public String hitOrStand(String message) {
		String[] serverMessages = message.split("\n");
		StringBuilder result = new StringBuilder();

		for (String playerMessage : serverMessages) {
			String[] playerStats = playerMessage.split(":");
			if (playerStats.length < 2) continue;
			String playerName = playerStats[0];
			String choice = playerStats[1].toUpperCase();

			for (Player p : table.players) {
				if (p.getPlayerName().equals(playerName)) {
					if (choice.equals("H")) {
						table.addCardToPlayerHand(p, table.deal());
					}
					result.append(p.getPlayerName()).append(":")
						.append(p.toStringPlayersHand()).append(":")
						.append(p.calculateHandTotal()).append(":")
						.append(p.getPlayerFunds()).append(":")
						.append(p.getBet()).append("\n");
					break;
				}
			}
		}
		return result.toString();
	}

	public void settleBets() {
		int dealerTotal = dealer.calculateHandTotal();
		boolean dealerBusted = dealerTotal > 21;

		for (Player p : table.players) {
			if (p.getBet() <= 0) continue;

			int playerTotal = p.calculateHandTotal();

			if (playerTotal > 21) {
				p.lostBet();
			} else if (dealerBusted) {
				p.wonBet();
			} else if (playerTotal > dealerTotal) {
				p.wonBet();
			} else if (playerTotal < dealerTotal) {
				p.lostBet();
			} else {
				p.pushed();
			}
		}
	}

	public void dealerTurn() {
		while (dealer.calculateHandTotal() <= 16) {
			table.addCardToDealerHand(dealer, table.deck.dealACard());
		}
	}

	public String buildGameState(String forPlayer) {
		return buildGameState(forPlayer, false);
	}

	public String buildGameState(String forPlayer, boolean hideDealerHole) {
		StringBuilder sb = new StringBuilder();

		// Dealer info
		if (dealer != null && !dealer.getDealerHand().isEmpty()) {
			if (hideDealerHole && dealer.getDealerHand().size() >= 2) {
				sb.append("DEALER_HAND:").append(dealer.getDealerHand().get(0)).append(", ?\n");
				sb.append("DEALER_TOTAL:").append(dealer.getDealerHand().get(0).getCardValue()).append("\n");
			} else {
				sb.append("DEALER_HAND:").append(dealer.toStringDealersHand()).append("\n");
				sb.append("DEALER_TOTAL:").append(dealer.calculateHandTotal()).append("\n");
			}
		}

		// Find the specific player
		for (Player p : table.players) {
			if (p.getPlayerName().equals(forPlayer)) {
				if (!p.getPlayerHand().isEmpty()) {
					sb.append("PLAYER_HAND:").append(p.toStringPlayersHand()).append("\n");
					sb.append("PLAYER_TOTAL:").append(p.calculateHandTotal()).append("\n");
				}
				sb.append("PLAYER_BET:").append(p.getBet()).append("\n");
				sb.append("PLAYER_FUNDS:").append(p.getPlayerFunds()).append("\n");
				break;
			}
		}

		return sb.toString();
	}

	public String buildRoundResult() {
		StringBuilder sb = new StringBuilder();
		int dealerTotal = dealer.calculateHandTotal();
		boolean dealerBusted = dealerTotal > 21;

		sb.append("DEALER_HAND:").append(dealer.toStringDealersHand()).append("\n");
		sb.append("DEALER_TOTAL:").append(dealerTotal).append(dealerBusted ? " (BUST)" : "").append("\n");

		for (Player p : table.players) {
			int playerTotal = p.calculateHandTotal();
			String result;
			if (playerTotal > 21) {
				result = "BUST - Lost $" + String.format("%.2f", p.getBet());
			} else if (dealerBusted) {
				result = "WIN (Dealer Bust) +$" + String.format("%.2f", p.getBet());
			} else if (playerTotal > dealerTotal) {
				result = "WIN +$" + String.format("%.2f", p.getBet());
			} else if (playerTotal < dealerTotal) {
				result = "LOSE -$" + String.format("%.2f", p.getBet());
			} else {
				result = "PUSH";
			}
			sb.append(p.getPlayerName()).append(":").append(result).append("\n");
		}

		return sb.toString();
	}

	public void clearHands() {
		for (Player p : table.players) {
			p.clearHand();
		}
		if (dealer != null) {
			dealer.clearHand();
		}
	}
}
