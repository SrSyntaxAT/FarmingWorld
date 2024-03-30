package at.srsyntax.farmingworld.util;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.template.TemplateData;
import at.srsyntax.farmingworld.api.template.TemplateRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * MIT License
 *
 * Copyright (c) 2022-2024 Marcel Haberl
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
public class TemplateRegistryImpl implements TemplateRegistry {

    private final List<TemplateData> templates = new ArrayList<>();

    public TemplateRegistryImpl(FarmingWorldPlugin plugin) {
        Arrays.stream(createFolder(plugin).listFiles()).forEach(file -> templates.add(new TemplateData(file)));
        plugin.getLogger().info(String.format("%d world template(s) found.", templates.size()));
    }

    private File createFolder(FarmingWorldPlugin plugin) {
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdirs();

        final var templateFolder = new File(plugin.getDataFolder(), "templates");
        if (!templateFolder.exists()) templateFolder.mkdirs();

        return templateFolder;
    }

    @Override
    public @NotNull TemplateData register(@NotNull File file) {
        final var data = new TemplateData(file);
        templates.add(data);
        return data;
    }

    @Override
    public boolean unregister(@NotNull TemplateData data) {
        return templates.remove(data);
    }

    @Override
    public @Nullable TemplateData getTemplate(@NotNull String name) {
        for (TemplateData data : templates) {
            if (data.getName().equalsIgnoreCase(name))
                return data;
        }
        return null;
    }

    @Override
    public @NotNull List<TemplateData> getTemplates() {
        return templates;
    }
}
