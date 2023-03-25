package at.srsyntax.farmingworld.farmworld;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.event.farmworld.FarmWorldChangeWorldEvent;
import at.srsyntax.farmingworld.api.farmworld.Border;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.farmworld.sign.SignCache;
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
    @Getter
    private final List<String> aliases;

    @Getter @Setter
    private transient FarmWorldData data;
    @Getter @Setter
    private transient boolean loaded = false, enabled = false;
    @Getter @Setter private transient LinkedHashMap<String, Location> locations = new LinkedHashMap<>();
    @Getter private transient String oldWorldName;

    public FarmWorldImpl(String name, String permission, int cooldown, int countdown, int timer, World.Environment environment, String generator, Border border, List<String> aliases) {
        this.name = name;
        this.permission = permission;
        this.cooldown = cooldown;
        this.countdown = countdown;
        this.timer = timer;
        this.environment = environment;
        this.generator = generator;
        this.border = border;
        this.aliases = aliases;
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
        if (permission == null || permission.equalsIgnoreCase("null")) return true;
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
    public long getResetDate() {
        return data.getCreated() + TimeUnit.MINUTES.toMillis(timer);
    }

    @Override
    public boolean needReset() {
        if (data.getCurrentWorldName() == null) return true;
        return getResetDate() <= System.currentTimeMillis();
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
        oldWorldName = world == null ? null : world.getName();
        data.setCurrentWorldName(nextWorld == null ? null : nextWorld.getName());
        data.setCreated(TimeUnit.MINUTES.toMillis(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis())));

        Bukkit.getPluginManager().callEvent(new FarmWorldChangeWorldEvent(this, world, getWorld()));

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FarmWorldImpl farmWorld = (FarmWorldImpl) o;
        return cooldown == farmWorld.cooldown && countdown == farmWorld.countdown && timer == farmWorld.timer
                && active == farmWorld.active && loaded == farmWorld.loaded && enabled == farmWorld.enabled
                && Objects.equals(name, farmWorld.name) && Objects.equals(permission, farmWorld.permission)
                && environment == farmWorld.environment && Objects.equals(generator, farmWorld.generator)
                && Objects.equals(border, farmWorld.border) && Objects.equals(aliases, farmWorld.aliases)
                && Objects.equals(data, farmWorld.data) && Objects.equals(locations, farmWorld.locations)
                && Objects.equals(oldWorldName, farmWorld.oldWorldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, permission, cooldown, countdown, timer, environment, generator, border, active, aliases, data, loaded, enabled, locations, oldWorldName);
    }

    @Override
    public void updateSigns() {
        plugin.getSignRegistry().update(this);
    }

    @Override
    public List<SignCache> getSigns() {
        return plugin.getSignRegistry().getCaches(this);
    }
}
