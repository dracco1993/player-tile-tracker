package com.dracco;

import javax.inject.Inject;

import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(name = "PlayerTileTracker")
public class PlayerTileTracker extends Plugin {
	@Inject
	private Client client;

	@Inject
	private PlayerTileTrackerConfig config;

	@Override
	protected void startUp() throws Exception {
		log.info("PlayerTileTracker started!");
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("PlayerTileTracker stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "PlayerTileTracker says " + config.greeting(), null);
		}
	}

	@Provides
	PlayerTileTrackerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(PlayerTileTrackerConfig.class);
	}
}
