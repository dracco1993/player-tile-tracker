package com.dracco;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
	private static final int MAX_TRACKED_TILES = 20;
	public final int ITEM_SPAWN_DELAY = 60; // Seconds until item spawns on ground

	public final int MAX_TICKS = (int) (ITEM_SPAWN_DELAY / 0.6);

	private final Set<TileEntry> recentTiles = new LinkedHashSet<>();
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
		int currentTick = client.getTickCount();

		// Remove expired tiles (tiles older than ITEM_SPAWN_DELAY seconds)
		removeExpiredTiles(currentTick);

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
			addTileToTracker(currentLocation, currentTick);
		}
	}

	private void addTileToTracker(WorldPoint tile, int gameTick) {
		TileEntry newEntry = new TileEntry(tile, gameTick);

		// TODO: I don't think we actually need this
		// since players can walk back onto the same tile
		// Remove any existing entry with the same WorldPoint to avoid duplicates
		recentTiles.removeIf(entry -> entry.getWorldPoint().equals(tile));

		// Add the new tile entry
		recentTiles.add(newEntry);

		// Keep only the most recent MAX_TRACKED_TILES tiles
		while (recentTiles.size() > MAX_TRACKED_TILES) {
			// Remove the oldest tile (first in LinkedHashSet)
			TileEntry oldestEntry = recentTiles.iterator().next();
			recentTiles.remove(oldestEntry);
		}
	}

	public Set<TileEntry> getRecentTileEntries() {
		return recentTiles;
	}

	public Set<WorldPoint> getRecentTiles() {
		return recentTiles.stream()
				.map(TileEntry::getWorldPoint)
				.collect(Collectors.toSet());
	}

	private void removeExpiredTiles(int currentTick) {
		// Remove tiles that have been active longer than MAX_TICKS
		recentTiles.removeIf(entry -> {
			int elapsedTicks = currentTick - entry.getGameTick();
			return elapsedTicks >= MAX_TICKS;
		});
	}

	@Provides
	PlayerTileTrackerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(PlayerTileTrackerConfig.class);
	}
}
