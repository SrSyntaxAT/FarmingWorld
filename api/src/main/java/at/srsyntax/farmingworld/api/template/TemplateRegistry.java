package at.srsyntax.farmingworld.api.template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

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
public interface TemplateRegistry {

    /**
     * Register a template.
     * @param file - Location where the template is saved
     * @return an object that represents the template
     */
    @NotNull TemplateData register(@NotNull File file);

    /**
     * Unregister a template.
     * @param data which is to be unregistered
     * @return whether the template was successfully deleted
     */
    boolean unregister(@NotNull TemplateData data);

    /**
     * Receive the template or null if the template is not registered.
     * @param name of the template
     * @return the template or null if the template is not registered
     */
    @Nullable TemplateData getTemplate(@NotNull String name);

    /**
     * @return a modifiable list of all registered templates
     */
    @NotNull List<TemplateData> getTemplates();

}
