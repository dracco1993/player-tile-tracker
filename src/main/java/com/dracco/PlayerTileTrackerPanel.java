package com.dracco;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.inject.Inject;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.PluginPanel;

/**
 * UI panel for the Player Tile Tracker plugin
 */
public class PlayerTileTrackerPanel extends PluginPanel {
  private final ConfigManager configManager;
  private final JTextField playerNameField;

  @Inject
  public PlayerTileTrackerPanel(ConfigManager configManager) {
    this.configManager = configManager;

    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(10, 10, 10, 10));

    // Create main content panel
    JPanel contentPanel = new JPanel(new BorderLayout());

    // Title label
    JLabel titleLabel = new JLabel("Player Tile Tracker");
    titleLabel.setHorizontalAlignment(JLabel.CENTER);
    contentPanel.add(titleLabel, BorderLayout.NORTH);

    // Input panel
    JPanel inputPanel = new JPanel(new BorderLayout());
    inputPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

    // Player name label
    JLabel playerNameLabel = new JLabel("Target Player Name:");
    inputPanel.add(playerNameLabel, BorderLayout.NORTH);

    // Player name text field
    playerNameField = new JTextField();
    playerNameField.setPreferredSize(new Dimension(200, 25));

    // Load current config value
    String currentPlayerName = configManager.getConfiguration("PlayerTileTracker", "targetPlayer");
    if (currentPlayerName != null && !currentPlayerName.isEmpty()) {
      playerNameField.setText(currentPlayerName);
    }

    // Add document listener for real-time updates
    playerNameField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateConfig();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateConfig();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateConfig();
      }

      private void updateConfig() {
        String playerName = playerNameField.getText().trim();
        configManager.setConfiguration("PlayerTileTracker", "targetPlayer", playerName);
      }
    });

    inputPanel.add(playerNameField, BorderLayout.CENTER);
    contentPanel.add(inputPanel, BorderLayout.CENTER);

    add(contentPanel, BorderLayout.NORTH);
  }

  /**
   * Update the text field with the current config value
   */
  public void updatePlayerNameField() {
    String currentPlayerName = configManager.getConfiguration("PlayerTileTracker", "targetPlayer");
    if (currentPlayerName != null && !playerNameField.getText().equals(currentPlayerName)) {
      playerNameField.setText(currentPlayerName);
    }
  }
}
