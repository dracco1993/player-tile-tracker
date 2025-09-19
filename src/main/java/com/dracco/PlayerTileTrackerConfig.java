package com.dracco;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("PlayerTileTracker")
public interface PlayerTileTrackerConfig extends Config {
	@ConfigItem(keyName = "targetPlayer", name = "Target Player", description = "The player to track")
	default String targetPlayer() {
		return "Sk Dracco";
	}
}
