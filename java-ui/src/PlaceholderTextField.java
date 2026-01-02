import javax.swing.*;
import java.awt.*;

public class PlaceholderTextField extends RoundedTextField {

    private String placeholder = "";

    public PlaceholderTextField(String placeholder) {
        super(20);
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(150, 150, 150));
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            g2.drawString(placeholder, 14, getHeight()/2 + 5);
        }
    }
}
