package at.srsyntax.farmingworld.util.version;

import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.config.PluginConfig;
import at.srsyntax.farmingworld.database.FarmingWorldData;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

/*
 * MIT License
 *
 * Copyright (c) 2022 Marcel Haberl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class ConfigUpdater {

  private final JsonObject object;

  public ConfigUpdater(File file) throws FileNotFoundException {
    this.object = new Gson().fromJson(new FileReader(file), JsonObject.class);
  }

  public boolean checkVersion(PluginDescriptionFile descriptionFile) {
    final JsonElement element = this.object.get("version");
    if (element == null) return false;
    return descriptionFile.getVersion().equalsIgnoreCase(element.getAsString());
  }

  public void updateConfig(PluginConfig config, Plugin plugin) throws IOException {
    config.setVersion(plugin.getDescription().getVersion());

    // since 1.6
    final JsonElement element = this.object.get("farmingWorlds");
    if (element == null) return;
    element.getAsJsonArray().forEach(jsonElement -> insertData(jsonElement.getAsJsonObject(), config));

    config.setAliases(Collections.singletonList("farmingworld"));

    config.save(plugin);
  }

  private void insertData(JsonObject object, PluginConfig pluginConfig) {
    final JsonElement created, current, next;
    created = object.get("created");
    current = object.get("currentWorldName");
    next = object.get("nextWorldName");

    final FarmingWorldData data = new FarmingWorldData(
        created == null ? 0L : created.getAsLong(),
        current == null ? null : current.getAsString(),
        next == null ? null : next.getAsString()
    );

    getFarmingWorld(object.get("name").getAsString(), pluginConfig).setData(data);
  }

  private FarmingWorldConfig getFarmingWorld(String name, PluginConfig pluginConfig) {
    for (FarmingWorldConfig config : pluginConfig.getFarmingWorlds()) {
      if (config.getName().equalsIgnoreCase(name))
        return config;
    }
    return null;
  }
}
