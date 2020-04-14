package com.hirohiro716.gui.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import com.hirohiro716.graphics.GraphicalString;
import com.hirohiro716.graphics.GraphicalString.HorizontalPosition;
import com.hirohiro716.graphics.GraphicalString.VerticalPosition;
import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.control.Pane;
import com.hirohiro716.gui.event.ChangeListener;

/**
 * インスタントメッセージのクラス。
 * 
 * @author hiro
 *
 */
public class InstantMessage extends Component<InstantMessage.JWindowForInstantMessage> {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    private InstantMessage(JWindowForInstantMessage innerInstance) {
        super(innerInstance, innerInstance);
        InstantMessage instance = this;
        this.addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                instance.updateDisplay();
            }
        });
        this.pane = Pane.newInstance((JPanel) this.getInnerInstance().getContentPane());
        this.pane.setParent(this);
        this.pane.setBackgroundColor(null);
        this.getInnerInstance().addWindowListener(this.windowListener);
    }

    /**
     * コンストラクタ。<br>
     * このメッセージのオーナーを指定する。
     * 
     * @param owner
     */
    public InstantMessage(Frame<?> owner) {
        this(new JWindowForInstantMessage(owner.getInnerInstance()));
        this.getInnerInstance().setInstantMessage(this);
        this.owner = owner;
    }

    private Frame<?> owner;
    
    private Pane pane;
    
    private String text;
    
    /**
     * このメッセージに表示する文字列を取得する。
     * 
     * @return 結果。
     */
    public String getText() {
        return this.text;
    }
    
    /**
     * このメッセージに表示する文字列をセットする。
     * 
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    private Color foregroundColor = Color.WHITE;
    
    /**
     * このメッセージの前景色を取得する。
     * 
     * @return 結果。
     */
    public Color getForegroundColor() {
        return this.foregroundColor;
    }
    
    /**
     * このメッセージに前景色をセットする。
     * 
     * @param color
     */
    public void setForegroundColor(Color color) {
        this.foregroundColor = color;
    }
    
    private Color backgroundColor = Color.DARK_GRAY;
    
    @Override
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    @Override
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }
    
    /**
     * このメッセージの場所をオーナーの位置に合わせる。
     */
    private void updateLocation() {
        int x = this.owner.getX() + this.owner.getWidth() / 2 - this.getWidth() / 2;
        int y = this.owner.getY() + this.owner.getHeight() / 2 - this.getHeight() / 2;
        this.setLocation(x, y);
    }
    
    /**
     * このメッセージのオーナーの位置が変更された場合のリスナー。
     */
    private ChangeListener<Point> locationChangeListener = new ChangeListener<Point>() {

        @Override
        protected void changed(Component<?> component, Point changedValue, Point valueBeforeChange) {
            InstantMessage instance = InstantMessage.this;
            instance.updateLocation();
        }
    };
    
    /**
     * 表示時間を指定して、このメッセージを表示する。
     * 
     * @param closeAfterMillisecond このミリ秒数を経過後に自動的にメッセージを閉じる。
     */
    public void show(Integer closeAfterMillisecond) {
        this.getInnerInstance().setVisible(true);
        if (closeAfterMillisecond != null) {
            Thread thread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    InstantMessage instance = InstantMessage.this;
                    try {
                        Thread.sleep(closeAfterMillisecond);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                    GUI.executeLater(new Runnable() {
                        
                        @Override
                        public void run() {
                            instance.close();
                        }
                    });
                }
            });
            thread.start();
        }
    }
    
    /**
     * このメッセージを表示する。
     */
    public void show() {
        this.show(null);
    }
    
    /**
     * このメッセージを非表示にする。
     */
    public void hide() {
        this.getInnerInstance().setVisible(false);
    }
    
    /**
     * このメッセージを閉じる。
     */
    public void close() {
        this.getInnerInstance().dispose();
    }

    private WindowListener windowListener = new WindowListener() {
        
        @Override
        public void windowOpened(WindowEvent event) {
            InstantMessage instance = InstantMessage.this;
            instance.owner.addLocationChangeListener(instance.locationChangeListener);
        }
        
        @Override
        public void windowIconified(WindowEvent event) {
        }
        
        @Override
        public void windowDeiconified(WindowEvent event) {
        }
        
        @Override
        public void windowDeactivated(WindowEvent event) {
        }
        
        @Override
        public void windowClosing(WindowEvent event) {
        }
        
        @Override
        public void windowClosed(WindowEvent event) {
            InstantMessage instance = InstantMessage.this;
            instance.owner.removeChangeListener(instance.locationChangeListener);
            instance.getInnerInstance().dispose();
        }
        
        @Override
        public void windowActivated(WindowEvent event) {
        }
    };
    
    /**
     * このインスタントメッセージで使用するJWindow拡張クラス。
     * 
     * @author hiro
     *
     */
    @SuppressWarnings("serial")
    protected static class JWindowForInstantMessage extends javax.swing.JWindow {
        
        /**
         * コンストラクタ。<br>
         * このJWindowのオーナーを指定する。
         * 
         * @param owner
         */
        public JWindowForInstantMessage(Window owner) {
            super(owner);
        }
        
        private InstantMessage instantMessage;
        
        /**
         * このJWindowにインスタントメッセージのインスタンスをセットする。
         * 
         * @param instantMessage
         */
        public void setInstantMessage(InstantMessage instantMessage) {
            this.instantMessage = instantMessage;
        }
        
        @Override
        public void paint(Graphics graphics) {
            graphics.clearRect(0, 0, this.getWidth(), this.getHeight());
            // Initial settings
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics2D.setFont(this.instantMessage.pane.getFont());
            // Calculate text size
            int fontSize = graphics2D.getFont().getSize();
            int paddingX = (int) (fontSize * 1.5);
            int paddingY = (int) (fontSize * 1.2);
            int maximumWidth = this.instantMessage.owner.getWidth() - paddingX * 4;
            GraphicalString graphicalString = new GraphicalString(this.instantMessage.text, graphics2D);
            graphicalString.setMaximumWidth(maximumWidth);
            Dimension textSize = graphicalString.createDimension();
            // Calculate window size
            int windowWidth = textSize.width + paddingX * 2;
            int windowHeight = textSize.height + paddingY * 2;
            if (windowWidth != this.getWidth() || windowHeight != this.getHeight()) {
                this.setSize(windowWidth, windowHeight);
                this.instantMessage.updateLocation();
                
            }
            // Draw background
            int arc = fontSize * 2;
            RoundRectangle2D rectangle = new RoundRectangle2D.Double(0, 0, windowWidth, windowHeight, arc, arc);
            this.setShape(rectangle);
            graphics2D.setColor(this.instantMessage.backgroundColor);
            graphics2D.fill(rectangle);
            // Draw text
            graphics2D.setColor(this.instantMessage.foregroundColor);
            graphicalString.setHorizontalPosition(HorizontalPosition.LEFT);
            graphicalString.setVerticalPosition(VerticalPosition.CENTER);
            graphicalString.drawInBox(paddingX, paddingY, textSize.width, textSize.height);
        }
    }
    
    /**
     * テキスト、閉じるまでの時間(ミリ秒)、オーナーを指定してインスタントメッセージを表示する。
     * 
     * @param text
     * @param closeAfterMillisecond
     * @param owner
     */
    public static void show(String text, int closeAfterMillisecond, Frame<?> owner) {
        InstantMessage instance = new InstantMessage(owner);
        instance.setText(text);
        instance.show(closeAfterMillisecond);
    }
}
