import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    private final Runnable onFinish;

    public SplashScreen(Runnable onFinish) {
        this.onFinish = onFinish;
    }

    public void showSplash() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        // 🔹 GIF LOADER
        ImageIcon gif = new ImageIcon("resources/splash.gif");
        JLabel gifLabel = new JLabel(gif);
        gifLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel title = new JLabel("UrbanFlow", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));

        panel.add(title, BorderLayout.NORTH);
        panel.add(gifLabel, BorderLayout.CENTER);

        setContentPane(panel);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        // 🔥 DELAY → LAUNCH MAIN UI
        new javax.swing.Timer(2200, e -> {
            dispose();
            onFinish.run();
        }).start();
    }
}
