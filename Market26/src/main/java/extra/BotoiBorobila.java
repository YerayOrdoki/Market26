package extra;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class BotoiBorobila extends JButton {

    
    public BotoiBorobila() {
        super();
        setup();
    }

  
    public BotoiBorobila(String texto) {
        super(texto);
        setup();
    }

    private void setup() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setBackground(new Color(70, 130, 180));
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isPressed()) {
            g2.setColor(getBackground().darker());
        } else if (getModel().isRollover()) {
            g2.setColor(getBackground().brighter());
        } else {
            g2.setColor(getBackground());
        }
        
   
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));
        
        super.paintComponent(g);
        g2.dispose();
    }
    
    public void setColourRED() {
    	setBackground(new Color(200, 0, 0));
    }
    public void setColourBLUE() {
    	setBackground(new Color(70, 130, 180));
    }
    public void setColourGREEN() {
    	setBackground(new Color(0, 165, 0));
    }
    public void setColourMOREA() {
    	setBackground(new Color(114, 60, 196));
    }
    
    
}