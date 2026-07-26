package com.group5.blackjack;

import java.io.*;
import java.net.Socket;
import javax.swing.SwingUtilities;

public class Client {
	private static final String SERVER_ADDRESS = "localhost";
	private static final int SERVER_PORT = 7777;

	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;
	private BlackjackGUI gui;
	private Socket socket;
	private volatile boolean running = false;
	private volatile boolean connected = false;
	private String userType;
	private String username;

	public Client(BlackjackGUI gui) {
		this.gui = gui;
	}

	public void connect() {
		new Thread(() -> {
			try {
				socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
				objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
				objectInputStream = new ObjectInputStream(socket.getInputStream());
				running = true;
				connected = true;
				System.out.println("Connected to server.");

				receiveLoop();
			} catch (IOException e) {
				System.err.println("Connection failed: " + e.getMessage());
				connected = false;
				SwingUtilities.invokeLater(() -> gui.onError("Could not connect to server."));
			}
		}).start();
	}

	private void receiveLoop() {
		while (running) {
			try {
				Message response = (Message) objectInputStream.readObject();
				if (response == null) break;
				handleResponse(response);
			} catch (IOException e) {
				if (running) {
					System.err.println("Connection lost: " + e.getMessage());
					SwingUtilities.invokeLater(() -> gui.onError("Lost connection to server."));
				}
				connected = false;
				running = false;
				break;
			} catch (ClassNotFoundException e) {
				System.err.println("Protocol error: " + e.getMessage());
			}
		}
		connected = false;
	}

	private void handleResponse(Message response) {
		SwingUtilities.invokeLater(() -> {
			switch (response.getType()) {
				case Login:
					gui.onLoginResult(response.getStatus() == Status.Success,
							response.getText());
					break;

				case Register:
					gui.onLoginResult(false,
							response.getStatus() == Status.Success
									? "registered"
									: response.getText());
					break;

				case ListGames:
					if (response.getStatus() == Status.Success) {
						gui.onGameListReceived(response.getText());
					} else {
						gui.onError(response.getText() != null ? response.getText() : "No games available.");
					}
					break;

				case ListPlayersOnline:
					if (response.getStatus() == Status.Success) {
						gui.onPlayerListReceived(response.getText());
					} else {
						gui.onError(response.getText() != null ? response.getText() : "No players online.");
					}
					break;

				case ListDealersOnline:
					if (response.getStatus() == Status.Success) {
						gui.onDealerListReceived(response.getText());
					} else {
						gui.onError(response.getText() != null ? response.getText() : "No dealers online.");
					}
					break;

				case ListPlayersInGame:
					if (response.getStatus() == Status.Success) {
						gui.onPlayersInGameReceived(response.getText());
					} else {
						gui.onError(response.getText() != null ? response.getText() : "No players in game.");
					}
					break;

				case OpenGame:
					if (response.getStatus() == Status.Success) {
						gui.onGameOpened(response.getText());
					} else {
						gui.onError(response.getText() != null ? response.getText() : "Could not open game.");
					}
					break;

				case CloseGame:
					if (response.getStatus() == Status.Success) {
						gui.onGameClosed(response.getText());
					} else {
						gui.onError(response.getText() != null ? response.getText() : "Could not close game.");
					}
					break;

				case JoinGame:
					if (response.getStatus() == Status.Success) {
						gui.onGameJoined(response.getText());
					} else {
						gui.onError(response.getText() != null ? response.getText() : "Could not join game.");
					}
					break;

				case LeaveGame:
					if (response.getStatus() == Status.Success) {
						gui.onGameLeft(response.getText());
					} else {
						gui.onError(response.getText() != null ? response.getText() : "Could not leave game.");
					}
					break;

				case QuickJoin:
					if (response.getStatus() == Status.Success) {
						gui.onGameJoined(response.getText());
					} else {
						gui.onError(response.getText() != null ? response.getText() : "No open games.");
					}
					break;

				case CheckFunds:
					if (response.getStatus() == Status.Success) {
						gui.onFundsReceived(response.getText());
					} else {
						gui.onError("Could not retrieve funds.");
					}
					break;

				case AddFunds:
					if (response.getStatus() == Status.Success) {
						gui.onFundsAdded(response.getText());
					} else {
						gui.onError(response.getText() != null ? response.getText() : "Could not add funds.");
					}
					break;

				case Bet:
					if (response.getStatus() == Status.Success) {
						gui.onRoundState(response.getText());
					} else {
						gui.onError(response.getText() != null ? response.getText() : "Bet failed.");
					}
					break;

				case HitOrStand:
					if (response.getStatus() == Status.Success) {
						gui.onRoundState(response.getText());
					} else {
						gui.onError(response.getText() != null ? response.getText() : "Action failed.");
					}
					break;

				case Logout:
					if (response.getStatus() == Status.Success) {
						connected = false;
						running = false;
						gui.onLoggedOut();
					}
					break;

				default:
					break;
			}
		});
	}

	public void sendLogin(String username, String password) {
		this.username = username;
		sendMessage(new Message(Type.Login, Status.New, username + ":" + password));
	}

	public void sendRegister(String username, String password) {
		this.username = username;
		sendMessage(new Message(Type.Register, Status.New, username + ":" + password));
	}

	public void sendLogout() {
		sendMessage(new Message(Type.Logout, Status.New, username));
	}

	public void sendOpenGame() {
		sendMessage(new Message(Type.OpenGame, Status.New, ""));
	}

	public void sendCloseGame(String gameId) {
		sendMessage(new Message(Type.CloseGame, Status.New, gameId));
	}

	public void sendJoinGame(String gameId) {
		sendMessage(new Message(Type.JoinGame, Status.New, gameId));
	}

	public void sendLeaveGame(String gameId) {
		sendMessage(new Message(Type.LeaveGame, Status.New, gameId));
	}

	public void sendQuickJoin() {
		sendMessage(new Message(Type.QuickJoin, Status.New, ""));
	}

	public void sendListGames() {
		sendMessage(new Message(Type.ListGames, Status.New, ""));
	}

	public void sendListPlayersOnline() {
		sendMessage(new Message(Type.ListPlayersOnline, Status.New, ""));
	}

	public void sendListDealersOnline() {
		sendMessage(new Message(Type.ListDealersOnline, Status.New, ""));
	}

	public void sendListPlayersInGame(String gameId) {
		sendMessage(new Message(Type.ListPlayersInGame, Status.New, gameId));
	}

	public void sendCheckFunds() {
		sendMessage(new Message(Type.CheckFunds, Status.New, username));
	}

	public void sendAddFunds(double amount) {
		sendMessage(new Message(Type.AddFunds, Status.New, username + ":" + amount));
	}

	public void sendBet(String gameId, double amount) {
		sendMessage(new Message(Type.Bet, Status.New, username + ":" + amount));
	}

	public void sendHit(String gameId) {
		sendMessage(new Message(Type.HitOrStand, Status.New, username + ":H"));
	}

	public void sendStand(String gameId) {
		sendMessage(new Message(Type.HitOrStand, Status.New, username + ":S"));
	}

	private void sendMessage(Message message) {
		if (!connected) {
			System.err.println("Not connected to server.");
			return;
		}
		try {
			if (objectOutputStream != null) {
				objectOutputStream.writeObject(message);
				objectOutputStream.flush();
			}
		} catch (IOException e) {
			System.err.println("Error sending message: " + e.getMessage());
			connected = false;
			SwingUtilities.invokeLater(() -> gui.onError("Lost connection to server."));
		}
	}

	public String getUserType() {
		return userType;
	}

	public String getUsername() {
		return username;
	}

	public boolean isConnected() {
		return connected;
	}

	public void disconnect() {
		running = false;
		connected = false;
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			// ignore
		}
	}
}
