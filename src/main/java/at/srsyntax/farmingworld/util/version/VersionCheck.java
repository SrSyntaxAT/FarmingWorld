package at.srsyntax.farmingworld.util.version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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
public record VersionCheck(String currentVersion, int ressouceId) {

  public boolean check() throws IOException {
    InputStreamReader inputStreamReader = null;
    BufferedReader bufferedReader = null;

    try {
      final URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.ressouceId);

      inputStreamReader = new InputStreamReader(url.openConnection().getInputStream());
      bufferedReader = new BufferedReader(inputStreamReader);

      return bufferedReader.readLine().equalsIgnoreCase(this.currentVersion);
    } finally {
      if (inputStreamReader != null) inputStreamReader.close();
      if (bufferedReader != null) bufferedReader.close();
    }
  }

}
