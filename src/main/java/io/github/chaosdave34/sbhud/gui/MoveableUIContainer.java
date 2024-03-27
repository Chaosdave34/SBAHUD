package io.github.chaosdave34.sbhud.gui;

import gg.essential.elementa.components.UIContainer;
import gg.essential.elementa.effects.Effect;
import gg.essential.elementa.effects.OutlineEffect;
import kotlin.Pair;

import java.awt.*;

public class MoveableUIContainer extends UIContainer implements IMoveableUIContainer{
    private final String name;
    private boolean isDragging;
    private Pair<Float, Float> dragOffset;
    private Effect outlineEffect;

    public MoveableUIContainer(String name) {
        super();
        this.name = name;
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
