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
package de.johni0702.minecraft.gui.element;

import de.johni0702.minecraft.gui.GuiRenderer;
import de.johni0702.minecraft.gui.RenderInfo;
import de.johni0702.minecraft.gui.container.GuiContainer;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Dimension;
import org.lwjgl.util.Point;
import org.lwjgl.util.ReadableDimension;

public abstract class AbstractGuiElement<T extends AbstractGuiElement<T>> implements GuiElement<T> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation("jgui", "gui.png");

    @Getter
    private final Minecraft minecraft = Minecraft.getMinecraft();
    protected Dimension minSize, maxSize;
    @Getter
    private GuiContainer container;
    private GuiElement tooltip;
    @Getter
    private boolean enabled = true;
    /**
     * The last size this element was render at layer 0.
     * May be {@code null} when this element has not yet been rendered.
     */
    @Getter(AccessLevel.PROTECTED)
    private ReadableDimension lastSize;

    public AbstractGuiElement() {
    }

    public AbstractGuiElement(GuiContainer container) {
        container.addElements(null, this);
    }

    protected abstract T getThis();

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        if (renderInfo.layer == 0) {
            lastSize = size;
        }
    }

    @Override
    public T setEnabled(boolean enabled) {
        this.enabled = enabled;
        return getThis();
    }

    @Override
    public T setEnabled() {
        return setEnabled(true);
    }

    @Override
    public T setDisabled() {
        return setEnabled(false);
    }

    @Override
    public GuiElement getTooltip(RenderInfo renderInfo) {
        if (tooltip != null && lastSize != null) {
            Point mouse = new Point(renderInfo.mouseX, renderInfo.mouseY);
            if (container != null) {
                container.convertFor(this, mouse);
            }
            if (mouse.getX() > 0
                    && mouse.getY() > 0
                    && mouse.getX() < lastSize.getWidth()
                    && mouse.getY() < lastSize.getHeight()) {
                return tooltip;
            }
        }
        return null;
    }

    @Override
    public T setTooltip(GuiElement tooltip) {
        this.tooltip = tooltip;
        return getThis();
    }

    @Override
    public T setContainer(GuiContainer container) {
        this.container = container;
        return getThis();
    }

    public T setSize(ReadableDimension size) {
        setMinSize(size);
        return setMaxSize(size);
    }

    public T setSize(int width, int height) {
        return setSize(new Dimension(width, height));
    }

    public T setWidth(int width) {
        if (minSize == null) {
            minSize = new Dimension(width, 0);
        } else {
            minSize.setWidth(width);
        }
        if (maxSize == null) {
            maxSize = new Dimension(width, Integer.MAX_VALUE);
        } else {
            maxSize.setWidth(width);
        }
        return getThis();
    }

    public T setHeight(int height) {
        if (minSize == null) {
            minSize = new Dimension(0, height);
        } else {
            minSize.setHeight(height);
        }
        if (maxSize == null) {
            maxSize = new Dimension(Integer.MAX_VALUE, height);
        } else {
            maxSize.setHeight(height);
        }
        return getThis();
    }

    public int getLayer() {
        return 0;
    }

    @Override
    public ReadableDimension getMinSize() {
        ReadableDimension calcSize = calcMinSize();
        if (minSize == null) {
            return calcSize;
        } else {
            if (minSize.getWidth() >= calcSize.getWidth() && minSize.getHeight() >= calcSize.getHeight()) {
                return minSize;
            } else {
                return new Dimension(
                        Math.max(calcSize.getWidth(), minSize.getWidth()),
                        Math.max(calcSize.getHeight(), minSize.getHeight())
                );
            }
        }
    }

    public T setMinSize(ReadableDimension minSize) {
        this.minSize = new Dimension(minSize);
        return getThis();
    }

    protected abstract ReadableDimension calcMinSize();

    @Override
    public ReadableDimension getMaxSize() {
        return maxSize == null ? new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE) : maxSize;
    }

    public T setMaxSize(ReadableDimension maxSize) {
        this.maxSize = new Dimension(maxSize);
        return getThis();
    }
}
