package extra;

import javax.swing.JButton;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.Shape;

/**
 * Botoi formako botoia.
 */
public class EstrellitaButton extends JButton {

    private boolean faboritoa = false;
    private Shape estrellaShape;

    public EstrellitaButton(boolean bal) {
        setOpaque(true);  // fondo solidoa
        setContentAreaFilled(true);
        setBorderPainted(false);
        setFocusPainted(false);
        setPreferredSize(new Dimension(50, 50));
        recalcularEstrella();  // forma iniziala kalkulatu
        faboritoa=bal;
    }

    public boolean isFavorito() {
        return faboritoa;
    }

    public void setFavorito(boolean faborito) {
        this.faboritoa = faborito;
        repaint();
    }

    public void toggle() {
        setFavorito(!isFavorito());
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        recalcularEstrella();  // rekalkulatu egiten da tamaina aldatzen bada
    }

    private void recalcularEstrella() {
        estrellaShape = getEstrellitaShape();
    }

    private Shape getEstrellitaShape() {
        int size = Math.min(getWidth(), getHeight());
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double outerRadius = size * 0.4;
        double innerRadius = outerRadius * 0.38;

        GeneralPath estrella = new GeneralPath();
        for (int i = 0; i < 10; i++) {
            double angle = -Math.PI / 2 - i * Math.PI / 5;
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));

            if (i == 0) estrella.moveTo(x, y);
            else estrella.lineTo(x, y);
        }
        estrella.closePath();
        return estrella;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // CLIPPING: solo pinta DENTRO de la forma de estrella
        g2d.setClip(estrellaShape);

        // Fondo según estado
        if (faboritoa) {
            g2d.setColor(new Color(255, 215, 0)); // dorado
        } else {
            g2d.setColor(new Color(169, 169, 169)); // gris
        }
        g2d.fillRect(0, 0, getWidth(), getHeight());  // rellena el clip

        g2d.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(2f));
        g2d.draw(estrellaShape);
        
        g2d.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        // Detecta clics SOLO dentro de la estrella
        return estrellaShape.contains(x, y);
    }
}
