import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Random;

public class WelcomePanel extends JPanel {

    private float opacity = 0.0f;
    private float pulseValue = 0.0f;
    private float shinePos = -1.0f; // Controls the position of the light streak
    private int mouseX = 0, mouseY = 0; 
    private Timer animationTimer;
    private ArrayList<Bubble> bubbles;

    public WelcomePanel() {
        setLayout(new GridBagLayout());
        setOpaque(true);
        initBubbles();

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        // The elegant central card
        JPanel glassCard = createElegantCard();
        add(glassCard);

        // Core Animation Timer
        animationTimer = new Timer(30, e -> {
            if (opacity < 1.0f) opacity += 0.015f;
            pulseValue += 0.04f;
            
            // Move the shine reflection across the card
            shinePos += 0.015f;
            if (shinePos > 2.0f) shinePos = -1.5f; // Loop the shine

            updateBubbles();
            repaint();
        });
        animationTimer.start();
    }

    private void initBubbles() {
        bubbles = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 15; i++) {
            bubbles.add(new Bubble(rand.nextInt(1600), rand.nextInt(1000)));
        }
    }

    private void updateBubbles() {
        for (Bubble b : bubbles) {
            b.move(getWidth(), getHeight());
        }
    }

    private JPanel createElegantCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();

                // 1. Frosted Glass Body
                g2.setColor(new Color(255, 255, 255, (int) (opacity * 18)));
                g2.fillRoundRect(0, 0, w, h, 40, 40);

                // 2. Moving Shine Reflection
                float x = shinePos * w;
                LinearGradientPaint shine = new LinearGradientPaint(
                    new Point( (int)x, 0), new Point((int)x + 100, h),
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(255, 255, 255, 0), 
                                new Color(255, 255, 255, (int)(opacity * 40)), 
                                new Color(255, 255, 255, 0)}
                );
                g2.setPaint(shine);
                g2.fillRoundRect(0, 0, w, h, 40, 40);

                // 3. Elegant Border
                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(new Color(255, 255, 255, (int)(opacity * 80)));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 40, 40);
                
                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(50, 80, 50, 80));

        // Typography Container
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JLabel urbanLabel = new JLabel("URBAN");
        urbanLabel.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 54));
        urbanLabel.setForeground(new Color(255, 255, 255, 210));

        JLabel flowLabel = new JLabel("FLOW");
        flowLabel.setFont(new Font("Segoe UI", Font.BOLD, 54));
        flowLabel.setForeground(Color.WHITE);

        logoPanel.add(urbanLabel);
        logoPanel.add(flowLabel);

        // Breathing Slogan
        JLabel slogan = new JLabel("REDEFINING THE PULSE OF LOGISTICS") {
            @Override
            protected void paintComponent(Graphics g) {
                float alpha = (float) (Math.sin(pulseValue) * 0.3 + 0.7);
                setForeground(new Color(0, 210, 255, (int)(opacity * 255 * alpha)));
                super.paintComponent(g);
            }
        };
        slogan.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(logoPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(slogan);

        return card;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();

        // Background Parallax
        int offX = (mouseX - w / 2) / 40;
        int offY = (mouseY - h / 2) / 40;

        // Cinematic Gradient Background
        GradientPaint bg = new GradientPaint(0, 0, new Color(4, 10, 25), w, h, new Color(10, 25, 60));
        g2.setPaint(bg);
        g2.fillRect(0, 0, w, h);

        // Render Elegant Bubbles
        for (Bubble b : bubbles) {
            int bx = (int)b.x + (offX / b.layer);
            int by = (int)b.y + (offY / b.layer);
            
            // Soft Outer Glow
            g2.setColor(new Color(0, 200, 255, (int) (opacity * b.alpha * 0.2)));
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(bx, by, b.size, b.size);
            
            // Main Bubble Stroke
            g2.setColor(new Color(255, 255, 255, (int) (opacity * b.alpha)));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawOval(bx, by, b.size, b.size);
        }

        // Corner Ambient Glows
        g2.setColor(new Color(0, 255, 255, (int) (opacity * 15)));
        g2.fillOval(w - 400 + offX, -150 + offY, 550, 550);
        g2.setColor(new Color(130, 50, 255, (int) (opacity * 12)));
        g2.fillOval(-150 - offX, h - 350 - offY, 450, 450);
    }

    private class Bubble {
        float x, y, speed;
        int size, alpha, layer;
        Random r = new Random();

        Bubble(int x, int y) {
            this.x = x;
            this.y = y;
            this.speed = 0.2f + r.nextFloat() * 0.4f;
            this.size = 30 + r.nextInt(70); 
            this.alpha = 15 + r.nextInt(35);
            this.layer = 1 + r.nextInt(4); // Parallax layering
        }

        void move(int w, int h) {
            y -= speed;
            if (y < -size) {
                y = h + size;
                x = r.nextInt(w > 0 ? w : 1600);
            }
        }
    }
}