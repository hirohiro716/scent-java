package com.hirohiro716.gui.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.event.ChangeListener;

/**
 * 待機中のサークルのクラス。
 * 
 * @author hiro
 *
 */
public class WaitCircle extends Control {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected WaitCircle(AnimationPanel innerInstance) {
        super(innerInstance);
    }
    
    /**
     * コンストラクタ。
     */
    public WaitCircle() {
        this(new AnimationPanel());
        this.getInnerInstance().setWaitCircle(this);
        this.addSizeChangeListener(this.sizeChangeListener);
    }
    
    @Override
    public AnimationPanel getInnerInstance() {
        return (AnimationPanel) super.getInnerInstance();
    }

    @Override
    protected ChangeListener<Dimension> createBugFixChangeListener() {
        return null;
    }
    
    /**
     * このコントロールのサイズが変更された際にアニメーションのサイズも変更するリスナー。
     */
    private ChangeListener<Dimension> sizeChangeListener = new ChangeListener<>() {

        @Override
        protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
            WaitCircle waitCircle = WaitCircle.this;
            waitCircle.getInnerInstance().setCircleSize(changedValue.width / 12d * 5, changedValue.height / 12d * 5, (int) (changedValue.width / 12d * 2), (int) (changedValue.height / 12d * 2));
        }
    };
    
    /**
     * 待機中サークルのアニメーションを表示するJPanelクラス。
     * 
     * @author hiro
     *
     */
    @SuppressWarnings("serial")
    private static class AnimationPanel extends JPanel {
        
        private WaitCircle waitCircle;
        
        /**
         * 待機中サークルのインスタンスをセットする。
         * 
         * @param waitCircle
         */
        public void setWaitCircle(WaitCircle waitCircle) {
            this.waitCircle = waitCircle;
        }

        private double circleWidth;
        
        private double circleHeight;
        
        private int childCircleWidth;
        
        private int childCircleHeight;
        
        /**
         * 待機中サークルのサイズをセットする。
         * 
         * @param circleWidth
         * @param circleHeight
         * @param childCircleWidth
         * @param childCircleHeight
         */
        public void setCircleSize(double circleWidth, double circleHeight, int childCircleWidth, int childCircleHeight) {
            this.circleWidth = circleWidth;
            this.circleHeight = circleHeight;
            this.childCircleWidth = childCircleWidth;
            this.childCircleHeight = childCircleHeight;
        }
        
        private int startAngle = 0;
        
        private Color baseColor = UIManager.getColor("textHighlight");
        
        private Thread thread = null;
        
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            int maxIndex = 11;
            for (int index = 0; index < maxIndex; index++) {
                int alpha = 255 / maxIndex * (index + 1);
                Color color = new Color(this.baseColor.getRed(), this.baseColor.getGreen(), this.baseColor.getBlue(), alpha);
                graphics.setColor(color);
                double angle = 360d / maxIndex * index + this.startAngle;
                double radian = Math.toRadians(angle);
                int x = (int) (this.circleWidth + Math.cos(radian) * this.circleWidth);
                int y = (int) (this.circleHeight + Math.sin(radian) * this.circleHeight);
                graphics.fillOval(x, y, this.childCircleWidth, this.childCircleHeight);
            }
            if (this.startAngle == 360) {
                this.startAngle = 0;
            } else {
                this.startAngle += 3;
            }
            if (this.thread == null) {
                this.thread = new Thread(new Runnable() {
                    
                    @Override
                    public void run() {
                        AnimationPanel panel = AnimationPanel.this;
                        try {
                            WaitCircle waitCircle = panel.waitCircle;
                            Frame<?> frame = waitCircle.getFrame();
                            while (waitCircle.isVisible() && waitCircle.isDisabled() == false && frame != null && frame.isVisible()) {
                                Thread.sleep(20);
                                frame = panel.waitCircle.getFrame();
                                panel.waitCircle.getInnerInstance().revalidate();
                                panel.waitCircle.getInnerInstance().repaint();
                            }
                            panel.thread = null;
                        } catch (InterruptedException exception) {
                        }
                    }
                });
                this.thread.start();
            }
        }
    }
}
