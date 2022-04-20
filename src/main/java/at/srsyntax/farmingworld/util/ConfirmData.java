package at.srsyntax.farmingworld.util;

import at.srsyntax.farmingworld.api.FarmingWorld;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
public class ConfirmData {

  public static final long EXPIRED = TimeUnit.SECONDS.toMillis(10);

  private final long timestamp;
  @Getter
  private final ConfirmAction action;
  private final Map<String, Object> data = new LinkedHashMap<>();

  public ConfirmData(ConfirmAction action) {
    this.action = action;
    this.timestamp = System.currentTimeMillis();
  }

  public ConfirmData addData(String key, Object value) {
    this.data.put(key, value);
    return this;
  }

  public <T> T getData(String key) {
    try {
      final Object value = this.data.get(key);
      if (value == null) return null;
      return (T) value;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  public boolean isExpired() {
    return System.currentTimeMillis() - timestamp > EXPIRED;
  }
}
