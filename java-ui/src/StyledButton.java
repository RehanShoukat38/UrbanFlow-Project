import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StyledButton extends JButton {

    private Color baseColor;
    private static final int ARC_SIZE = 30; // Creates the pill shape

    public StyledButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        
        // Standard Look and Feel settings
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        // Taller button for better visual weight
        setPreferredSize(new Dimension(200, 40)); 
        setMinimumSize(new Dimension(100, 40));
    }

    // Overridden method for custom painting
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        
        // Determine the color based on button state (for an impressive effect)
        Color paintColor = baseColor;
        if (getModel().isArmed() || getModel().isPressed()) {
            paintColor = baseColor.darker(); // Darker when clicked
        } else if (getModel().isRollover()) {
            paintColor = baseColor.brighter(); // Brighter on hover (requires MouseListener for full effect)
        }
        
        // Fill the rounded rectangle background
        g2.setColor(paintColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, w, h, ARC_SIZE, ARC_SIZE));
        
        // Draw the text
        g2.dispose();
        super.paintComponent(g);
    }
}