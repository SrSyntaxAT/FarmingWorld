package at.srsyntax.farmingworld.api.handler.economy;

import at.srsyntax.farmingworld.api.handler.HandleException;
import net.milkbowl.vault.economy.EconomyResponse;
import org.jetbrains.annotations.NotNull;

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
/**
 * Signals that something went wrong with the economy handler.
 * This class is the base for economy related errors.
 */
public class EconomyException extends HandleException {

    private final Economy economy;
    private final EconomyResponse response;

    public EconomyException(@NotNull String message, @NotNull Economy economy, @NotNull EconomyResponse response) {
        super(message);
        this.economy = economy;
        this.response = response;
    }

    /**
     * Get the economy handler on which the exception is related.
     * @return the economy handler on which the exception is related.
     */
    public @NotNull Economy getEconomy() {
        return economy;
    }

    /**
     * Provides the response from Vault economy;
     * @return the response from Vault.
     */
    public @NotNull EconomyResponse getResponse() {
        return response;
    }
}
