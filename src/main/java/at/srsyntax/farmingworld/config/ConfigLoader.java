package at.srsyntax.farmingworld.config;

import at.srsyntax.farmingworld.util.version.ConfigUpdater;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

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
public class ConfigLoader {

  private static final String CONFIG_FILE_NAME = "config.json";

  public static PluginConfig load(Plugin plugin, PluginConfig defaultConfig) throws IOException {
    if (!plugin.getDataFolder().exists())
      plugin.getDataFolder().mkdirs();

    final File file = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
    if (file.exists()) {
      final PluginConfig config = new Gson().fromJson(new FileReader(file), PluginConfig.class);
      final ConfigUpdater updater = new ConfigUpdater(file);
      if (!updater.checkVersion(plugin.getDescription()))
        updater.updateConfig(config, plugin);
      return config;
    }

    defaultConfig.save(plugin);
    return defaultConfig;
  }

  public void save(Plugin plugin) throws IOException {
    final File file = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
    if (!file.exists())
      file.createNewFile();

    final String content = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(this);
    Files.write(
        file.toPath(),
        Arrays.asList(content.split("\n")),
        StandardCharsets.UTF_8
    );
  }
}
