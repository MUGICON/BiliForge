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
package de.johni0702.minecraft.gui.popup;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import de.johni0702.minecraft.gui.container.GuiContainer;
import de.johni0702.minecraft.gui.container.GuiPanel;
import de.johni0702.minecraft.gui.element.GuiButton;
import de.johni0702.minecraft.gui.element.GuiElement;
import de.johni0702.minecraft.gui.function.Typeable;
import de.johni0702.minecraft.gui.layout.HorizontalLayout;
import de.johni0702.minecraft.gui.layout.VerticalLayout;
import de.johni0702.minecraft.gui.utils.Colors;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Dimension;
import org.lwjgl.util.ReadablePoint;

public class GuiYesNoPopup extends AbstractGuiPopup<GuiYesNoPopup> implements Typeable {
    private final SettableFuture<Boolean> future = SettableFuture.create();
    @Getter
    private final GuiButton yesButton = new GuiButton().setSize(150, 20).onClick(new Runnable() {
        @Override
        public void run() {
            close();
            future.set(true);
        }
    });
    @Getter
    private final GuiButton noButton = new GuiButton().setSize(150, 20).onClick(new Runnable() {
        @Override
        public void run() {
            close();
            future.set(false);
        }
    });
    @Getter
    private final GuiPanel info = new GuiPanel().setMinSize(new Dimension(320, 50))
            .setLayout(new VerticalLayout(VerticalLayout.Alignment.TOP).setSpacing(2));
    @Getter
    private final GuiPanel buttons = new GuiPanel()
            .setLayout(new HorizontalLayout(HorizontalLayout.Alignment.CENTER).setSpacing(5))
            .addElements(new HorizontalLayout.Data(0.5), yesButton, noButton);
    @Getter
    @Setter
    @Accessors(chain = true)
    private int layer;

    {
        popup.setLayout(new VerticalLayout().setSpacing(10))
                .addElements(new VerticalLayout.Data(0.5), info, buttons);
    }

    public GuiYesNoPopup(GuiContainer container) {
        super(container);
    }

    public static GuiYesNoPopup open(GuiContainer container, GuiElement... info) {
        GuiYesNoPopup popup = new GuiYesNoPopup(container).setBackgroundColor(Colors.DARK_TRANSPARENT);
        popup.getInfo().addElements(new VerticalLayout.Data(0.5), info);
        popup.open();
        return popup;
    }

    public GuiYesNoPopup setYesLabel(String label) {
        yesButton.setLabel(label);
        return this;
    }

    public GuiYesNoPopup setNoLabel(String label) {
        noButton.setLabel(label);
        return this;
    }

    public GuiYesNoPopup setYesI18nLabel(String label, Object... args) {
        yesButton.setI18nLabel(label, args);
        return this;
    }

    public GuiYesNoPopup setNoI18nLabel(String label, Object... args) {
        noButton.setI18nLabel(label, args);
        return this;
    }

    public ListenableFuture<Boolean> getFuture() {
        return future;
    }

    @Override
    protected GuiYesNoPopup getThis() {
        return this;
    }

    @Override
    public boolean typeKey(ReadablePoint mousePosition, int keyCode, char keyChar, boolean ctrlDown, boolean shiftDown) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            noButton.onClick();
            return true;
        }
        return false;
    }
}
