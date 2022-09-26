package at.srsyntax.farmingworld.registry;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;

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
public class CommandRegistry {

    private final String name;
    private final SimpleCommandMap commandMap;

    public CommandRegistry(String name) throws Exception {
        this.name = name;
        this.commandMap = getCommandMap();
    }

    private SimpleCommandMap getCommandMap() throws Exception {
        final Field field = SimplePluginManager.class.getDeclaredField("commandMap");
        field.setAccessible(true);
        return  (SimpleCommandMap) field.get(Bukkit.getPluginManager());
    }

    public void register(Command... commands) {
        for (Command command : commands)
            commandMap.register(name, command);
    }

    public void unregister(Command command) {
        command.unregister(commandMap);
    }
}
