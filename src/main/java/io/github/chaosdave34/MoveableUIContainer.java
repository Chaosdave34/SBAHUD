package io.github.chaosdave34;

import gg.essential.elementa.components.UIContainer;
import kotlin.Pair;

public class MoveableUIContainer extends UIContainer implements IMoveableUIContainer{
    private final String name;
    private boolean isDragging;
    private Pair<Float, Float> dragOffset;

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
}
