// ChatImage © 2023 ChickenSaysBak
// This code is licensed under MIT license (see LICENSE file for details).
package me.chickensaysbak.playerdatatocsv;

public enum DataOption {

    USERNAME("Username", "bukkit>lastKnownName", true),
    FIRST_PLAYED("First Played", "bukkit>firstPlayed", true),
    LAST_PLAYED("Last Played", "bukkit>lastPlayed", true),
    COORDINATES("Coordinates", "Pos", false),
    ROTATION("Rotation", "Rotation", false),
    DIMENSION("Dimension", "Dimension", false),
    HEALTH("Health", "Health", false),
    FOOD_LEVEL("Food Level", "foodLevel", false),
    GAME_MODE("Game Mode", "playerGameType", false),
    XP_LEVEL("XP Level", "XpLevel", false),
    SCORE("Score", "Score", false),
    SEEN_CREDITS("Seen Credits", "seenCredits", false);

    private final String NAME;
    private final String PATH;
    private final boolean DEFAULT;

    DataOption(String name, String path, boolean def) {
        NAME = name;
        PATH = path;
        DEFAULT = def;
    }

    public String toString() {
        return NAME;
    }

    public String getPath() {
        return PATH;
    }

    public boolean isDefault() {
        return DEFAULT;
    }

    public static DataOption getByName(String name) {
        for (DataOption option : DataOption.values()) if (option.toString().equals(name)) return option;
        throw new IllegalArgumentException("Invalid option name: " + name);
    }

}
