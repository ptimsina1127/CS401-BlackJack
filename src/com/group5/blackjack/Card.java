package com.group5.blackjack;

public class Card {
	private Suit suit;
	private Rank rank;
	private int cardValue;

	public Card(Suit suit, Rank rank) {
		this.suit = suit;
		this.rank = rank;

		switch (this.rank) {
		case Two: {
			cardValue = 2;
			break;
		}
		case Three: {
			cardValue = 3;
			break;
		}
		case Four: {
			cardValue = 4;
			break;
		}
		case Five: {
			cardValue = 5;
			break;
		}
		case Six: {
			cardValue = 6;
			break;
		}
		case Seven: {
			cardValue = 7;
			break;
		}
		case Eight: {
			cardValue = 8;
			break;
		}
		case Nine: {
			cardValue = 9;
			break;
		}
		case Ten: {
			cardValue = 10;
			break;
		}
		case Ace: {
			cardValue = 1;
			break;
		}
		// King, Queen, and Jack should get the default case
		default:
			cardValue = 10;
			break;
		}
	}

	public Suit getSuit() {
		return suit;
	}

	public Rank getRank() {
		return rank;
	}

	public String toString() {
		return rank + " of " + suit;
	}

	public int getCardValue() {
		return cardValue;
	}

	public String getImageFilename() {
		String rankCode = switch (rank) {
			case Ace -> "A"; case Two -> "2"; case Three -> "3";
			case Four -> "4"; case Five -> "5"; case Six -> "6";
			case Seven -> "7"; case Eight -> "8"; case Nine -> "9";
			case Ten -> "10"; case Jack -> "J"; case Queen -> "Q";
			case King -> "K";
		};
		String suitCode = switch (suit) {
			case Hearts -> "H"; case Diamonds -> "D";
			case Clubs -> "C"; case Spades -> "S";
		};
		return rankCode + "-" + suitCode + ".png";
	}

	public static String getCardBackFilename() {
		return "back.png";
	}
}
