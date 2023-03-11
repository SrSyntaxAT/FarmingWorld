package at.srsyntax.farmingworld.util;

import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.command.FarmingAliasCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
public class CommandRegistry {

    private final String prefix;
    private final SimpleCommandMap commandMap;

    public CommandRegistry(String prefix) throws Exception {
        this.prefix = prefix;
        this.commandMap = getCommandMap();
    }

    private SimpleCommandMap getCommandMap() throws Exception {
        final Field field = SimplePluginManager.class.getDeclaredField("commandMap");
        field.setAccessible(true);
        return (SimpleCommandMap) field.get(Bukkit.getPluginManager());
    }

    public void register(FarmWorld farmWorld) {
        if (farmWorld.getAliases().isEmpty()) throw new IllegalArgumentException();
        for (String alias : farmWorld.getAliases())
            commandMap.register(prefix, createCommand(farmWorld, alias));
    }

    public void unregister(FarmWorld farmWorld) {
        if (farmWorld.getAliases().isEmpty()) throw new IllegalArgumentException();
        for (String alias : farmWorld.getAliases())
            commandMap.getCommand(alias).unregister(commandMap);
    }

    private Command createCommand(FarmWorld farmWorld, String name) {
        return new FarmingAliasCommand(name.replace(" " , "_"), new ArrayList<>(), farmWorld.getName());
    }
}
