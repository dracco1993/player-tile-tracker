package com.dracco;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.function.Consumer;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

// PlayerTileTrackerTileOverlay
public class PlayerTileTrackerTileOverlay extends Overlay {
	private final Client client;

	@Inject
	private PlayerTileTrackerTileOverlay(Client client) {
		this.client = client;
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(PRIORITY_MED);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		forEachPlayer((player) -> {
			System.out.println("Rendering polygon for player: " + player.getName());

			final Polygon poly = player.getCanvasTilePoly();

			if (poly != null) {
				OverlayUtil.renderPolygon(graphics, poly, java.awt.Color.RED);
			}
		});

		return null;
	}

	@SuppressWarnings("null")
	void forEachPlayer(final Consumer<Player> consumer) {
		// TODO: Fix this with a non-deprecated method
		for (Player player : client.getPlayers()) {
			if (player == null || player.getName() == null) {
				continue;
			}

			// If this is the target player, draw a polygon around their tile
			if (player.getName().equalsIgnoreCase(client.getLocalPlayer().getName())) {
				consumer.accept(player);
			}

		}
	}
}
