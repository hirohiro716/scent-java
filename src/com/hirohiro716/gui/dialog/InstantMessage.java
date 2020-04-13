package com.hirohiro716.gui.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;
import javax.swing.JWindow;

import com.hirohiro716.graphics.GraphicalString;
import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.HorizontalAlignment;
import com.hirohiro716.gui.VerticalAlignment;
import com.hirohiro716.gui.control.Label;
import com.hirohiro716.gui.control.Pane;
import com.hirohiro716.gui.event.ChangeListener;

/**
 * インスタントメッセージのクラス。
 * 
 * @author hiro
 *
 */
public class InstantMessage extends Component<JWindow> {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected InstantMessage(JWindow innerInstance) {
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
        this.getInnerInstance().addWindowListener(this.windowListener);
        this.label = new Label();
        this.label.setTextAlignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        this.label.setForegroundColor(Color.WHITE);
        this.label.setBackgroundColor(null);
        this.pane.getChildren().add(this.label);
    }

    /**
     * コンストラクタ。<br>
     * このメッセージのオーナーを指定する。
     * 
     * @param owner
     */
    public InstantMessage(Frame<?> owner) {
        this(new JWindow(owner.getInnerInstance()));
        this.owner = owner;
    }

    private Frame<?> owner;
    
    private Pane pane;
    
    private Label label;
    
    /**
     * このメッセージに表示する文字列を取得する。
     * 
     * @return 結果。
     */
    public String getText() {
        return this.label.getText();
    }
    
    /**
     * このメッセージに表示する文字列をセットする。
     * 
     * @param text
     */
    public void setText(String text) {
        this.label.setText(text);
    }
    
    /**
     * このメッセージの前景色を取得する。
     * 
     * @return 結果。
     */
    public Color getForegroundColor() {
        return this.label.getForegroundColor();
    }
    
    /**
     * このメッセージに前景色をセットする。
     * 
     * @param color
     */
    public void setForegroundColor(Color color) {
        this.label.setForegroundColor(color);
    }
    
    private Color backgroundColor = new Color(0, 0, 0, 0.4f);
    
    @Override
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    @Override
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    @Override
    public void updateDisplay() {
        this.getInnerInstance().setBackground(this.backgroundColor);
        this.getInnerInstance().setShape(new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), 3, 3));
        super.updateDisplay();
    }

    @Override
    public void updateLayout() {
        super.updateLayout();
        this.updateDisplay();
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
        // Calculate text size
        Graphics2D graphics = GraphicalString.createGraphics2D();
        graphics.setFont(this.label.getFont());
        GraphicalString graphicalString = new GraphicalString(this.label.getText(), graphics);
        Dimension dimension = graphicalString.createDimension();
        // Display message
        this.label.setSize(this.getSize());
        int padding = this.label.getFont().getSize();
        this.setSize(dimension.width + padding * 2, dimension.height + padding * 2);
        this.updateLocation();
        this.getInnerInstance().setVisible(true);
        // Auto close
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
}
