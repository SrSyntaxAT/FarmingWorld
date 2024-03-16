package at.srsyntax.farmingworld.api.util.file;

import at.srsyntax.farmingworld.api.util.file.CopyDirVisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
public class FileUtil {

    public static boolean deleteFolder(File folder) {
        if (!folder.exists()) return false;

        if(folder.isDirectory()) {
            final File[] files = folder.listFiles();

            if(files != null) {
                for (File file : files) {
                    if (file.isDirectory())
                        deleteFolder(file);
                    else
                        file.delete();
                }
            }
        }

        return folder.delete();
    }

    public static void copy(File src, File target) throws IOException {
        if (!src.exists()) throw new NullPointerException();
        Files.walkFileTree(src.toPath(), new CopyDirVisitor(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING));
    }
}
