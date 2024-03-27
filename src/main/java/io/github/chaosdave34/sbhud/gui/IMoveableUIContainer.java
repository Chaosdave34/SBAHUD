package io.github.chaosdave34.sbhud.gui;

import gg.essential.elementa.UIComponent;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.effects.Effect;
import gg.essential.elementa.effects.OutlineEffect;
import gg.essential.elementa.events.UIClickEvent;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

import java.awt.*;


public interface IMoveableUIContainer {
    Function2<? super UIComponent, ? super UIClickEvent, Unit> onMouseClickHandler = (component, event) -> {
        IMoveableUIContainer moveableComponent = (IMoveableUIContainer) component;

        moveableComponent.setIsDragging(true);
        moveableComponent.setDragOffset(new Pair<>(event.getAbsoluteX(), event.getAbsoluteY()));

        return null;
    };

    Function1<? super UIComponent, Unit> onMouseReleaseHandler = (component) -> {
        IMoveableUIContainer moveableComponent = (IMoveableUIContainer) component;

        moveableComponent.setIsDragging(false);

        return null;
    };

    Function4<? super UIComponent, ? super Float, ? super Float, ? super Integer, Unit> onMouseDragHandler = (component, x, y, mouseButton) -> {
        IMoveableUIContainer moveableComponent = (IMoveableUIContainer) component;

        if (!moveableComponent.isDragging() || mouseButton != 0) {
            return null;
        }

        float absolutX = x + component.getLeft();
        float absolutY = y + component.getTop();

        float deltaX = absolutX - moveableComponent.getDragOffset().getFirst();
        float deltaY = absolutY - moveableComponent.getDragOffset().getSecond();

        moveableComponent.setDragOffset(new Pair<>(absolutX, absolutY));

        float newX = component.getLeft() + deltaX;
        float newY = component.getTop() + deltaY;

        component.setX(new PixelConstraint(newX));
        component.setY(new PixelConstraint(newY));

        return null;
    };

    String getName();

    boolean isDragging();

    void setIsDragging(boolean isDragging);

    Pair<Float, Float> getDragOffset();

    void setDragOffset(Pair<Float, Float> dragOffset);

    void enableOutline();

    void disableOutline();
}
