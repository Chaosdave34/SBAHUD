package io.github.chaosdave34.gui;

import gg.essential.elementa.components.UIBlock;
import kotlin.Pair;

public class MoveableUIBlock extends UIBlock implements IMoveableUIContainer {
    private final String name;
    private boolean isDragging;
    private Pair<Float, Float> dragOffset;

    public MoveableUIBlock(String name) {
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
}
