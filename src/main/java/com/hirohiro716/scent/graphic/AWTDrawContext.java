package com.hirohiro716.scent.graphic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * java.awt.Graphics2Dを使って描画命令を実行するコンテキストのクラス。
 */
public class AWTDrawContext extends DrawContext<Graphics2D> {

    /**
     * コンストラクタ。
     * 
     * @param graphics2D
     */
    public AWTDrawContext(Graphics2D graphics2D) {
        super(graphics2D);
    }

    @Override
    protected void setColor(Graphics2D graphics2D, Color color) {
        graphics2D.setColor(color);
    }

    @Override
    protected Font getFont(Graphics2D graphics2D) {
        return graphics2D.getFont();
    }

    @Override
    protected void setFont(Graphics2D graphics2D, Font font) {
        graphics2D.setFont(font);
    }

    /**
     * AWTのフォントサイズの情報を作成する。
     * 
     * @return
     */
    private java.awt.FontMetrics createAWTFontMetrics() {
        // NOTE: 元と同じGraphics2Dインスタンスを使用するとLANDSCAPE印刷で異常値を返すバグ(OpenJDK11で確認)が発生する。
        Graphics2D graphics2d = AWTDrawContext.createGraphics2D();
        graphics2d.setFont(this.getInnerInstance().getFont());
        java.awt.FontMetrics awtFontMetrics = graphics2d.getFontMetrics();
        return awtFontMetrics;
    }

    @Override
    public FontMetrics createFontMetrics(Graphics2D graphics2D) {
        java.awt.FontMetrics awtFontMetrics = this.createAWTFontMetrics();
        return new FontMetrics(awtFontMetrics.getAscent(), awtFontMetrics.getDescent(), awtFontMetrics.getLeading());
    }

    @Override
    protected float measureStringWidth(Graphics2D graphics2D, String string) {
        java.awt.FontMetrics awtFontMetrics = this.createAWTFontMetrics();
        Rectangle2D rectangle2D = awtFontMetrics.getStringBounds(string, graphics2D);
        return (float) rectangle2D.getWidth();
    }

    @Override
    protected void drawSingleLineString(Graphics2D graphics2D, String string, float x, float y) {
        graphics2D.drawString(string, x, y);
    }

    @Override
    protected void setStrokeWidth(Graphics2D graphics2D, float width) {
        BasicStroke basicStroke = (BasicStroke) graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(width, basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(), basicStroke.getDashArray(), basicStroke.getDashPhase()));
    }

    @Override
    protected void setStrokeDashArray(Graphics2D graphics2D, float... dashes) {
        BasicStroke basicStroke = (BasicStroke) graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(basicStroke.getLineWidth(), basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(), dashes, basicStroke.getDashPhase()));
    }

    @Override
    protected void clearStrokeDashArray(Graphics2D graphics2D) {
        BasicStroke basicStroke = (BasicStroke) graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(basicStroke.getLineWidth(), basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(), null, basicStroke.getDashPhase()));
    }

    @Override
    protected void drawLine(Graphics2D graphics2D, float startX, float startY, float endX, float endY) {
        graphics2D.draw(new Line2D.Double(startX, startY, endX, endY));
    }

    @Override
    protected void drawRectangleLine(Graphics2D graphics2D, float x, float y, float width, float height, float diameter) {
        graphics2D.draw(new RoundRectangle2D.Double(x, y, width, height, diameter, diameter));
    }

    @Override
    protected void drawRectangleFill(Graphics2D graphics2D, float x, float y, float width, float height, float diameter) {
        graphics2D.fill(new RoundRectangle2D.Double(x, y, width, height, diameter, diameter));
    }

    @Override
    protected void drawEllipseLine(Graphics2D graphics2D, float x, float y, float width, float height) {
        graphics2D.draw(new Ellipse2D.Double(x, y, width, height));
    }

    @Override
    protected void drawEllipseFill(Graphics2D graphics2D, float x, float y, float width, float height) {
        graphics2D.fill(new Ellipse2D.Double(x, y, width, height));
    }

    @Override
    protected void drawImage(Graphics2D graphics2D, BufferedImage bufferedImage, float x, float y, float width, float height) throws IOException {
        AffineTransform transform = graphics2D.getTransform();
        double scaleX = width / bufferedImage.getWidth();
        double scaleY = height / bufferedImage.getHeight();
        graphics2D.translate(x, y);
        graphics2D.scale(scaleX, scaleY);
        graphics2D.drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        graphics2D.setTransform(transform);
    }

    @Override
    protected void setScale(Graphics2D graphics2D, float scaleX, float scaleY) {
        graphics2D.scale(scaleX, scaleY);
    }
    
    @Override
    protected void setRotate(Graphics2D graphics2D, int angle, float x, float y) {
        graphics2D.rotate(Math.toRadians(angle), x, y);
    }
    
    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param graphics2D
     * @return 新しいインスタンス。
     */
    public static AWTDrawContext newInstance(Graphics2D graphics2D) {
        return new AWTDrawContext(graphics2D);
    }

    /**
     * 仮想でGraphics2Dインスタンスを新しく作成する。
     * 
     * @return
     */
    public static Graphics2D createGraphics2D() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        return (Graphics2D) image.getGraphics();
    }
}
