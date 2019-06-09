/*
 * This file is part of jGui API, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 johni0702 <https://github.com/johni0702>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.johni0702.minecraft.gui.container;

import cn.charlotte.biliforge.util.render.colors.CustomColor;
import de.johni0702.minecraft.gui.element.ComposedGuiElement;
import de.johni0702.minecraft.gui.element.GuiElement;
import de.johni0702.minecraft.gui.layout.Layout;
import de.johni0702.minecraft.gui.layout.LayoutData;
import org.lwjgl.util.Point;

import java.util.Comparator;
import java.util.Map;

public interface GuiContainer<T extends GuiContainer<T>> extends ComposedGuiElement<T> {

    Layout getLayout();

    T setLayout(Layout layout);

    void convertFor(GuiElement element, Point point);

    /**
     * Converts the global coordinates of the point to ones relative to the element.
     *
     * @param element       The element, must be part of this container
     * @param point         The point
     * @param relativeLayer Layer at which the point is relative to this element,
     *                      positive values are above this element
     */
    void convertFor(GuiElement element, Point point, int relativeLayer);

    Map<GuiElement, LayoutData> getElements();

    T addElements(LayoutData layoutData, GuiElement... elements);

    T removeElement(GuiElement element);

    T sortElements();

    T sortElements(Comparator<GuiElement> comparator);

    CustomColor getBackgroundColor();

    T setBackgroundColor(CustomColor backgroundColor);
}
