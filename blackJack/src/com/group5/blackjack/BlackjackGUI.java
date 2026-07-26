package com.group5.blackjack;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BlackjackGUI {
	private JFrame frame;
	private CardLayout cardLayout;
	private JPanel cardPanel;
	private Client client;

	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton loginButton;
	private JButton registerButton;
	private JLabel loginStatusLabel;

	private JLabel welcomeLabel;
	private JLabel fundsLabel;
	private JLabel statusLabel;
	private JLabel dealerTotalLabel;
	private JPanel dealerCardPanel;
	private JPanel playerCardPanel;
	private JLabel playerTotalLabel;
	private JLabel playerFundsLabel;
	private JLabel playerBetLabel;
	private JLabel gameResultLabel;

	private JTextArea gameListArea;
	private JTextArea playerListArea;
	private JTextArea playersInGameArea;

	private JTextField gameIdField;
	private JTextField betField;
	private JTextField addFundsField;
	private JTextField closeGameIdField;
	private JTextField playersInGameIdField;

	private JButton hitButton;
	private JButton standButton;
	private JButton placeBetButton;

	private String currentGameId;
	private double currentFunds;
	private boolean isDealer;

	private static final int CARD_WIDTH = 90;
	private static final int CARD_HEIGHT = 130;
	private static final String CARDS_DIR = "Cards/";
	private static final Map<String, ImageIcon> imageCache = new HashMap<>();

	public BlackjackGUI() {
		initializeGUI();
	}

	private void initializeGUI() {
		frame = new JFrame("BLACKJACK");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 700);

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);

		initializeLoginPanel();
		initializePlayerGamePanel();
		initializeDealerGamePanel();
		initializeBlackjackTablePanel();
		initializeGameListPanel();
		initializePlayerListPanel();
		initializePlayersInGamePanel();

		frame.add(cardPanel);
		frame.setVisible(true);
	}

	private void initializeLoginPanel() {
		JPanel loginPanel = new JPanel(new GridBagLayout());
		loginPanel.setBackground(new Color(20, 20, 20));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;

		// Card suit symbols as title decoration
		JLabel suitLabel = new JLabel("\u2660  \u2665  \u2666  \u2663");
		suitLabel.setForeground(new Color(200, 160, 50));
		suitLabel.setFont(new Font("Serif", Font.BOLD, 28));
		gbc.insets = new Insets(20, 0, 5, 0);
		loginPanel.add(suitLabel, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(5, 0, 0, 0);
		JLabel titleLabel = new JLabel("BLACKJACK");
		titleLabel.setForeground(new Color(255, 215, 0));
		titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
		loginPanel.add(titleLabel, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 25, 0);
		JLabel subtitleLabel = new JLabel("CASINO");
		subtitleLabel.setForeground(new Color(180, 140, 40));
		subtitleLabel.setFont(new Font("Serif", Font.PLAIN, 20));
		loginPanel.add(subtitleLabel, gbc);

		// Login form card
		JPanel formCard = new JPanel(new GridBagLayout());
		formCard.setBackground(new Color(40, 40, 40));
		formCard.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(100, 80, 30), 2),
			BorderFactory.createEmptyBorder(25, 30, 25, 30)
		));
		GridBagConstraints fc = new GridBagConstraints();
		fc.gridx = 0;
		fc.gridy = 0;
		fc.anchor = GridBagConstraints.WEST;
		fc.insets = new Insets(0, 0, 3, 0);

		JLabel userLabel = new JLabel("USERNAME");
		userLabel.setForeground(new Color(160, 160, 160));
		userLabel.setFont(new Font("Arial", Font.BOLD, 11));
		formCard.add(userLabel, fc);

		fc.gridy++;
		fc.insets = new Insets(0, 0, 12, 0);
		usernameField = new JTextField(22);
		usernameField.setPreferredSize(new Dimension(250, 32));
		usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
		usernameField.setBackground(new Color(55, 55, 55));
		usernameField.setForeground(Color.WHITE);
		usernameField.setCaretColor(Color.WHITE);
		usernameField.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(80, 80, 80)),
			BorderFactory.createEmptyBorder(5, 8, 5, 8)
		));
		formCard.add(usernameField, fc);

		fc.gridy++;
		fc.insets = new Insets(0, 0, 3, 0);
		JLabel passLabel = new JLabel("PASSWORD");
		passLabel.setForeground(new Color(160, 160, 160));
		passLabel.setFont(new Font("Arial", Font.BOLD, 11));
		formCard.add(passLabel, fc);

		fc.gridy++;
		fc.insets = new Insets(0, 0, 18, 0);
		passwordField = new JPasswordField(22);
		passwordField.setPreferredSize(new Dimension(250, 32));
		passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
		passwordField.setBackground(new Color(55, 55, 55));
		passwordField.setForeground(Color.WHITE);
		passwordField.setCaretColor(Color.WHITE);
		passwordField.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(80, 80, 80)),
			BorderFactory.createEmptyBorder(5, 8, 5, 8)
		));
		formCard.add(passwordField, fc);

		// Login button
		fc.gridy++;
		fc.insets = new Insets(0, 0, 8, 0);
		fc.fill = GridBagConstraints.HORIZONTAL;
		loginButton = new JButton("LOGIN");
		loginButton.setPreferredSize(new Dimension(250, 38));
		loginButton.setFont(new Font("Arial", Font.BOLD, 15));
		loginButton.setBackground(new Color(0, 100, 200));
		loginButton.setForeground(Color.WHITE);
		loginButton.setFocusPainted(false);
		loginButton.setBorderPainted(false);
		loginButton.setOpaque(true);
		loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		loginButton.addActionListener(e -> attemptLogin());
		formCard.add(loginButton, fc);

		// Register button
		fc.gridy++;
		fc.insets = new Insets(0, 0, 10, 0);
		registerButton = new JButton("CREATE ACCOUNT");
		registerButton.setPreferredSize(new Dimension(250, 38));
		registerButton.setFont(new Font("Arial", Font.BOLD, 13));
		registerButton.setBackground(new Color(50, 50, 50));
		registerButton.setForeground(new Color(200, 180, 100));
		registerButton.setFocusPainted(false);
		registerButton.setBorder(BorderFactory.createLineBorder(new Color(100, 80, 30)));
		registerButton.setOpaque(true);
		registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		registerButton.addActionListener(e -> attemptRegister());
		formCard.add(registerButton, fc);

		// Inline status label for login feedback
		fc.gridy++;
		fc.insets = new Insets(0, 0, 0, 0);
		loginStatusLabel = new JLabel(" ");
		loginStatusLabel.setForeground(new Color(220, 80, 80));
		loginStatusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
		loginStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		formCard.add(loginStatusLabel, fc);

		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 0, 0);
		loginPanel.add(formCard, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(20, 0, 10, 0);
		JLabel footerLabel = new JLabel("Group 5 - CS401");
		footerLabel.setForeground(new Color(100, 100, 100));
		footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		loginPanel.add(footerLabel, gbc);

		cardPanel.add(loginPanel, "Login");
	}

	private void attemptLogin() {
		String username = usernameField.getText().trim();
		String password = new String(passwordField.getPassword()).trim();
		if (username.isEmpty() || password.isEmpty()) {
			loginStatusLabel.setText("Enter username and password.");
			return;
		}
		loginButton.setEnabled(false);
		registerButton.setEnabled(false);
		loginStatusLabel.setText("Connecting...");
		loginStatusLabel.setForeground(new Color(160, 160, 160));
		client.sendLogin(username, password);
	}

	private void attemptRegister() {
		String username = usernameField.getText().trim();
		String password = new String(passwordField.getPassword()).trim();
		if (username.isEmpty() || password.isEmpty()) {
			loginStatusLabel.setText("Enter username and password.");
			return;
		}
		loginButton.setEnabled(false);
		registerButton.setEnabled(false);
		loginStatusLabel.setText("Registering...");
		loginStatusLabel.setForeground(new Color(160, 160, 160));
		client.sendRegister(username, password);
	}

	private void setLoginButtonsEnabled(boolean enabled) {
		loginButton.setEnabled(enabled);
		registerButton.setEnabled(enabled);
	}

	private void initializePlayerGamePanel() {
		JPanel gamePanel = new JPanel(new GridBagLayout());
		gamePanel.setBackground(new Color(0, 102, 0));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 10, 5, 10);

		welcomeLabel = new JLabel("Welcome to BLACKJACK!", SwingConstants.CENTER);
		welcomeLabel.setForeground(Color.WHITE);
		welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
		gbc.gridwidth = 2;
		gamePanel.add(welcomeLabel, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 10, 0);
		fundsLabel = new JLabel("Funds: $0.00", SwingConstants.CENTER);
		fundsLabel.setForeground(Color.YELLOW);
		fundsLabel.setFont(new Font("Arial", Font.BOLD, 20));
		gamePanel.add(fundsLabel, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.gridwidth = 1;

		JButton quickJoinButton = createStyledButton("Quick Join", new Color(0, 123, 255));
		quickJoinButton.addActionListener(e -> client.sendQuickJoin());
		gamePanel.add(quickJoinButton, gbc);

		gbc.gridx = 1;
		JButton viewGamesButton = createStyledButton("View Games", new Color(0, 123, 255));
		viewGamesButton.addActionListener(e -> {
			client.sendListGames();
			cardLayout.show(cardPanel, "Game List");
		});
		gamePanel.add(viewGamesButton, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		JButton viewPlayersButton = createStyledButton("View Players", new Color(0, 123, 255));
		viewPlayersButton.addActionListener(e -> {
			client.sendListPlayersOnline();
			cardLayout.show(cardPanel, "Player List");
		});
		gamePanel.add(viewPlayersButton, gbc);

		gbc.gridx = 1;
		JButton checkFundsButton = createStyledButton("Check Funds", new Color(0, 123, 255));
		checkFundsButton.addActionListener(e -> client.sendCheckFunds());
		gamePanel.add(checkFundsButton, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(5, 50, 5, 50);
		JPanel addFundsPanel = new JPanel(new FlowLayout());
		addFundsPanel.setBackground(new Color(0, 102, 0));
		addFundsField = new JTextField(8);
		JButton addFundsButton = createStyledButton("Add Funds", new Color(40, 167, 69));
		addFundsButton.addActionListener(e -> {
			try {
				double amount = Double.parseDouble(addFundsField.getText().trim());
				if (amount > 0) {
					client.sendAddFunds(amount);
				}
			} catch (NumberFormatException ex) {
				onError("Enter a valid amount.");
			}
		});
		addFundsPanel.add(addFundsField);
		addFundsPanel.add(addFundsButton);
		gamePanel.add(addFundsPanel, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 10, 0);
		gbc.gridwidth = 2;
		JButton logoutButton = createStyledButton("LOGOUT", new Color(220, 53, 69));
		logoutButton.addActionListener(e -> client.sendLogout());
		gamePanel.add(logoutButton, gbc);

		cardPanel.add(gamePanel, "Player Game");
	}

	private void initializeDealerGamePanel() {
		JPanel dealerPanel = new JPanel(new GridBagLayout());
		dealerPanel.setBackground(new Color(0, 102, 0));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 10, 5, 10);

		JLabel dealerWelcomeLabel = new JLabel("DEALER DASHBOARD", SwingConstants.CENTER);
		dealerWelcomeLabel.setForeground(Color.WHITE);
		dealerWelcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
		gbc.gridwidth = 2;
		dealerPanel.add(dealerWelcomeLabel, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 10, 0);
		JLabel dealerNameLabel = new JLabel("", SwingConstants.CENTER);
		dealerNameLabel.setForeground(Color.YELLOW);
		dealerNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
		dealerPanel.add(dealerNameLabel, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.gridwidth = 1;

		JButton openGameButton = createStyledButton("Open Game", new Color(40, 167, 69));
		openGameButton.addActionListener(e -> client.sendOpenGame());
		dealerPanel.add(openGameButton, gbc);

		gbc.gridx = 1;
		JButton viewGamesButton = createStyledButton("View Games", new Color(0, 123, 255));
		viewGamesButton.addActionListener(e -> {
			client.sendListGames();
			cardLayout.show(cardPanel, "Game List");
		});
		dealerPanel.add(viewGamesButton, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(5, 50, 5, 50);
		JPanel closeGamePanel = new JPanel(new FlowLayout());
		closeGamePanel.setBackground(new Color(0, 102, 0));
		closeGameIdField = new JTextField(5);
		JButton closeGameButton = createStyledButton("Close Game", new Color(220, 53, 69));
		closeGameButton.addActionListener(e -> {
			String gameId = closeGameIdField.getText().trim();
			if (!gameId.isEmpty()) {
				client.sendCloseGame(gameId);
			}
		});
		closeGamePanel.add(new JLabel("Game ID: ") {{ setForeground(Color.WHITE); }});
		closeGamePanel.add(closeGameIdField);
		closeGamePanel.add(closeGameButton);
		dealerPanel.add(closeGamePanel, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.gridwidth = 1;
		gbc.gridx = 0;

		JButton viewPlayersOnline = createStyledButton("Players Online", new Color(0, 123, 255));
		viewPlayersOnline.addActionListener(e -> {
			client.sendListPlayersOnline();
			cardLayout.show(cardPanel, "Player List");
		});
		dealerPanel.add(viewPlayersOnline, gbc);

		gbc.gridx = 1;
		JButton viewDealersOnline = createStyledButton("Dealers Online", new Color(0, 123, 255));
		viewDealersOnline.addActionListener(e -> {
			client.sendListDealersOnline();
			cardLayout.show(cardPanel, "Player List");
		});
		dealerPanel.add(viewDealersOnline, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(5, 50, 5, 50);
		JPanel joinGamePanel = new JPanel(new FlowLayout());
		joinGamePanel.setBackground(new Color(0, 102, 0));
		gameIdField = new JTextField(5);
		JButton joinGameButton = createStyledButton("Join Game", new Color(0, 123, 255));
		joinGameButton.addActionListener(e -> {
			String gameId = gameIdField.getText().trim();
			if (!gameId.isEmpty()) {
				client.sendJoinGame(gameId);
			}
		});
		joinGamePanel.add(new JLabel("Game ID: ") {{ setForeground(Color.WHITE); }});
		joinGamePanel.add(gameIdField);
		joinGamePanel.add(joinGameButton);
		dealerPanel.add(joinGamePanel, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 10, 0);
		JButton logoutButton = createStyledButton("LOGOUT", new Color(220, 53, 69));
		logoutButton.addActionListener(e -> client.sendLogout());
		dealerPanel.add(logoutButton, gbc);

		cardPanel.add(dealerPanel, "Dealer Game");
	}

	private void initializeBlackjackTablePanel() {
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(new Color(0, 102, 0));

		// Dealer area
		JPanel dealerArea = new JPanel(new BorderLayout());
		dealerArea.setBackground(new Color(0, 80, 0));
		dealerArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		JLabel dealerTitle = new JLabel("DEALER");
		dealerTitle.setForeground(Color.WHITE);
		dealerTitle.setFont(new Font("Arial", Font.BOLD, 18));
		dealerArea.add(dealerTitle, BorderLayout.NORTH);

		dealerCardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		dealerCardPanel.setOpaque(false);
		JLabel dealerPlaceholder = new JLabel("No cards dealt");
		dealerPlaceholder.setForeground(Color.WHITE);
		dealerPlaceholder.setFont(new Font("Arial", Font.PLAIN, 16));
		dealerCardPanel.add(dealerPlaceholder);
		dealerArea.add(dealerCardPanel, BorderLayout.CENTER);

		dealerTotalLabel = new JLabel("Total: -");
		dealerTotalLabel.setForeground(Color.YELLOW);
		dealerTotalLabel.setFont(new Font("Arial", Font.BOLD, 16));
		dealerArea.add(dealerTotalLabel, BorderLayout.SOUTH);

		tablePanel.add(dealerArea, BorderLayout.NORTH);

		// Player area
		JPanel playerArea = new JPanel(new BorderLayout());
		playerArea.setBackground(new Color(0, 90, 0));
		playerArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		JPanel playerInfoPanel = new JPanel(new BorderLayout());
		playerInfoPanel.setOpaque(false);

		playerCardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		playerCardPanel.setOpaque(false);
		JLabel playerPlaceholder = new JLabel("Place your bet!");
		playerPlaceholder.setForeground(Color.WHITE);
		playerPlaceholder.setFont(new Font("Arial", Font.PLAIN, 16));
		playerCardPanel.add(playerPlaceholder);
		playerInfoPanel.add(playerCardPanel, BorderLayout.NORTH);

		JPanel playerStatsPanel = new JPanel(new GridLayout(3, 1));
		playerStatsPanel.setOpaque(false);

		playerTotalLabel = new JLabel("Total: -");
		playerTotalLabel.setForeground(Color.YELLOW);
		playerTotalLabel.setFont(new Font("Arial", Font.BOLD, 16));
		playerStatsPanel.add(playerTotalLabel);

		playerFundsLabel = new JLabel("Funds: $0.00");
		playerFundsLabel.setForeground(Color.GREEN);
		playerFundsLabel.setFont(new Font("Arial", Font.BOLD, 16));
		playerStatsPanel.add(playerFundsLabel);

		playerBetLabel = new JLabel("Bet: $0.00");
		playerBetLabel.setForeground(Color.ORANGE);
		playerBetLabel.setFont(new Font("Arial", Font.BOLD, 16));
		playerStatsPanel.add(playerBetLabel);

		playerInfoPanel.add(playerStatsPanel, BorderLayout.CENTER);

		playerArea.add(playerInfoPanel, BorderLayout.CENTER);

		// Game result label
		gameResultLabel = new JLabel("", SwingConstants.CENTER);
		gameResultLabel.setForeground(Color.WHITE);
		gameResultLabel.setFont(new Font("Arial", Font.BOLD, 20));
		playerArea.add(gameResultLabel, BorderLayout.SOUTH);

		tablePanel.add(playerArea, BorderLayout.CENTER);

		// Action area
		JPanel actionArea = new JPanel(new BorderLayout());
		actionArea.setBackground(new Color(0, 60, 0));
		actionArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		// Bet controls
		JPanel betPanel = new JPanel(new FlowLayout());
		betPanel.setOpaque(false);
		betField = new JTextField(6);
		placeBetButton = createStyledButton("Place Bet", new Color(255, 193, 7));
		placeBetButton.addActionListener(e -> {
			try {
				double amount = Double.parseDouble(betField.getText().trim());
				if (amount <= 0) {
					onError("Bet amount must be greater than zero.");
					return;
				}
				if (currentFunds <= 0) {
					onError("No funds remaining. Go back and add funds to continue playing.");
					return;
				}
				if (amount > currentFunds) {
					onError("Insufficient funds. You have $" + String.format("%.2f", currentFunds) + ".");
					return;
				}
				if (currentGameId != null) {
					client.sendBet(currentGameId, amount);
					betField.setText("");
				}
			} catch (NumberFormatException ex) {
				onError("Enter a valid bet amount.");
			}
		});
		betPanel.add(new JLabel("Bet: ") {{ setForeground(Color.WHITE); }});
		betPanel.add(betField);
		betPanel.add(placeBetButton);
		actionArea.add(betPanel, BorderLayout.NORTH);

		// Hit/Stand buttons
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setOpaque(false);

		hitButton = createStyledButton("HIT", new Color(40, 167, 69));
		hitButton.addActionListener(e -> {
			if (currentGameId != null) {
				client.sendHit(currentGameId);
			}
		});
		hitButton.setEnabled(false);
		buttonPanel.add(hitButton);

		standButton = createStyledButton("STAND", new Color(220, 53, 69));
		standButton.addActionListener(e -> {
			if (currentGameId != null) {
				client.sendStand(currentGameId);
			}
		});
		standButton.setEnabled(false);
		buttonPanel.add(standButton);

		JButton backToLobbyButton = createStyledButton("Back to Lobby", new Color(108, 117, 125));
		backToLobbyButton.addActionListener(e -> {
			if (currentGameId != null) {
				client.sendLeaveGame(currentGameId);
				currentGameId = null;
			}
			showGamePanel();
		});
		buttonPanel.add(backToLobbyButton);

		actionArea.add(buttonPanel, BorderLayout.CENTER);

		// Status
		statusLabel = new JLabel("Waiting to start...", SwingConstants.CENTER);
		statusLabel.setForeground(Color.WHITE);
		statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
		actionArea.add(statusLabel, BorderLayout.SOUTH);

		tablePanel.add(actionArea, BorderLayout.SOUTH);

		cardPanel.add(tablePanel, "BlackjackTable");
	}

	private void initializeGameListPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(32, 32, 32));

		JLabel title = new JLabel("Game List", SwingConstants.CENTER);
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Arial", Font.BOLD, 20));
		title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		panel.add(title, BorderLayout.NORTH);

		gameListArea = new JTextArea();
		gameListArea.setEditable(false);
		gameListArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		gameListArea.setBackground(new Color(48, 48, 48));
		gameListArea.setForeground(Color.WHITE);
		JScrollPane scrollPane = new JScrollPane(gameListArea);
		panel.add(scrollPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout());
		bottomPanel.setBackground(new Color(32, 32, 32));

		JButton refreshButton = createStyledButton("Refresh", new Color(0, 123, 255));
		refreshButton.addActionListener(e -> client.sendListGames());
		bottomPanel.add(refreshButton);

		JPanel joinPanel = new JPanel(new FlowLayout());
		joinPanel.setOpaque(false);
		JTextField joinGameIdField = new JTextField(5);
		JButton joinButton = createStyledButton("Join", new Color(40, 167, 69));
		joinButton.addActionListener(e -> {
			String gameId = joinGameIdField.getText().trim();
			if (!gameId.isEmpty()) {
				client.sendJoinGame(gameId);
			}
		});
		joinPanel.add(new JLabel("Game ID: ") {{ setForeground(Color.WHITE); }});
		joinPanel.add(joinGameIdField);
		joinPanel.add(joinButton);
		bottomPanel.add(joinPanel);

		JButton backButton = createStyledButton("Back", new Color(108, 117, 125));
		backButton.addActionListener(e -> showGamePanel());
		bottomPanel.add(backButton);

		panel.add(bottomPanel, BorderLayout.SOUTH);

		cardPanel.add(panel, "Game List");
	}

	private void initializePlayerListPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(32, 32, 32));

		JLabel title = new JLabel("Players Online", SwingConstants.CENTER);
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Arial", Font.BOLD, 20));
		title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		panel.add(title, BorderLayout.NORTH);

		playerListArea = new JTextArea();
		playerListArea.setEditable(false);
		playerListArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		playerListArea.setBackground(new Color(48, 48, 48));
		playerListArea.setForeground(Color.WHITE);
		JScrollPane scrollPane = new JScrollPane(playerListArea);
		panel.add(scrollPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout());
		bottomPanel.setBackground(new Color(32, 32, 32));

		JButton refreshButton = createStyledButton("Refresh", new Color(0, 123, 255));
		refreshButton.addActionListener(e -> client.sendListPlayersOnline());
		bottomPanel.add(refreshButton);

		JButton backButton = createStyledButton("Back", new Color(108, 117, 125));
		backButton.addActionListener(e -> showGamePanel());
		bottomPanel.add(backButton);

		panel.add(bottomPanel, BorderLayout.SOUTH);

		cardPanel.add(panel, "Player List");
	}

	private void initializePlayersInGamePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(32, 32, 32));

		JLabel title = new JLabel("Players in Game", SwingConstants.CENTER);
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Arial", Font.BOLD, 20));
		title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		panel.add(title, BorderLayout.NORTH);

		playersInGameArea = new JTextArea();
		playersInGameArea.setEditable(false);
		playersInGameArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		playersInGameArea.setBackground(new Color(48, 48, 48));
		playersInGameArea.setForeground(Color.WHITE);
		JScrollPane scrollPane = new JScrollPane(playersInGameArea);
		panel.add(scrollPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout());
		bottomPanel.setBackground(new Color(32, 32, 32));

		playersInGameIdField = new JTextField(5);
		JButton viewButton = createStyledButton("View", new Color(0, 123, 255));
		viewButton.addActionListener(e -> {
			String gameId = playersInGameIdField.getText().trim();
			if (!gameId.isEmpty()) {
				client.sendListPlayersInGame(gameId);
			}
		});
		bottomPanel.add(new JLabel("Game ID: ") {{ setForeground(Color.WHITE); }});
		bottomPanel.add(playersInGameIdField);
		bottomPanel.add(viewButton);

		JButton backButton = createStyledButton("Back", new Color(108, 117, 125));
		backButton.addActionListener(e -> showGamePanel());
		bottomPanel.add(backButton);

		panel.add(bottomPanel, BorderLayout.SOUTH);

		cardPanel.add(panel, "Players In Game");
	}

	private JButton createStyledButton(String text, Color bgColor) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.BOLD, 14));
		button.setBackground(bgColor);
		button.setForeground(Color.WHITE);
		button.setPreferredSize(new Dimension(150, 35));
		return button;
	}

	// ========== Card image helpers ==========

	private void updateCardPanel(JPanel panel, String handString) {
		panel.removeAll();
		if (handString == null || handString.isEmpty()) {
			JLabel placeholder = new JLabel("No cards");
			placeholder.setForeground(Color.WHITE);
			placeholder.setFont(new Font("Arial", Font.PLAIN, 16));
			panel.add(placeholder);
			panel.revalidate();
			panel.repaint();
			return;
		}
		String[] cardNames = handString.split(",");
		for (String cardName : cardNames) {
			String trimmed = cardName.trim();
			if (trimmed.isEmpty()) continue;
			ImageIcon icon = loadCardImage(trimmed);
			JLabel cardLabel = new JLabel(icon);
			panel.add(cardLabel);
		}
		panel.revalidate();
		panel.repaint();
	}

	private void resetCardPanel(JPanel panel, String message) {
		panel.removeAll();
		JLabel placeholder = new JLabel(message);
		placeholder.setForeground(Color.WHITE);
		placeholder.setFont(new Font("Arial", Font.PLAIN, 16));
		panel.add(placeholder);
		panel.revalidate();
		panel.repaint();
	}

	private ImageIcon loadCardImage(String cardName) {
		String filename = cardNameToFilename(cardName);
		if (imageCache.containsKey(filename)) {
			return imageCache.get(filename);
		}
		File file = new File(CARDS_DIR + filename);
		if (!file.exists()) {
			file = new File(CARDS_DIR + Card.getCardBackFilename());
		}
		try {
			BufferedImage img = ImageIO.read(file);
			Image scaled = img.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
			ImageIcon icon = new ImageIcon(scaled);
			imageCache.put(filename, icon);
			return icon;
		} catch (IOException e) {
			System.err.println("Failed to load card image: " + file.getPath());
			return new ImageIcon();
		}
	}

	private static String cardNameToFilename(String cardName) {
		return switch (cardName) {
			case "Ace of Hearts" -> "A-H.png";
			case "Ace of Diamonds" -> "A-D.png";
			case "Ace of Clubs" -> "A-C.png";
			case "Ace of Spades" -> "A-S.png";
			case "Two of Hearts" -> "2-H.png";
			case "Two of Diamonds" -> "2-D.png";
			case "Two of Clubs" -> "2-C.png";
			case "Two of Spades" -> "2-S.png";
			case "Three of Hearts" -> "3-H.png";
			case "Three of Diamonds" -> "3-D.png";
			case "Three of Clubs" -> "3-C.png";
			case "Three of Spades" -> "3-S.png";
			case "Four of Hearts" -> "4-H.png";
			case "Four of Diamonds" -> "4-D.png";
			case "Four of Clubs" -> "4-C.png";
			case "Four of Spades" -> "4-S.png";
			case "Five of Hearts" -> "5-H.png";
			case "Five of Diamonds" -> "5-D.png";
			case "Five of Clubs" -> "5-C.png";
			case "Five of Spades" -> "5-S.png";
			case "Six of Hearts" -> "6-H.png";
			case "Six of Diamonds" -> "6-D.png";
			case "Six of Clubs" -> "6-C.png";
			case "Six of Spades" -> "6-S.png";
			case "Seven of Hearts" -> "7-H.png";
			case "Seven of Diamonds" -> "7-D.png";
			case "Seven of Clubs" -> "7-C.png";
			case "Seven of Spades" -> "7-S.png";
			case "Eight of Hearts" -> "8-H.png";
			case "Eight of Diamonds" -> "8-D.png";
			case "Eight of Clubs" -> "8-C.png";
			case "Eight of Spades" -> "8-S.png";
			case "Nine of Hearts" -> "9-H.png";
			case "Nine of Diamonds" -> "9-D.png";
			case "Nine of Clubs" -> "9-C.png";
			case "Nine of Spades" -> "9-S.png";
			case "Ten of Hearts" -> "10-H.png";
			case "Ten of Diamonds" -> "10-D.png";
			case "Ten of Clubs" -> "10-C.png";
			case "Ten of Spades" -> "10-S.png";
			case "Jack of Hearts" -> "J-H.png";
			case "Jack of Diamonds" -> "J-D.png";
			case "Jack of Clubs" -> "J-C.png";
			case "Jack of Spades" -> "J-S.png";
			case "Queen of Hearts" -> "Q-H.png";
			case "Queen of Diamonds" -> "Q-D.png";
			case "Queen of Clubs" -> "Q-C.png";
			case "Queen of Spades" -> "Q-S.png";
			case "King of Hearts" -> "K-H.png";
			case "King of Diamonds" -> "K-D.png";
			case "King of Clubs" -> "K-C.png";
			case "King of Spades" -> "K-S.png";
			case "?" -> Card.getCardBackFilename();
			default -> Card.getCardBackFilename();
		};
	}

	private void showGamePanel() {
		if (isDealer) {
			cardLayout.show(cardPanel, "Dealer Game");
		} else {
			cardLayout.show(cardPanel, "Player Game");
			client.sendCheckFunds();
		}
	}

	// ========== Callback methods called by Client ==========

	public void onLoginSuccess(String userType) {
		this.isDealer = userType.equals("dealer");
		welcomeLabel.setText("Welcome, " + client.getUsername() + "!");
		if (isDealer) {
			// Update dealer panel name
			cardLayout.show(cardPanel, "Dealer Game");
		} else {
			client.sendCheckFunds();
			cardLayout.show(cardPanel, "Player Game");
		}
	}

	public void onLoginResult(boolean success, String text) {
		setLoginButtonsEnabled(true);
		if (success) {
			onLoginSuccess(text);
		} else if ("registered".equals(text)) {
			onRegisterSuccess(text);
		} else {
			loginStatusLabel.setText(text != null ? text : "Login failed.");
			loginStatusLabel.setForeground(new Color(255, 100, 100));
		}
	}

	public void onRegisterSuccess(String message) {
		JOptionPane.showMessageDialog(frame, "Registration successful! You can now login.",
				"Success", JOptionPane.INFORMATION_MESSAGE);
	}

	public void onGameListReceived(String gameList) {
		gameListArea.setText("");
		if (gameList == null || gameList.isEmpty()) {
			gameListArea.setText("No games available.");
			return;
		}
		String[] games = gameList.split("\n");
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-6s %-12s %-15s %-10s%n", "ID", "Status", "Dealer", "Players"));
		sb.append("-".repeat(50)).append("\n");
		for (String game : games) {
			String[] parts = game.split(":");
			if (parts.length >= 4) {
				sb.append(String.format("%-6s %-12s %-15s %-10s%n",
						parts[0], parts[1], parts[2], parts[3]));
			} else {
				sb.append(game).append("\n");
			}
		}
		gameListArea.setText(sb.toString());
	}

	public void onPlayerListReceived(String playerList) {
		playerListArea.setText("");
		if (playerList == null || playerList.isEmpty()) {
			playerListArea.setText("No players online.");
			return;
		}
		String[] players = playerList.split(",");
		for (String player : players) {
			playerListArea.append(player.trim() + "\n");
		}
	}

	public void onDealerListReceived(String dealerList) {
		playerListArea.setText("");
		if (dealerList == null || dealerList.isEmpty()) {
			playerListArea.setText("No dealers online.");
			return;
		}
		String[] dealers = dealerList.split(",");
		for (String dealer : dealers) {
			playerListArea.append(dealer.trim() + "\n");
		}
	}

	public void onPlayersInGameReceived(String data) {
		playersInGameArea.setText("");
		if (data == null || data.isEmpty()) {
			playersInGameArea.setText("No players in this game.");
			return;
		}
		String[] lines = data.split("\n");
		for (String line : lines) {
			String[] parts = line.split(":");
			if (parts.length >= 2) {
				playersInGameArea.append("Name: " + parts[0] + "\n");
				playersInGameArea.append("  Hand: " + parts[1] + "\n");
				if (parts.length >= 4) {
					playersInGameArea.append("  Funds: $" + parts[2] + "  Bet: $" + parts[3] + "\n");
				} else if (parts.length == 3) {
					playersInGameArea.append("  Funds: $" + parts[2] + "\n");
				}
				playersInGameArea.append("\n");
			}
		}
	}

	public void onGameOpened(String gameId) {
		currentGameId = gameId;
		statusLabel.setText("Game #" + gameId + " opened. Waiting for players...");
		if (isDealer) {
			cardLayout.show(cardPanel, "BlackjackTable");
		}
	}

	public void onGameClosed(String message) {
		JOptionPane.showMessageDialog(frame, message, "Game Closed", JOptionPane.INFORMATION_MESSAGE);
	}

	public void onGameJoined(String gameId) {
		currentGameId = gameId;
		statusLabel.setText("Joined Game #" + gameId);
		cardLayout.show(cardPanel, "BlackjackTable");
		// Reset table state
		resetCardPanel(dealerCardPanel, "Waiting for cards...");
		dealerTotalLabel.setText("Total: -");
		resetCardPanel(playerCardPanel, "Place your bet!");
		playerTotalLabel.setText("Total: -");
		playerBetLabel.setText("Bet: $0.00");
		gameResultLabel.setText("");
		hitButton.setEnabled(false);
		standButton.setEnabled(false);
		client.sendCheckFunds();
		updateBetButtonState();
	}

	public void onGameLeft(String message) {
		currentGameId = null;
		showGamePanel();
	}

	public void onFundsReceived(String funds) {
		try {
			double amount = Double.parseDouble(funds);
			currentFunds = amount;
			fundsLabel.setText(String.format("Funds: $%.2f", amount));
			playerFundsLabel.setText(String.format("Funds: $%.2f", amount));
			updateBetButtonState();
		} catch (NumberFormatException e) {
			fundsLabel.setText("Funds: " + funds);
		}
	}

	public void onFundsAdded(String message) {
		JOptionPane.showMessageDialog(frame, message, "Funds Added", JOptionPane.INFORMATION_MESSAGE);
		client.sendCheckFunds();
	}

	public void onRoundState(String gameState) {
		if (gameState == null || gameState.isEmpty()) return;

		String[] lines = gameState.split("\n");
		for (String line : lines) {
			int colonIdx = line.indexOf(':');
			if (colonIdx < 0) continue;

			String key = line.substring(0, colonIdx).trim();
			String value = line.substring(colonIdx + 1).trim();

			switch (key) {
				case "DEALER_HAND":
					updateCardPanel(dealerCardPanel, value);
					break;
				case "DEALER_TOTAL":
					dealerTotalLabel.setText("Total: " + value);
					break;
				case "PLAYER_HAND":
					updateCardPanel(playerCardPanel, value);
					break;
				case "PLAYER_TOTAL":
					playerTotalLabel.setText("Total: " + value);
					break;
				case "PLAYER_BET":
					playerBetLabel.setText("Bet: $" + value);
					break;
				case "PLAYER_FUNDS":
					playerFundsLabel.setText("Funds: $" + value);
					fundsLabel.setText("Funds: $" + value);
					try { currentFunds = Double.parseDouble(value); } catch (NumberFormatException ignored) {}
					break;
				case "STATUS":
					statusLabel.setText(value);
					break;
				case "RESULT":
					gameResultLabel.setText(value);
					break;
				case "YOUR_TURN":
					hitButton.setEnabled(true);
					standButton.setEnabled(true);
					placeBetButton.setEnabled(false);
					break;
				case "WAITING":
					hitButton.setEnabled(false);
					standButton.setEnabled(false);
					break;
				case "BET_PHASE":
					hitButton.setEnabled(false);
					standButton.setEnabled(false);
					updateBetButtonState();
					break;
				case "ROUND_OVER":
					hitButton.setEnabled(false);
					standButton.setEnabled(false);
					updateBetButtonState();
					break;
			}
		}
	}

	private void updateBetButtonState() {
		if (currentFunds <= 0) {
			placeBetButton.setEnabled(false);
			statusLabel.setText("No funds! Go back and add funds to continue playing.");
		} else {
			placeBetButton.setEnabled(true);
		}
	}

	public void onLoggedOut() {
		currentGameId = null;
		usernameField.setText("");
		passwordField.setText("");
		cardLayout.show(cardPanel, "Login");
	}

	public void onError(String message) {
		statusLabel.setText("Error: " + message);
		JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	// ========== Main ==========

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			BlackjackGUI gui = new BlackjackGUI();
			Client client = new Client(gui);
			gui.client = client;
			client.connect();
		});
	}
}
