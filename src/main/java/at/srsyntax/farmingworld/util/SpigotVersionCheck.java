package at.srsyntax.farmingworld.util;

import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/*
 * Copyright 2022 Marcel Haberl. No rights reserved.
 * Source: https://gist.github.com/SrSyntaxAT/8ea28bf0ea315db1317fbfde06ac0a09
 */
public class SpigotVersionCheck {

    private static final String API_URL = "https://api.spigotmc.org/legacy/update.php?resource=";

    /**
     * Check if the plugin is up-to-date.
     * @param plugin which would like to check its version.
     * @param ressourceId for the SpigotMC API.
     * @return if the versions of plugin and resource match.
     * @throws IOException – if an I/O exception occurs.
     * @see URL#URL(String)
     * @see URL#openConnection()
     * @see BufferedReader#readLine()
     */
    public static boolean check(Plugin plugin, int ressourceId) throws IOException {
        final URL url = new URL(API_URL + ressourceId);
        final InputStream stream = url.openConnection().getInputStream();
        final BufferedReader reader =  new BufferedReader(new InputStreamReader(stream));
        return reader.readLine().equalsIgnoreCase(plugin.getDescription().getVersion());
    }

    /**
     * Checks if the plugin is up-to-date and issues a warning message if it is not.
     * @param plugin which would like to check its version.
     * @param ressourceId for the SpigotMC API.
     * @param message to be output.
     */
    public static void checkWithError(Plugin plugin, int ressourceId, String message) {
        try {
            if (!check(plugin, ressourceId))
                plugin.getLogger().warning(message);
        } catch (Exception ignored) {}
    }
}
