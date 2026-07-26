# CS401-BlackJack

A multiplayer online Blackjack card game built in Java with a client-server architecture. Players connect to a casino server over TCP/IP, join tables, and play blackjack against a dealer (human or computer-controlled) with real-time card display and fund management.

## Features

- **Multiplayer Gameplay** - Multiple players connect to a single server and play at shared blackjack tables
- **Player & Dealer Roles** - Separate client interfaces for players (customers) and dealers (casino employees)
- **Computer Dealer Tables** - Pre-created "Casino Computer" tables available 24/7 for instant play
- **Card Image Display** - Visual card rendering using PNG card images (90x130 px) with caching
- **Dealer Hole Card Hidden** - Dealer's face-down card is hidden during the player's turn (standard blackjack)
- **Fund Management** - Players can add funds, cash out, and view transaction history
- **Quick Join** - One-click matchmaking to instantly join an open table
- **Blackjack Detection** - Automatic detection of natural blackjack with 3:2 payout
- **Bet Validation** - Client-side and server-side fund validation prevents betting with exhausted funds
- **Instant Fund Refresh** - Funds update immediately after each round and screen navigation

## Tech Stack

- **Language:** Java 17 (SE-17)
- **GUI:** Java Swing
- **Networking:** TCP/IP sockets with custom serialization protocol
- **Testing:** JUnit 5
- **Build:** Manual compilation (javac) or Eclipse/STS IDE

## Project Structure

```
CS401-BlackJack/
├── src/com/group5/blackjack/     # Source code (16 files)
│   ├── Server.java               # Main server entry point
│   ├── ClientHandler.java        # Per-client server-side handler
│   ├── Client.java               # Network client (sends/receives messages)
│   ├── BlackjackGUI.java         # Java Swing GUI (login, lobby, game table)
│   ├── Game.java                 # Game logic (bets, hands, results)
│   ├── Table.java                # Table with players, deck, and dealing
│   ├── Player.java               # Player model (hand, funds, bets)
│   ├── Dealer.java               # Dealer model (hand, auto-play)
│   ├── Card.java                 # Card model (suit, rank, display name)
│   ├── PlayingDeck.java          # Multi-deck shoe with shuffle/deal
│   ├── Deck.java                 # Single 52-card deck
│   ├── Message.java              # Protocol message (Type, Status, text)
│   ├── Date.java                 # Date utility for logging
│   ├── Rank.java                 # Card rank enum (ACE through KING)
│   ├── Suit.java                 # Card suit enum (SPADES, HEARTS, etc.)
│   └── TableStatus.java          # Table state enum (Open, Full, Closed)
├── tests/com/group5/blackjack/   # Unit tests (7 files)
├── assets/cards/                 # Card image PNGs (52 cards + back)
├── data/                         # Runtime data files
│   ├── players.txt               # Player accounts (username:password)
│   ├── dealers.txt               # Dealer accounts (username:password)
│   └── casinoFunds.txt           # Casino fund balance
├── docs/                         # Documentation
│   ├── SRS.md                    # Software Requirements Specification
│   ├── diagrams/                 # UML and sequence diagrams
│   └── icon.png                  # Application icon
└── ServerLogs.txt                # Server activity logs
```

## Prerequisites

- **Java Development Kit (JDK) 17** or later
- No external libraries required (uses only Java standard library)

## Setup & Running

### 1. Compile the source code

From the project root directory:

```bash
# Compile all source files
javac -d bin src/com/group5/blackjack/*.java
```

### 2. Start the server

```bash
java -cp bin com.group5.blackjack.Server
```

The server starts on **port 7777** and creates 3 computer dealer tables automatically.

### 3. Start a client

```bash
java -cp bin com.group5.blackjack.Client
```

Launch a separate client instance for each player or dealer. The GUI will open with a login screen.

### Default Accounts

| Role    | Username | Password |
|---------|----------|----------|
| Player  | luser1   | 1234     |
| Player  | luser2   | 1234     |
| Player  | luser3   | 1234     |
| Dealer  | dealer1  | 1234     |
| Dealer  | dealer2  | 1234     |

## How to Play

1. **Login** - Enter your username and password (or register a new account)
2. **Join a Table** - Use "Quick Join" to auto-match, or select a table from the game list
3. **Place a Bet** - Enter a bet amount and click "Place Bet" (must be within your available funds)
4. **Play Your Hand** - Choose to **Hit** (take a card) or **Stand** (keep your hand)
5. **Result** - Compare your hand to the dealer's; win, lose, or push determines your payout
6. **Repeat** - Place another bet to play again, or leave the table to return to the lobby

### Blackjack Rules Implemented

- Dealer must draw on 16 or less, stand on 17 or more
- Natural blackjack (Ace + 10-value card) pays 3:2
- Player bust (over 21) is an automatic loss
- Dealer bust means all remaining players win
- Cards 2-10 are face value; face cards (J, Q, K) are worth 10; Ace is 1 or 11

## Architecture

The system follows a three-tier architecture:

1. **Server** (`Server.java`) - Accepts TCP connections, spawns `ClientHandler` threads
2. **Client Handler** (`ClientHandler.java`) - Processes messages per client, manages game state
3. **Client** (`Client.java` + `BlackjackGUI.java`) - Sends actions, displays game state

All game logic runs server-side. Clients only send actions (Bet, Hit, Stand) and display the results sent back by the server.

## Documentation

- [Software Requirements Specification](docs/SRS.md)
- [UML Diagrams](docs/diagrams/)
