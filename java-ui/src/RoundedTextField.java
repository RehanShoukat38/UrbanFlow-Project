import javax.swing.*;
import java.awt.*;

public class RoundedTextField extends JTextField {

    public RoundedTextField(int size) {
        super(size);
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.PLAIN, 15));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(255, 255, 255));
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

        g.setColor(new Color(200, 200, 200));
        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);

        super.paintComponent(g0);
    }
}
