package at.srsyntax.farmingworld.farmworld;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.farmworld.Border;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.handler.cooldown.Cooldown;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import at.srsyntax.farmingworld.database.repository.FarmWorldRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
public class FarmWorldImpl implements FarmWorld {

    @Setter
    private transient FarmingWorldPlugin plugin;

    private final String name;
    private final String permission;

    private final int cooldown, countdown, timer;

    private final World.Environment environment;
    private final String generator;
    private final Border border;
    @Getter
    private boolean active = false;

    @Getter @Setter
    private transient FarmWorldData data;
    @Getter @Setter
    private transient boolean loaded = false, enabled = false;
    @Getter @Setter private transient LinkedHashMap<String, Location> locations = new LinkedHashMap<>();

    public FarmWorldImpl(String name, String permission, int cooldown, int countdown, int timer, World.Environment environment, String generator, Border border) {
        this.name = name;
        this.permission = permission;
        this.cooldown = cooldown;
        this.countdown = countdown;
        this.timer = timer;
        this.environment = environment;
        this.generator = generator;
        this.border = border;
        this.data = new FarmWorldData(0, null, null);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public boolean hasPermission(@NotNull Player player) {
        return player.hasPermission("farmingworld.world.*") || player.hasPermission("farmingworld.world." + name);
    }

    @Override
    public void teleport(@NotNull Player... players) {
        teleport(false, players);
    }

    @Override
    public void teleport(boolean sameLocation, @NotNull Player... players) {
        final Location location = sameLocation ? randomLocation() : null;
        for (Player player : players)
            player.teleport(location == null ? randomLocation() : location);
    }

    @Override
    public void teleport(@NotNull List<Player> players) {
        teleport(false, players);
    }

    @Override
    public void teleport(boolean sameLocation, @NotNull List<Player> players) {
        teleport(sameLocation, players.toArray(Player[]::new));
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public Cooldown getCooldown(@NotNull Player player) {
        return FarmingWorldPlugin.getApi().getCooldown(player, this);
    }

    @Override
    public int getCountdown() {
        return countdown;
    }

    @Override
    public Countdown getCountdown(@NotNull Player player, @NotNull CountdownCallback callback) {
        return FarmingWorldPlugin.getApi().getCountdown(player, callback);
    }

    @Override
    public int getTimer() {
        return timer;
    }

    @Override
    public void setActive(boolean active) {
        if (this.active == active) return;
        this.active = active;
        if (!active) new FarmWorldDeleter(plugin, this).disable();
        else new FarmWorldLoader(plugin, this).enable();
    }

    @Override
    public World.Environment getEnvironment() {
        return environment;
    }

    @Override
    public String getGenerator() {
        return generator;
    }

    @Override
    public @Nullable Border getBorder() {
        return border;
    }

    @Override
    public boolean needReset() {
        if (data.getCurrentWorldName() == null) return true;
        return data.getCreated() + TimeUnit.MINUTES.toMillis(timer) <= System.currentTimeMillis();
    }

    @Override
    public boolean needNextWorld() {
        return data.getCreated() + TimeUnit.MINUTES.toMillis(timer-1) <= System.currentTimeMillis() && !hasNext();
    }

    @SneakyThrows
    public void save(FarmingWorldPlugin plugin) {
        final FarmWorldRepository repository = plugin.getDatabase().getFarmWorldRepository();
        repository.save(this);
        plugin.getPluginConfig().save(plugin);
    }

    @Override
    public @Nullable World getWorld() {
        return data.getCurrentWorldName() == null ? null : new FarmWorldLoader(plugin, this).generateWorld(data.getCurrentWorldName());
    }

    @Override
    public void newWorld(@Nullable World nextWorld) {
        final World world = getWorld();
        data.setCurrentWorldName(nextWorld == null ? null : nextWorld.getName());
        data.setCreated(System.currentTimeMillis());

        if (locations == null) locations = new LinkedHashMap<>();
        if (!locations.isEmpty()) {
            plugin.getDatabase().getLocationRepository().deleteByFarmWorldName(name);
            locations.clear();
        }

        new FarmWorldLoader(plugin, this).checkLocations();
        if (nextWorld != null && world != null)
            teleport(world.getPlayers());

        new FarmWorldDeleter(plugin, this).deleteWorld(world);
        save(plugin);
    }

    @Override
    public @Nullable World getNextWorld() {
        return data.getNextWorldName() == null ? null : Bukkit.getWorld(data.getNextWorldName());
    }

    @Override
    public void newNextWorld(@Nullable World world) {
        data.setNextWorldName(world == null ? null : world.getName());
    }

    @Override
    public void deleteNextWorld() {
        if (!hasNext()) return;
        new FarmWorldDeleter(plugin, this).deleteWorld(getNextWorld());
    }

    @Override
    public void next() {
        final World nextWorld = hasNext() ? getNextWorld() : generateWorld();
        newNextWorld(null);
        next(nextWorld);
    }

    @Override
    public void next(@NotNull World world) {
        newWorld(world);
    }

    @Override
    public boolean hasNext() {
        return data.getNextWorldName() != null;
    }

    @Override
    public @NotNull World generateWorld() {
        return new FarmWorldLoader(plugin, this).generateWorld();
    }

    public WorldCreator createWorldCreator(String worldName) {
        final WorldCreator creator = new WorldCreator(worldName);
        creator.environment(environment);
        if (generator != null) creator.generator(generator);
        return creator;
    }

    @Override
    public void addLocation(String id, Location location) {
        if (locations == null) locations = new LinkedHashMap<>();
        locations.put(id, location);
    }

    @Override
    public void removeLocation(String id) {
        locations.remove(id);
        plugin.getDatabase().getLocationRepository().delete(id);
    }

    @Override
    public Location randomLocation() {
        if (locations.isEmpty())
            return new FarmWorldLoader(plugin, this).generateLocation(true);
        final Map.Entry<String, Location> location = locations.entrySet().stream().findFirst().get();
        removeLocation(location.getKey());
        if (locations.size() < plugin.getPluginConfig().getLocationCache())
            new FarmWorldLoader(plugin, this).generateLocation(true);
        return location.getValue();
    }
}
