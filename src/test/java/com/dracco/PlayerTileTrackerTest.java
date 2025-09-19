package com.dracco;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PlayerTileTrackerTest {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		ExternalPluginManager.loadBuiltin(PlayerTileTrackerPlugin.class);
		RuneLite.main(args);
	}
}
