package com.hirohiro716.gui.control;

import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.Bounds;
import com.hirohiro716.gui.Border;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.MouseCursor;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.MouseEvent;
import com.hirohiro716.gui.event.MouseEvent.MouseButton;

/**
 * マウス操作で移動やサイズ変更が可能なラベルのクラス。
 * 
 * @author hiro
 *
 */
public class MovableLabel extends Label {
    
    /**
     * コンストラクタ。<br>
     * このラベルに表示するテキストを指定する。
     * 
     * @param text
     */
    public MovableLabel(String text) {
        super(text);
        this.setBorder(Border.createLine(GUI.getBorderColor(), 1));
        this.addMouseMovedEventHandler(this.mouseMoveEventHandler);
        this.addMousePressedEventHandler(MouseButton.BUTTON1, this.mousePressedEventHandler);
        this.addMouseReleasedEventHandler(MouseButton.BUTTON1, this.mouseReleasedEventHandler);
        this.addMouseDraggedEventHandler(this.mouseDraggedEventHandler);
    }
    
    /**
     * コンストラクタ。
     */
    public MovableLabel() {
        this(null);
    }
    
    private int edgeSizeOfResize = 8;
    
    private List<Operation> operations = new ArrayList<>();
    
    private boolean isPressedMouseButton = false;
    
    private Bounds defaultBounds;
    
    private int startScreenPositionX;
    
    private int startScreenPositionY;
    
    /**
     * このラベルでマウス移動した際のイベントハンドラー。
     */
    private EventHandler<MouseEvent> mouseMoveEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            MovableLabel label = MovableLabel.this;
            if (label.isPressedMouseButton) {
                return;
            }
            int x = event.getX();
            int y = event.getY();
            int width = label.getWidth();
            int height = label.getHeight();
            label.operations.clear();
            if (x < label.edgeSizeOfResize) {
                label.operations.add(Operation.RESIZE_LEFT);
            }
            if (width - x < label.edgeSizeOfResize) {
                label.operations.add(Operation.RESIZE_RIGHT);
            }
            if (y < label.edgeSizeOfResize) {
                label.operations.add(Operation.RESIZE_TOP);
            }
            if (height - y < label.edgeSizeOfResize) {
                label.operations.add(Operation.RESIZE_BOTTOM);
            }
            if (label.operations.size() == 0) {
                label.operations.add(Operation.MOVE);
            }
            switch (label.operations.size()) {
            case 1:
                switch (label.operations.get(0)) {
                case MOVE:
                    label.setMouseCursor(MouseCursor.MOVE);
                    break;
                case RESIZE_TOP:
                    label.setMouseCursor(MouseCursor.RESIZE_NORTH);
                    break;
                case RESIZE_RIGHT:
                    label.setMouseCursor(MouseCursor.RESIZE_EAST);
                    break;
                case RESIZE_BOTTOM:
                    label.setMouseCursor(MouseCursor.RESIZE_SOUTH);
                    break;
                case RESIZE_LEFT:
                    label.setMouseCursor(MouseCursor.RESIZE_WEST);
                    break;
                }
                break;
            default:
                if (label.operations.contains(Operation.RESIZE_TOP) && label.operations.contains(Operation.RESIZE_RIGHT)) {
                    label.setMouseCursor(MouseCursor.RESIZE_NORTH_EAST);
                }
                if (label.operations.contains(Operation.RESIZE_RIGHT) && label.operations.contains(Operation.RESIZE_BOTTOM)) {
                    label.setMouseCursor(MouseCursor.RESIZE_SOUTH_EAST);
                }
                if (label.operations.contains(Operation.RESIZE_BOTTOM) && label.operations.contains(Operation.RESIZE_LEFT)) {
                    label.setMouseCursor(MouseCursor.RESIZE_SOUTH_WEST);
                }
                if (label.operations.contains(Operation.RESIZE_LEFT) && label.operations.contains(Operation.RESIZE_TOP)) {
                    label.setMouseCursor(MouseCursor.RESIZE_NORTH_WEST);
                }
                break;
            }
        }
    };
    
    /**
     * このラベルでマウスボタンを押した際のイベントハンドラー。
     */
    private EventHandler<MouseEvent> mousePressedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            MovableLabel label = MovableLabel.this;
            label.isPressedMouseButton = true;
            label.defaultBounds = label.getBounds();
            label.startScreenPositionX = event.getScreenX();
            label.startScreenPositionY = event.getScreenY();
        }
    };
    
    /**
     * このラベルでマウスボタンを離した際のイベントハンドラー。
     */
    private EventHandler<MouseEvent> mouseReleasedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            MovableLabel label = MovableLabel.this;
            label.isPressedMouseButton = false;
        }
    };
    
    /**
     * このラベルでマウスドラッグした際のイベントハンドラー。
     */
    private EventHandler<MouseEvent> mouseDraggedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            MovableLabel label = MovableLabel.this;
            int differenceX = event.getScreenX() - label.startScreenPositionX;
            int differenceY = event.getScreenY() - label.startScreenPositionY;
            for (Operation operation : label.operations) {
                switch (operation) {
                case MOVE:
                    int maximumY = label.getParent().getHeight() - label.getHeight();
                    int moveY = label.defaultBounds.getIntegerY() + differenceY;
                    if (moveY >= 0 && moveY < maximumY) {
                        label.setY(moveY);
                    }
                    int maximumX = label.getParent().getWidth() - label.getWidth();
                    int moveX = label.defaultBounds.getIntegerX() + differenceX;
                    if (moveX >= 0 && moveX < maximumX) {
                        label.setX(moveX);
                    }
                    break;
                case RESIZE_TOP:
                    int resizeTopY = label.defaultBounds.getIntegerY() + differenceY;
                    int resizeTopHeight = label.defaultBounds.getIntegerHeight() - differenceY;
                    if (resizeTopY >= 0 && resizeTopHeight > label.getMinimumHeight()) {
                        label.setY(resizeTopY);
                        label.setHeight(resizeTopHeight);
                    }
                    break;
                case RESIZE_RIGHT:
                    int maximumWidth = label.getParent().getWidth() - label.getX();
                    int resizeRightWidth = label.defaultBounds.getIntegerWidth() + differenceX;
                    if (resizeRightWidth > label.getMinimumWidth() && resizeRightWidth < maximumWidth) {
                        label.setWidth(resizeRightWidth);
                    }
                    break;
                case RESIZE_BOTTOM:
                    int maximumHeight = label.getParent().getHeight() - label.getY();
                    int resizeBottomHeight = label.defaultBounds.getIntegerHeight() + differenceY;
                    if (resizeBottomHeight > label.getMinimumHeight() && resizeBottomHeight < maximumHeight) {
                        label.setHeight(resizeBottomHeight);
                    }
                    break;
                case RESIZE_LEFT:
                    int resizeLeftX = label.defaultBounds.getIntegerX() + differenceX;
                    int resizeLeftWidth = label.defaultBounds.getIntegerWidth() - differenceX;
                    if (resizeLeftX >= 0 && resizeLeftWidth > label.getMinimumWidth()) {
                        label.setX(resizeLeftX);
                        label.setWidth(resizeLeftWidth);
                    }
                    break;
                }
            }
        }
    };
    
    /**
     * 操作の列挙型。
     * 
     * @author hiro
     *
     */
    private enum Operation {
        /**
         * 移動。
         */
        MOVE,
        /**
         * 上部のリサイズ。
         */
        RESIZE_TOP,
        /**
         * 右部のリサイズ。
         */
        RESIZE_RIGHT,
        /**
         * 下部のリサイズ。
         */
        RESIZE_BOTTOM,
        /**
         * 左部のりサイズ。
         */
        RESIZE_LEFT,
    }
}