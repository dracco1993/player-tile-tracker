package com.dracco;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
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
		// Render all tracked tiles as red squares with timer globes
		for (TileEntry entry : plugin.getRecentTileEntries()) {
			WorldPoint tile = entry.getWorldPoint();
			LocalPoint localPoint = LocalPoint.fromWorld(client, tile);
			if (localPoint == null) {
				continue;
			}

			Polygon tilePoly = Perspective.getCanvasTilePoly(client, localPoint);
			if (tilePoly != null) {
				OverlayUtil.renderPolygon(graphics, tilePoly, Color.RED);

				// Calculate timer progress using real time
				long elapsedMillis = System.currentTimeMillis() - entry.getTimestamp();
				long maxMillis = plugin.ITEM_SPAWN_DELAY * 1000L;

				if (elapsedMillis < maxMillis) {
					renderTimerGlobe(graphics, localPoint, entry);
				}
			}
		}

		return null;
	}

	private void renderTimerGlobe(Graphics2D graphics, LocalPoint localPoint, TileEntry entry) {
		Point canvasPoint = Perspective.localToCanvas(client, localPoint, 0);
		if (canvasPoint == null) {
			return;
		}

		// Move the timer globe to the center of the tile
		int centerX = canvasPoint.getX();
		int centerY = canvasPoint.getY() - 10; // Slightly above the tile center
		int radius = 15;

		// Enable antialiasing for smooth circles
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Calculate progress (0.0 to 1.0) using real time
		long elapsedMillis = System.currentTimeMillis() - entry.getTimestamp();
		long maxMillis = plugin.ITEM_SPAWN_DELAY * 1000L;
		double progress = Math.min(1.0, (double) elapsedMillis / maxMillis);

		// Background circle (gray - empty state)
		graphics.setColor(new Color(100, 100, 100, 50));
		graphics.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

		// Progress arc (red to green transition with increasing opacity)
		Color fillColor = getProgressFillColor(progress);
		graphics.setColor(fillColor);
		graphics.setStroke(new BasicStroke(3));

		// Draw progress arc (starts empty, fills up counter-clockwise)
		int arcAngle = (int) (360 * progress); // Fill up from empty to full
		if (arcAngle > 0) {
			graphics.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 90, arcAngle);
		}

		// Timer text in the center
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Arial", Font.BOLD, 10));
		FontMetrics fm = graphics.getFontMetrics();

		double remainingSeconds = Math.max(0.0, (maxMillis - elapsedMillis) / 1000.0);
		String timerText = String.format("%.1f", Math.max(0.0, remainingSeconds));
		int textWidth = fm.stringWidth(timerText);
		int textHeight = fm.getHeight();

		graphics.drawString(timerText,
				centerX - textWidth / 2,
				centerY - textHeight / 2 + fm.getAscent());

		// Reset rendering hints
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	private Color getProgressFillColor(double progress) {
		// Color transition: Red (progress = 0) to Green (progress = 1)
		int red = (int) (255 * (1.0 - progress)); // 255 -> 0
		int green = (int) (255 * progress); // 0 -> 255
		int blue = 0; // Always 0

		// Opacity transition: 50 (progress = 0) to 100 (progress = 1)
		int alpha = (int) (50 + (50 * progress)); // 50 -> 100

		return new Color(red, green, blue, alpha);
	}
}
