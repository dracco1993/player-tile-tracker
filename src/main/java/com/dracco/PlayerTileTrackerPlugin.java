package com.dracco;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Inject;

import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(name = "Player Tile Tracker")
public class PlayerTileTrackerPlugin extends Plugin {
	private static final int MAX_TRACKED_TILES = 10;

	private final Set<WorldPoint> recentTiles = new LinkedHashSet<>();
	private WorldPoint lastPlayerLocation;

	@Inject
	private Client client;

	@Inject
	private PlayerTileTrackerConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PlayerTileTrackerTileOverlay playerTileTrackerTileOverlay;

	@Override
	protected void startUp() throws Exception {
		overlayManager.add(playerTileTrackerTileOverlay);

		log.info("PlayerTileTracker started!");
	}

	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(playerTileTrackerTileOverlay);

		log.info("PlayerTileTracker stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "PlayerTileTracker says hello " + config.targetPlayer(),
					null);
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null) {
			return;
		}

		WorldPoint currentLocation = localPlayer.getWorldLocation();
		if (currentLocation == null) {
			return;
		}

		// Check if player has moved to a new tile
		if (!currentLocation.equals(lastPlayerLocation)) {
			lastPlayerLocation = currentLocation;
			addTileToTracker(currentLocation);
		}
	}

	private void addTileToTracker(WorldPoint tile) {
		// Remove the tile if it already exists to avoid duplicates
		recentTiles.remove(tile);

		// Add the new tile
		recentTiles.add(tile);

		// Keep only the most recent MAX_TRACKED_TILES tiles
		while (recentTiles.size() > MAX_TRACKED_TILES) {
			// Remove the oldest tile (first in LinkedHashSet)
			WorldPoint oldestTile = recentTiles.iterator().next();
			recentTiles.remove(oldestTile);
		}
	}

	public Set<WorldPoint> getRecentTiles() {
		return recentTiles;
	}

	@Provides
	PlayerTileTrackerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(PlayerTileTrackerConfig.class);
	}
}
