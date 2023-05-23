package at.srsyntax.farmingworld.util;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Marcel Haberl
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
@AllArgsConstructor
public class ChunkDeleterRunnable implements Runnable {

    private final API api;
    private final int time;

    @Override
    public void run() {
        for (FarmWorld farmWorld : api.getFarmWorlds()) {
            final var world = farmWorld.getWorld();
            if (world == null) continue;

            final File regionFolder = new File(world.getWorldFolder(), "region");
            if (!regionFolder.exists()) continue;

            for (File file : Objects.requireNonNull(regionFolder.listFiles())) {
                try {
                    if (needToDelete(file))
                        file.delete();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private boolean needToDelete(File file) throws IOException {
        if (file == null) return false;
        if (!file.exists()) return false;

        final var attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

        final long lastModified, currentTime;
        lastModified = attributes.lastModifiedTime().toMillis();
        currentTime = System.currentTimeMillis();

        return lastModified + TimeUnit.HOURS.toMillis(time) <= currentTime;
    }
}
