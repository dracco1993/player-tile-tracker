package com.dracco;

import com.dracco.PlayerTileTracker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PlayerTileTrackerTest {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		ExternalPluginManager.loadBuiltin(PlayerTileTracker.class);
		RuneLite.main(args);
	}
}
