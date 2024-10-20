package at.srsyntax.farmingworld;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.farmworld.Border;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.farmworld.LocationRandomizer;
import at.srsyntax.farmingworld.api.farmworld.sign.SignRegistry;
import at.srsyntax.farmingworld.api.handler.cooldown.Cooldown;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import at.srsyntax.farmingworld.api.handler.economy.Economy;
import at.srsyntax.farmingworld.api.template.TemplateRegistry;
import at.srsyntax.farmingworld.api.ticket.Ticket;
import at.srsyntax.farmingworld.farmworld.FarmWorldImpl;
import at.srsyntax.farmingworld.farmworld.FarmWorldLoader;
import at.srsyntax.farmingworld.farmworld.LocationRandomizerImpl;
import at.srsyntax.farmingworld.handler.cooldown.CooldownImpl;
import at.srsyntax.farmingworld.handler.countdown.CountdownImpl;
import at.srsyntax.farmingworld.handler.economy.EconomyImpl;
import at.srsyntax.farmingworld.ticket.TeleportTicket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
@AllArgsConstructor
@Getter
public class APIImpl implements API {

    private final FarmingWorldPlugin plugin;

    @Override
    public Countdown getCountdown(@NotNull Player player, @NotNull CountdownCallback callback) {
        if (hasCountdown(player))
            return plugin.getCountdownRegistry().getCountdown(player);
        return new CountdownImpl(plugin, player, callback);
    }

    @Override
    public boolean hasCountdown(Player player) {
        return plugin.getCountdownRegistry().hasCountdown(player);
    }

    @Override
    public Cooldown getCooldown(Player player, FarmWorld farmWorld) {
        return new CooldownImpl(plugin, plugin.getMessageConfig().getCooldown(), player, farmWorld);
    }

    @Override
    public boolean hasCooldown(Player player, FarmWorld farmWorld) {
        return getCooldown(player, farmWorld).hasCooldown();
    }

    @Override
    public boolean vaultSupported() {
        return plugin.getEconomy() != null;
    }

    @Override
    public void teleport(@NotNull FarmWorld farmWorld, @NotNull Player... players) {
        farmWorld.teleport(players);
    }

    @Override
    public void teleport(@NotNull FarmWorld farmWorld, boolean sameLocation, @NotNull Player... players) {
        farmWorld.teleport(sameLocation, players);
    }

    @Override
    public void teleport(@NotNull FarmWorld farmWorld, @NotNull List<Player> players) {
        farmWorld.teleport(players);
    }

    @Override
    public void teleport(@NotNull FarmWorld farmWorld, boolean sameLocation, @NotNull List<Player> players) {
        farmWorld.teleport(sameLocation, players);
    }

    @Override
    public @NotNull World generateWorld(FarmWorld farmWorld) {
        return new FarmWorldLoader(plugin, (FarmWorldImpl) farmWorld).generateWorld();
    }

    @Override
    public @Nullable FarmWorld getFarmWorld(String name) {
        for (FarmWorld farmWorld : plugin.getPluginConfig().getFarmWorlds()) {
            if (farmWorld.getName().equalsIgnoreCase(name))
                return farmWorld;
        }
        return null;
    }

    @Override
    public @Nullable FarmWorld getFarmWorld(World world) {
        for (FarmWorldImpl farmWorld : plugin.getPluginConfig().getFarmWorlds()) {
            if (farmWorld.isEnabled() && farmWorld.getWorld().equals(world)) return farmWorld;
            else if (farmWorld.hasNext() && farmWorld.getNextWorld().equals(world)) return farmWorld;
            else if (farmWorld.getOldWorldName() != null && farmWorld.getOldWorldName().equalsIgnoreCase(world.getName())) return farmWorld;
        }
        return null;
    }

    @Override
    public @NotNull List<FarmWorld> getFarmWorlds() {
        return new ArrayList<>(plugin.getPluginConfig().getFarmWorlds());
    }

    @Override
    public @Nullable FarmWorld getDefaultFarmWorld() {
        final String defaultWorld = plugin.getPluginConfig().getDefaultFarmWorld();
        return defaultWorld == null ? (getFarmWorlds().isEmpty() ? null : getFarmWorlds().get(0)) : getFarmWorld(defaultWorld);
    }

    @Override
    public @NotNull LocationRandomizer createLocationRandomizer(List<Material> blacklist, World world, Border border) {
        return new LocationRandomizerImpl(blacklist, world, border);
    }

    @Override
    public @NotNull LocationRandomizer createLocationRandomizer(List<Material> blacklist, FarmWorld farmWorld) {
        return new LocationRandomizerImpl(blacklist, farmWorld);
    }

    @Override
    public @NotNull LocationRandomizer createLocationRandomizer(FarmWorld farmWorld) {
        return new LocationRandomizerImpl(plugin.getPluginConfig().getBlacklist(), farmWorld);
    }

    @Override
    public @NotNull SignRegistry getSignRegistry() {
        return plugin.getSignRegistry();
    }

    @Override
    public @NotNull Economy createEconomy(FarmWorld farmWorld, Player player) {
        return new EconomyImpl(plugin, player, farmWorld.getPrice());
    }

    @Override
    public @NotNull Ticket createTicket(FarmWorld farmWorld) {
        return new TeleportTicket((FarmWorldImpl) farmWorld, plugin.getPluginConfig().getTicket());
    }

    @Override
    public @NotNull TemplateRegistry getTemplateRegistry() {
        return plugin.getTemplateRegistry();
    }

    @Override
    public @NotNull String generateRandomName(String prefix) {
        return String.format("%s-%s", prefix, UUID.randomUUID().toString().split("-")[0]);
    }

    @Override
    public @NotNull String generateRandomName(FarmWorld farmWorld) {
        return generateRandomName(farmWorld.getName());
    }
}
