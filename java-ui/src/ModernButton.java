import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {

    private Color gradientStart = new Color(0, 132, 255);
    private Color gradientEnd   = new Color(0, 92, 200);

    private boolean hover = false;

    public ModernButton(String text) {
        super(text);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setOpaque(false);
        setBorderPainted(false);
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover animation
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { 
                hover = true; repaint(); 
            }
            @Override public void mouseExited(MouseEvent e) { 
                hover = false; repaint(); 
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Hover effect → slightly brighter gradient
        Color start = hover ? gradientStart.brighter() : gradientStart;
        Color end   = hover ? gradientEnd.brighter()   : gradientEnd;

        GradientPaint gp = new GradientPaint(0, 0, start, 0, h, end);
        g.setPaint(gp);
        g.fillRoundRect(0, 0, w, h, 18, 18);

        super.paintComponent(g0);
    }
}
