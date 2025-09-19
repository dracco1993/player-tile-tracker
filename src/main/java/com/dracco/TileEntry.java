package com.dracco;

import net.runelite.api.coords.WorldPoint;

/**
 * Represents a tile entry with its location and the game tick when it was
 * visited
 */
public class TileEntry {
  private final WorldPoint worldPoint;
  private final int gameTick;

  public TileEntry(WorldPoint worldPoint, int gameTick) {
    this.worldPoint = worldPoint;
    this.gameTick = gameTick;
  }

  public WorldPoint getWorldPoint() {
    return worldPoint;
  }

  public int getGameTick() {
    return gameTick;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;

    TileEntry tileEntry = (TileEntry) obj;
    return gameTick == tileEntry.gameTick &&
        worldPoint != null ? worldPoint.equals(tileEntry.worldPoint) : tileEntry.worldPoint == null;
  }

  @Override
  public int hashCode() {
    int result = worldPoint != null ? worldPoint.hashCode() : 0;
    result = 31 * result + gameTick;
    return result;
  }

  @Override
  public String toString() {
    return "TileEntry{" +
        "worldPoint=" + worldPoint +
        ", gameTick=" + gameTick +
        '}';
  }
}
