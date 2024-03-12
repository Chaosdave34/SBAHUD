package io.github.chaosdave34.gui;

import gg.essential.elementa.components.UIText;
import gg.essential.elementa.effects.Effect;
import gg.essential.elementa.effects.OutlineEffect;
import gg.essential.elementa.markdown.MarkdownComponent;
import kotlin.Pair;

import java.awt.*;

public class MoveableUIText extends UIText implements IMoveableUIContainer{
    private final String name;
    public String defaultText;
    private boolean isDragging;
    private Pair<Float, Float> dragOffset;
    private Effect outlineEffect;

    public MoveableUIText(String name, String defaultText) {
        super();
        this.name = name;
        this.defaultText = defaultText;
        onMouseClick(onMouseClickHandler);
        onMouseRelease(onMouseReleaseHandler);
        onMouseDrag(onMouseDragHandler);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDragging() {
        return isDragging;
    }

    @Override
    public void setIsDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }

    @Override
    public Pair<Float, Float> getDragOffset() {
        return dragOffset;
    }

    @Override
    public void setDragOffset(Pair<Float, Float> dragOffset) {
        this.dragOffset = dragOffset;
    }

    @Override
    public void enableOutline() {
        if (outlineEffect == null) {
            outlineEffect = new OutlineEffect(Color.RED, 1, true, true);
            enableEffect(outlineEffect);
        }
    }

    @Override
    public void disableOutline() {
        if (outlineEffect != null) {
            removeEffect(outlineEffect);
            outlineEffect = null;
        }
    }
}
