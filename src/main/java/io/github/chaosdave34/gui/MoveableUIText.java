package io.github.chaosdave34.gui;

import gg.essential.elementa.components.UIText;
import kotlin.Pair;

public class MoveableUIText extends UIText implements IMoveableUIContainer{
    private final String name;
    public String defaultText;
    private boolean isDragging;
    private Pair<Float, Float> dragOffset;

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


}
