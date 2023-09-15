package at.srsyntax.farmingworld.farmworld.sign;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.farmworld.LocationCache;
import at.srsyntax.farmingworld.api.farmworld.sign.SignCache;
import at.srsyntax.farmingworld.api.farmworld.sign.SignRegistry;
import at.srsyntax.farmingworld.database.repository.SignRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;

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
public class SignRegistryImpl implements SignRegistry {

    public static final String SIGN_TITLE = "[fw]";

    private final SignRepository repository;
    private List<SignCache> caches;
    private final Logger logger;

    public SignRegistryImpl(Logger logger, SignRepository repository) {
        this.repository = repository;
        this.logger = logger;
        load();
    }

    @Override
    public void updateAll() {
        update(getCaches());
    }

    @Override
    public void update(@NotNull FarmWorld farmWorld) {
        update(getCaches(farmWorld));
    }

    private void update(List<SignCache> list) {
        list.forEach(SignCache::update);
    }

    @Override
    public SignCache register(@NotNull Sign sign, @NotNull FarmWorld farmWorld) {
        final var cache = new SignCacheImpl(sign, new LocationCache(sign.getLocation()),farmWorld);
        repository.save(cache);
        caches.add(cache);
        Bukkit.getScheduler().runTask(((APIImpl) FarmingWorldPlugin.getApi()).getPlugin(), cache::update);
        logger.info(String.format("Sign (%s) was created.", sign.getLocation()));
        return cache;
    }

    @Override
    public void unregister(@NotNull Location location) {
        unregister(getCache(location));
    }

    private void unregister(SignCache cache) {
        if (cache == null) return;
        clearLines(cache.getSign());
        caches.remove(cache);
        repository.delete(cache.getLocation());
        logger.info(String.format("Sign (%s) was deleted.", cache.getLocation()));
    }

    public boolean isTeleportSign(Location location) {
        return caches.contains(getCache(location));
    }

    @Override
    public void unregister(@NotNull FarmWorld farmWorld) {
        for (SignCache cache : caches) {
            if (cache.getFarmWorld().equals(farmWorld))
                unregister(cache);
        }
    }

    private void clearLines(Sign sign) {
        if (sign == null) return;
        for (byte i = 0; i < sign.getLines().length; i++)
            sign.setLine(i, "");
    }

    @Override
    public void unload() {
        caches.clear();
    }

    @Override
    public void load() {
        this.caches = repository.getCache();
    }

    @Override
    public @NotNull List<SignCache> getCaches() {
        return caches;
    }

    @Override
    public @NotNull List<SignCache> getCaches(@NotNull FarmWorld farmWorld) {
        return caches.stream().filter(signCache -> signCache.getFarmWorld().equals(farmWorld)).toList();
    }

    @Override
    public SignCache getCache(@NotNull Location location) {
        for (SignCache cache : caches) {
            if (cache.getSign().getLocation().equals(location))
                return cache;
        }
        return null;
    }
}
