package com.group5.blackjack;

//ClientHandler class

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
	private final Socket clientSocket;

	private static int count = 1;
    private final int id;
    
    private OutputStream outputStream;
    private ObjectOutputStream objectOutputStream;
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    
    // make a SINGLE generic type to hold either Player or Dealer
    private Player playerUser;
    private Dealer dealerUser;
    private Game usersGame;

	// Constructor
	public ClientHandler(Socket socket) throws IOException
	{
		this.clientSocket = socket;
		this.id = count++;
		
		this.outputStream = this.clientSocket.getOutputStream();
		this.objectOutputStream = new ObjectOutputStream(this.outputStream);
		
		this.inputStream = this.clientSocket.getInputStream();
		this.objectInputStream = new ObjectInputStream(this.inputStream);
	}

	public void run()
	{
		try {
			System.out.println(Server.getServerName());
	    	System.out.println(Server.getCasinoFunds());
	    	System.out.println(Server.getValidDealers());
	    	System.out.println(Server.getOnlineDealers());
	    	System.out.println(Server.getValidPlayers());
	    	System.out.println(Server.getOnlinePlayers());
	    	System.out.println("end of server details anything beyond is past respondToClient()\n\n");
	        
	        // Login loop — allow retries until successful or client disconnects.
	        Message login = (Message) objectInputStream.readObject();
	        login = validateUser(login);

	        while (login.getStatus() != Status.Success) {
	            objectOutputStream.writeObject(login);
	            objectOutputStream.flush();
	            logMessage(login);

	            // Wait for next login attempt from client
	            login = (Message) objectInputStream.readObject();
	            login = validateUser(login);
	        }

	        // Login succeeded — send success response and enter main loop
	        objectOutputStream.writeObject(login);
	        objectOutputStream.flush();
	        logMessage(login);

			// Keep reading for messages until we get a logout message.
			Message request = (Message) objectInputStream.readObject();
			
			while (!isLogginOut(request)) {
				
				// Respond back to Client's request with an updated message.
				respondToClient(request);

				// Get another message from the client
				// In the future this might change to a List of Message.
				request = (Message) objectInputStream.readObject();
			}

			// Don't forget to close the client durr.
			clientSocket.close();
		}
		catch (IOException e) {
			System.out.println("Client disconnected: " + e.getMessage());
		} 
		catch (ClassNotFoundException e) {
			System.out.println("Protocol error: " + e.getMessage());
		}
		finally {
			// Always clean up the socket
			try {
				if (clientSocket != null && !clientSocket.isClosed()) {
					clientSocket.close();
				}
			} catch (IOException e) {
				// ignore
			}
		}
	}

	public int getClientID() {
		return id;
	}
	
	private Boolean isNewLogin(Message login) {
		
		// The message is valid if of Type Login and has a Status of New.
		if(login.getType() == Type.Login && login.getStatus() == Status.New) {
			return true;
		}
		
		return false;
	}
	
	
	// Checks the message to see if a Client is requesting to logout and then
	// updates the message.
	// Used to break out of the ClientHandler.
	private Boolean isLogginOut(Message msg) throws IOException {
		
		// If the message is of Type Logout and New return TRUE.
		if(msg.getType() == Type.Logout && msg.getStatus() == Status.New) {
			
			// Acknowledge logout Message
			msg.setStatus(Status.Success);
			
			// Username is supplied in the message. Log user out of the Server.
			logoutUser(msg.getText());
			
			// Print message to the terminal (make a log of what happened).
			logMessage(msg);
			
			// Send updated LOGOUT message back to the client
			objectOutputStream.writeObject(msg);

			return true;
		}		

		// Else this message is not a logout message. Proceed to process the
		// message accordingly.
		return false;
	}
	
	
	// A Dealer or Player name is given and is removed from Server if valid.
	private void logoutUser(String user) {
		
		Player player = Server.getTargetPlayer(user);
		Dealer dealer = Server.getTargetDealer(user);
		
		// Remove Player
		if(player != null && dealer == null) {
			
			Server.getOnlinePlayers().remove(player);
			return;
		}
		
		// Remove Dealer
		if(dealer != null && player == null ) {
			
			Server.getOnlineDealers().remove(dealer);
		}		
	}

	
	private Message validateUser(Message login) {
		
        // If we get a NEW login message, check the text supplied in the
        // message and check the server account details in text file.
		if(isNewLogin(login)) {

	        // Login user will return a string as either a:
			// "dealer", "player" or "invalid".
        	String loginType = Server.loginUser(login.getText());
        	
        	// IF account details found Set status to success
	        if(loginType.equals("dealer") || loginType.equals("player")) {
	        	
	        	String details[] = login.getText().split(":");
	    		String username = details[0];
	        	
	    		// Set target dealer to the client handler.
	    		// Set Player to null.
	    		// Get the Dealer from Server.getTargetDealer();
	        	if(loginType.equals("dealer")) {
	        		
	        		dealerUser = Server.getTargetDealer(username);
	        		playerUser = null;
	        	}
	        	
	        	// Set target player to client handler.
	        	// Set Dealer to null;
	        	// Get the player from Server.getTargetPlayer();
	        	if(loginType.equals("player")) {
	        		
	        		playerUser = Server.getTargetPlayer(username);
	        		dealerUser = null;	
	        	}
	        	
	        	login.setStatus(Status.Success);
	        }
	        
	        // If neither player or dealer then the login is invalid
	        else {

	        	login.setStatus(Status.Failed);
	        }
	        
	        // Login should still contain the User Details.
	        login.setText(loginType);
		}
		// Print message to the terminal (make a log of what happened).
		logMessage(login);
		
		return login;		
	}


	// Prints a log to the terminal saying what was sent to the Client.
	// Also writes the log to a log file.
	private synchronized void logMessage(Message message) {
		
		Type request = message.getType();
		Status status = message.getStatus();
		String data = message.getText();
		String timeStamp = new Date().getCurrentDate();
		int id = getClientID();
		
		String toPrint = "Client# " + id + " <" + request + ">[" + status 
				   + "]:" + timeStamp + "\n" + data;
		
		// Client# id <type>[status]: timeStamp 
		// data
		System.out.println(toPrint);
		
		// Append to log file.
		try (FileWriter file = new FileWriter("ServerLogs.txt", true)) {
			
			file.append(toPrint + "\n");
			
		} catch (IOException e) {

			e.printStackTrace();
		}
	}


	// Updates the Message's Status to Success and sets whatever text in text
	// field.
	private void updateMessageSuccess(Message message, String text) {
		
		// Update the Status of the Message.
		message.setStatus(Status.Success);
		
		// Update the text area.
		message.setText(text);
	}
	
	
	// Updates the Message's Status to Success and sets whatever text in text
	// field.
	private void updateMessageFailed(Message message, String text) {
		
		// Update the Status of the Message.
		message.setStatus(Status.Failed);
		
		// Update the text area.
		message.setText(text);
	}
	
	
	// A general send Message back to Client function.
	// A Message request is supplied by the Client and gets handled by the 
	// message handler. Then updates the message accordingly and sends the 
	// response back to the Client.
	private void respondToClient(Message message) throws IOException {
		try {

			// Only brand new Message's with a Status of New will get handled.
			if(message.getStatus() == Status.New) {
				
				// If its a new message then handle that request from the Client
				handleMessage(message);
			}
			
			// If its not a brand New Message than its an invalid request from 
			// the Client.
			else {

				updateMessageFailed(message, 
						"Invalid Request from the Client!");
			}
			
			// Print message to the terminal (make a log of what happened).
			logMessage(message);
			
			// Send acknowledgment back to the client.
			objectOutputStream.writeObject(message);
			
	
		} catch (IOException e) {
			
			System.out.println("Something Borked! Closing socket!\n");
			clientSocket.close();
			
			e.printStackTrace();
		}

	}
	
	
	// Message handler
	//
	// Switch to handle all the various types of messages.
	// Controlled by the Message's Type.
	// Message's request data is supplied in the Message text field. A servers
	// action should be tied to the Message Type and data associated in the text
	// area.
	//
	private void handleMessage(Message message) {

		// Build out the functions as needed and remember to update
		// the message before sending to the Client.
		//
		switch(message.getType()) {
			
			// Creates a new Player on the server. Details supplied in message.
			case Register:
				registerUser(message);
				break;
			
			// Sends a list of all Games on the server.
			case ListGames:
				listGames(message);
				break;
			
			// Sends a list of all online Players on the Server.
			case ListPlayersOnline:
				listPlayersOnline(message);
				break;
				
			// Sends a list of all online Dealers on the Server.
			case ListDealersOnline:
				listDealersOnline(message);
				break;
				
			// Sends a list of all Players in a Game by its Game ID.
			case ListPlayersInGame:
				listPlayersInGame(message);
				break;
				
			// Opens a new game on the Server and returns the new Game ID.
			case OpenGame:
				openGame(message);
				break;
				
			// Closes a Game on the Server using a Game ID from the Client.
			case CloseGame:
				closeGame(message);
				break;
			
			// Player/Dealer is added to game. Client supplies the Game's ID.
			case JoinGame:
				joinGame(message);
				break;
			
			// Player/Dealer is removed from game. Client supplies Games' ID.
			case LeaveGame:
				leaveGame(message);
				break;
				
			// Player joins the first available game.
			case QuickJoin:
				quickJoin(message);
				break;
				
			// A Player wants to see their funds. A Dealer the Casino's funds.
			case CheckFunds:
				checkFunds(message);
				break;
				
			// A player wants to add funds.
			case AddFunds:
				addFunds(message);
				break;		
			
			// All Players places their bets for a round of Blackjack. 
			// Starts a round of blackjack.
			case Bet:
			case HitOrStand:
				roundOfBlackjack(message);
				break;
			
				
				
			// DO NOTHING
			default:
				break;
		}
	}
	
	
	// When the dealer wants to start a game of Blackjack in a game. The client
	// will request that action by sending a request of Type Bet.
	// This will start a round in the Server.
	private void roundOfBlackjack(Message message) {
		
		if(usersGame == null) {
			updateMessageFailed(message, "Not in a game!");
			return;
		}

		if(message.getType() == Type.Bet) {
			usersGame.getTable().shuffleCards();
			usersGame.getBets(message.getText());
			usersGame.getTable().dealCards();
			
			boolean dealerBlackjack = usersGame.checkBlackjack();
			
			String playerIdent = "";
			if (playerUser != null) {
				playerIdent = playerUser.getPlayerName();
			} else if (dealerUser != null) {
				playerIdent = dealerUser.getDealerName();
			}
			
			// Build state with dealer hole card hidden during player turn
			String state = usersGame.buildGameState(playerIdent, true);
			
			if (dealerBlackjack) {
				// Build result BEFORE settling bets (bet amounts needed for display)
				String results = usersGame.buildRoundResult();
				state += results;
				state += extractPlayerResult(results, playerIdent);
				state += "ROUND_OVER:true\n";
				usersGame.settleBets();
				usersGame.clearHands();
			} else {
				boolean playerHasNatural = false;
				Player naturalPlayer = null;
				for (Player p : usersGame.getTable().getPlayers()) {
					if (p.calculateHandTotal() == 21 && p.getPlayerHand().size() == 2) {
						if (p.getPlayerName().equals(playerIdent)) {
							playerHasNatural = true;
							naturalPlayer = p;
						}
					}
				}
				
				if (playerHasNatural) {
					// 3:2 payout for natural blackjack
					naturalPlayer.hasBlackjack();
					// Build result BEFORE settling remaining bets
					String results = usersGame.buildRoundResult();
					state += results;
					state += "RESULT:You have Blackjack!\n";
					state += "ROUND_OVER:true\n";
					usersGame.clearHands();
				} else {
					state += "YOUR_TURN:true\n";
					state += "STATUS:Your turn - Hit or Stand\n";
				}
			}
			
			updateMessageSuccess(message, state);
		}

		if(message.getType() == Type.HitOrStand) {
			String hitResult = usersGame.hitOrStand(message.getText());
			
			String playerIdent = "";
			if (playerUser != null) {
				playerIdent = playerUser.getPlayerName();
			} else if (dealerUser != null) {
				playerIdent = dealerUser.getDealerName();
			}
			
			String playerPart = "";
			String[] hitLines = hitResult.split("\n");
			for (String line : hitLines) {
				String[] parts = line.split(":");
				if (parts.length >= 1 && parts[0].equals(playerIdent)) {
					playerPart = line;
					break;
				}
			}
			
			boolean playerBusted = false;
			if (!playerPart.isEmpty()) {
				String[] pParts = playerPart.split(":");
				if (pParts.length >= 3) {
					int total = Integer.parseInt(pParts[2].trim());
					playerBusted = total > 21;
				}
			}
			
			boolean allDone = playerBusted;
			
			if (!allDone) {
				String choice = message.getText().split(":")[1].toUpperCase().trim();
				if (choice.equals("S")) {
					allDone = true;
				}
			}
			
			if (allDone) {
				usersGame.dealerTurn();
				
				// Build result BEFORE settling bets (bet amounts needed for display)
				String state = usersGame.buildGameState(playerIdent, false);
				String results = usersGame.buildRoundResult();
				state += results;
				state += extractPlayerResult(results, playerIdent);
				state += "ROUND_OVER:true\n";
				usersGame.settleBets();
				usersGame.clearHands();
				updateMessageSuccess(message, state);
			} else {
				// Hide dealer hole card during player's turn
				String state = usersGame.buildGameState(playerIdent, true);
				state += "YOUR_TURN:true\n";
				state += "STATUS:Your turn - Hit or Stand\n";
				updateMessageSuccess(message, state);
			}
		}
	}

	// Extract the requesting player's result from buildRoundResult output
	// and format it as a RESULT: line for the GUI.
	private String extractPlayerResult(String results, String playerIdent) {
		for (String rLine : results.split("\n")) {
			if (rLine.startsWith(playerIdent + ":")) {
				String outcome = rLine.substring(rLine.indexOf(':') + 1).trim();
				return "RESULT:" + outcome + "\n";
			}
		}
		return "";
	}

	
	// The client supplies in a request with the users details. 
	// Server responds if the user has been registered or if the username
	// given is taken.
	//
	// username:password
	//
	// Return response to client with whatever Server.registerUser returns.
	private synchronized void registerUser(Message message){

		String status;
		
		try {
			
			status = Server.registerUser(message.getText());
						
			// If the user is already registered.
			if(status.equals("taken") ) {
				
				updateMessageFailed(message, "Username already taken!");
				return;
			}
			
			// If the user was registered.
			if(status.equals("registerd") ) {
				
				updateMessageSuccess(message, 
									 "You have registered to the Server!");
				return;
			}
			
			// If there is a wrong format supplied.
			updateMessageFailed(message, "Error");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}





	// Sends a String back to the client with a list of all the games on the
	// Server with some details.
	private void listGames(Message message) {
		
		// Get list of games from Server.getGames()
		// Iterate through the list.
		// For Each Games concat a string: 
		//
		// GameID:TableStatus:DealerName:NumberOfPlayers\n
		// GameID:TableStatus:DealerName:NumberOfPlayers
		//
		
		String gameListString = "";
		List<Game> gameList = Server.getGames();
		
		// If there are no game send back to the Client a Failed message.
		if(gameList == null || gameList.isEmpty()) {
			updateMessageFailed(message, "There are no active Games!");
			return;
		}
		
		Game lastGame = gameList.get(gameList.size() -1);
		
		for(Game g : gameList) {
			
			String ID = g.getID();
			TableStatus tableStatus = g.getTableStatus();
			String dealerName = g.getDealer().getDealerName();
			int numPlayers = g.getTable().getPlayers().size();
			
			if(dealerName.equals(" ")) {
				dealerName = "No Dealer";
			}
			
			gameListString += ID + ":" + tableStatus + ":" + dealerName + ":"
				    		  + numPlayers;
			
			// If at last game on the list, print without newline character.
			if(!g.equals(lastGame) ) {
				
				gameListString += "\n";
//								g.getID() + ":" 
//							    + g.getTableStatus() + ":"
//							    + g.getDealer().getDealerName() + ":"
//							    + g.getTable().getPlayers().size();
			}
			
			// Else add the details to the string WITH newline characters.
//			gameListString += g.getID() + ":" 
//							+ g.getTableStatus() + ":"
//							+ g.getDealer().getDealerName() + ":"
//							+ g.getTable().getPlayers().size() + ;
		}
		
		
		// Update the Status of the Message.
		// Update the text area with list of the games and details.
		updateMessageSuccess(message, gameListString);
	}
	
	//NEED TO FIX
	// PRINTS
	
	// Lists all Players online in the Server or nothing at all.
	private void listPlayersOnline(Message message) {

		String playersOnlineString = "";
		List<Player> playersOnline = Server.getOnlinePlayers();
		
		// If no Players online send a Success message back to the client.
		if(playersOnline == null) {
			updateMessageFailed(message, "There are no Players online!");
			return;
		}
		
		Player lastPlayer = playersOnline.get(playersOnline.size() -1);
	
		// Each player on the list gets printed.
		for(Player p : playersOnline) {
	
			// If at the last Player on the list print w/o the comma.
			if(p.equals(lastPlayer) ) {
				playersOnlineString += p.getPlayerName();

			}
			
			else {
				
				playersOnlineString += p.getPlayerName() + ",";
			}
		}
		
		// Update the Status of the Message.
		// Update the text area with list of the games and details.
		updateMessageSuccess(message, playersOnlineString);
	}
	
	
	// List all Dealers online in the Server.
	private void listDealersOnline(Message message) {

		String dealersOnlineString = "";
		List<Dealer> dealersOnline = Server.getOnlineDealers();
		
		if(dealersOnline == null || dealersOnline.size() == 0) {
			updateMessageFailed(message, "There are no Dealers online!");
			return;
		}
		
		for(int i = 0; i < dealersOnline.size(); i++) {
			if(i > 0) {
				dealersOnlineString += ",";
			}
			dealersOnlineString += dealersOnline.get(i).getDealerName();
		}
		
		updateMessageSuccess(message, dealersOnlineString);
	}
	
	
	// Needs to be renamed to what it really is.
	// Updates the Client with the state of a game in a game of Blackjack.
	//
	// Lists the Players within a certain game.
	// The text area should contain the game's ID that wants to display its 
	// players.
	//
	// The message will update the text area in the Message with a string with 
	// that game's players.
	private void listPlayersInGame(Message message) {
		
		// Get list of players.
		// Iterate through the list of players.
		// For each Player in the Game concat a string:
		//

		// DealerName:Card,...,Card:Funds\n
		// PlayerName:Card,...,Card:Funds:CurrentBet\n
		// PlayerName:Card,...,Card:Funds:CurrentBet

		//
		// Where Card,...,Card is the players hand.
		
		String listOfPlayers = "";
		String gameID = message.getText();
		
		Game game = Server.getTargetGame(gameID);
		
		// If there is no game by supplied ID or if there are no games.
	    // Check if game or table is null
	    if (game == null || game.getTable() == null) {
	    	
	        updateMessageFailed(message, "Game Not Found!");
	        return;
	    }	
	    
	    Dealer dealer = game.getDealer();
	    String dealerName = dealer.getDealerName();
	    String dealerHand = dealer.toStringDealersHand();
	    String dealerFunds = String.valueOf(dealer.getCasinoFunds());
	    
	    listOfPlayers += dealerName + ":" + dealerHand + ":" + dealerFunds 
	    				 + "\n";
	    
		List<Player> players = game.getTable().getPlayers();
		
	    // Check if players list is null or empty
	    if (players == null || players.isEmpty()) {
	        updateMessageFailed(message, "No Players Found!");
	        return;
	    }
		
		// If Player logs in there should be at least one player.
		// If no players and a dealer logs in should return nothing. 
		Player lastPlayer = players.get(players.size() -1);		
		
		for(Player p : players) {
			
			String name = p.getPlayerName();
			String hand = p.toStringPlayersHand();
			String funds = String.valueOf(p.getPlayerFunds());
			String bet = String.valueOf(p.getBet());
			//String hitOrStand = p.getHitOrStand();
			
			listOfPlayers += name + ":" + hand + ":" + funds + ":" + bet;
			
			// If at the last player on list, print without newline character.
			if(!p.equals(lastPlayer)) {
				listOfPlayers += "\n";
			}
		}
		
		// Update the Status of the Message.
		// Update the text area with list of the players and details.
		updateMessageSuccess(message, listOfPlayers);
	}
	

	// Opens/Creates a game and returns a Game ID.
	private void openGame(Message message) {

		Game newGame = new Game();
		Server.getGames().add(newGame);

		String gameID = newGame.getID();
		updateMessageSuccess(message, gameID);
	}
	
	
	// Closes the game with the supplied Game ID and returns the same ID.
	private void closeGame(Message message) {

		String gameID = message.getText();
		Game gameToRemove = Server.getTargetGame(gameID);
		
		// If we didn't find the game to remove.
		if(gameToRemove == null) {
			updateMessageFailed(message, "Game #" + gameID + "Not Found!");
			return;
		}
		
		// Else remove the game.
		Server.getGames().remove(gameToRemove);
		updateMessageSuccess(message, "Game #" + gameID + " has been Closed.");
	}		


	// The message will have the Game ID that the PlayerUser or DealerUser wants
	// to join.
	private synchronized void joinGame(Message message) {
		
		String gameID = message.getText();
		Game gameToJoin = Server.getTargetGame(gameID);
		
	    if (gameToJoin == null) {
	        //System.out.println("No game found with ID: " + gameID);
	        updateMessageFailed(message, "Game #" + gameID + " not found!");
	        return;
	    }
		
		
		// If a Dealer wants to join a game as a DEALER
		if(dealerUser != null && playerUser == null) {
			
			gameToJoin.setDealer(dealerUser);
			usersGame = gameToJoin;
			updateMessageSuccess(message, "Dealer joined Game #" + gameID);
		}
		
		// If a Player wants to join a game
		else if(playerUser != null && dealerUser == null) {
			gameToJoin.addPlayer(playerUser);
			usersGame = gameToJoin;
			updateMessageSuccess(message, "Player joined Game #" + gameID);
		}
		
		else {
			updateMessageFailed(message, "Game #" + gameID + "Not Found!");
		}
	}

	
	// The message will have the Game ID. A playerUser or dealerUser will be 
	// removed from that game.
	private void leaveGame(Message message) {
		
		String gameID = message.getText();
		Game gameToLeave = Server.getTargetGame(gameID);

		// If a Dealer wants to leave a game.
		if(dealerUser != null && playerUser == null) {
			gameToLeave.removeDealer(dealerUser);
			usersGame = null;
			updateMessageSuccess(message, dealerUser.name +" has left Game #"
								 + gameID);
			return;
		}
		
		// If a Player wants to leave a game.
		if(playerUser != null && dealerUser == null) {
			gameToLeave.removePlayer(playerUser);
			usersGame = null;
			updateMessageSuccess(message, playerUser.name +" has left Game #"
					 + gameID);
		}
		
		updateMessageFailed(message, "Invalid game request!");
	}
	
	// User join the first Open Game's Table.
	// Nothing is supplied by the message.
	// Return the Game's ID that the player has joined.
	private synchronized void quickJoin(Message message) {
		
		if (playerUser == null) {
			updateMessageFailed(message, "Only players can Quick Join.");
			return;
		}

		String gameID = "";
		List<Game> games = Server.getGames();
		
		// If there are no games send back to the Client a Failed message.
		if(games == null || games.isEmpty()) {
			updateMessageFailed(message, "There are no open Games!");
			return;
		}
		
		// For every game on the server.
		for(Game g : games) {
			
			// If the Table is Open, add the player to the game/table.
			if(g.getTableStatus() == TableStatus.Open) {
				
				gameID = g.getID();
				g.addPlayer(playerUser);
				usersGame = g;
				
				// Update the status of the Message as Success.
				// Send back the Client a Game's ID.
				updateMessageSuccess(message, gameID);
				break;
			}
		}

		if (gameID.isEmpty()) {
			updateMessageFailed(message, "There are no open Games!");
		}
	}
	
	
	// A Player wants to see their funds. A Dealer the Casino's funds.
	// The message is of Type CheckFunds and has the Player/Dealer name.
	// 
	private void checkFunds(Message message) {
		
		String username = message.getText();
		Player player = Server.getTargetPlayer(username);
		Dealer dealer = Server.getTargetDealer(username);
		String funds;
		
		// Then a player is checking their funds.
		if(player != null && dealer == null) {
			
			funds = String.valueOf(player.getPlayerFunds());
			updateMessageSuccess(message, funds);
			return;
		}
		
		// Then its a dealer checking the Casino's funds.
		else if(dealer != null && player == null) {
			
			funds = String.valueOf(Server.getCasinoFunds());
			updateMessageSuccess(message, funds);
			return;
		}
		
		updateMessageFailed(message, "");
	}
	
	
	// A player wants to add funds to their account by giving a their name and
	// how much
	//
	// username:0000
	//
	private synchronized void addFunds(Message message) {
		
		String request[] = message.getText().split(":");
		
		if(request.length != 2) {
			updateMessageFailed(message, "");
			return;
		}
		
		Player player = Server.getTargetPlayer(request[0]);
		Double fundsToAdd = Double.valueOf(request[1]);
		
		if(player == null) {
			updateMessageFailed(message, "");
			return;
		}
		
		// Just add the funds to the player.
		player.funds += fundsToAdd;
		updateMessageSuccess(message, "Funds added!");
	}
	
	public Player getPlayerUser() {
	    return playerUser;
	}

	public void setPlayerUser(Player playerUser) {
	    this.playerUser = playerUser;
	}
	
	
	
}
