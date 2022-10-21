package at.srsyntax.farmingworld.api.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class Message {

    private final String message;
    private final ChatMessageType type;

    private final Map<String, Object> replaces = new ConcurrentHashMap<>();

    public Message(String message, ChatMessageType type) {
        this.message = message;
        this.type = type;
    }

    public Message replace(String key, Object value) {
        replaces.put(key, value);
        return this;
    }

    public void send(Player... players) {
        final TextComponent component = new TextComponent(toString());
        for (Player player : players)
            player.spigot().sendMessage(type, component);
    }

    @Override
    public String toString() {
        String result = message;

        replaces.put("&", "§");

        for (Map.Entry<String, Object> entry : replaces.entrySet())
            result = result.replace(entry.getKey(), String.valueOf(entry.getValue()));

        return result;
    }
}
