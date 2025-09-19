package com.dracco;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class PlayerTileTrackerTileOverlay extends Overlay {
	private final Client client;
	private final PlayerTileTrackerPlugin plugin;

	@Inject
	private PlayerTileTrackerTileOverlay(Client client, PlayerTileTrackerPlugin plugin) {
		this.client = client;
		this.plugin = plugin;
		setLayer(OverlayLayer.UNDER_WIDGETS);
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(PRIORITY_LOW);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		// Render all tracked tiles as red squares
		for (WorldPoint tile : plugin.getRecentTiles()) {
			LocalPoint localPoint = LocalPoint.fromWorld(client, tile);
			if (localPoint == null) {
				continue;
			}

			Polygon tilePoly = Perspective.getCanvasTilePoly(client, localPoint);
			if (tilePoly != null) {
				OverlayUtil.renderPolygon(graphics, tilePoly, Color.RED);
			}
		}

		return null;
	}
}
