import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Map;
import java.util.Random;

/**
 * =============================================================
 * HeatmapLayer - ULTIMATE-ENHANCED VISUALIZATION
 * =============================================================
 * ADVANCED FEATURES:
 * 1. Fluid Perlin Noise Displacement: Creates a smooth, organic flow.
 * 2. Motion Streak Effect: Visual cue for rapidly changing traffic.
 * 3. Temporal Pulsing: City breathes with the traffic level.
 * 4. Dual-mode Color Mapping: Cool-to-Hot gradient PLUS High-Contrast Binary.
 * =============================================================
 */
public class HeatmapLayer {

    // --- ENHANCED COLOR SPECTRUM ---
    private static final Color COLOR_LOW = new Color(50, 150, 255);      // Blue (Cool)
    private static final Color COLOR_MID = new Color(255, 255, 100);     // Yellow/Amber (Warm)
    private static final Color COLOR_HIGH = new Color(255, 150, 50);     // Orange (Hot)
    private static final Color COLOR_MAX_CORE = new Color(255, 255, 200); // Near-White Core
    
    // NEW: Colors for the ADVANCED ANALYTICS MODE
    private static final Color COLOR_BINARY_HIGH = new Color(255, 0, 0, 200); // Solid Red (Highlight)
    private static final Color COLOR_BINARY_LOW = new Color(0, 0, 0, 0);      // Transparent (Background)

    private double[] trafficIntensity;
    private double[] trafficChangeRate; // NEW: Tracks how quickly intensity is changing
    private double globalTrafficLevel = 1.0;
    private double pulse = 0.0;
    private float temporalScale = 1.0f; // NEW: Used for the Temporal Pulsing effect

    private BufferedImage cachedImage;
    private final Random random = new Random(1133);
    
    // NEW: Perlin Noise generator (Requires a utility class or external library for true Perlin)
    // For simplicity, we'll use a placeholder noise function.
    private PerlinNoiseGenerator noiseGen; 

    // --- ENHANCED VIEW MODE FOR ADVANCED FEATURE ---
    // Assuming this class is used within UrbanFlowPanel which defines ViewMode
    // You would need to pass this enum state in the render call.
    public enum AdvancedMode {
        DEFAULT_GRADIENT,
        ANALYTICS_BINARY
    }
    private AdvancedMode currentAdvancedMode = AdvancedMode.DEFAULT_GRADIENT;

    // ---------------------------------------------------------
    // Initialization
    // ---------------------------------------------------------
    public void initialize(int nodeCount) {
        trafficIntensity = new double[nodeCount];
        trafficChangeRate = new double[nodeCount];
        noiseGen = new PerlinNoiseGenerator(random.nextLong());
        for (int i = 0; i < nodeCount; i++) {
            trafficIntensity[i] = 0.2 + random.nextDouble() * 0.6;
            trafficChangeRate[i] = 0.0;
        }
        cachedImage = null;
    }

    // ---------------------------------------------------------
    // Traffic update (Advanced Temporal Pulsing)
    // ---------------------------------------------------------
    public void updateTraffic() {
        pulse += 0.035;
        if (pulse > Math.PI * 2) pulse = 0;
        
        // ADVANCED FEATURE 3: Temporal Pulsing (City "Breathes")
        // The entire heatmap subtly pulses based on a sine wave.
        temporalScale = (float) (0.95 + 0.1 * Math.sin(pulse * 3)); // Pulse 3x faster than change rate

        double oldIntensity;
        for (int i = 0; i < trafficIntensity.length; i++) {
            oldIntensity = trafficIntensity[i];
            
            double noise = random.nextDouble() * 0.06 - 0.03;
            trafficIntensity[i] = clamp(
                    trafficIntensity[i] * (0.97 + noise),
                    0.1, 1.0
            );
            
            // NEW: Calculate the magnitude of the change for the Motion Streak effect
            trafficChangeRate[i] = Math.abs(trafficIntensity[i] - oldIntensity);
        }
        cachedImage = null;
    }
    
    // ---------------------------------------------------------
    // Set Mode for Advanced Color Mapping
    // ---------------------------------------------------------
    public void setAdvancedMode(AdvancedMode mode) {
        this.currentAdvancedMode = mode;
        cachedImage = null;
    }
    // ---------------------------------------------------------
// Global traffic intensity control (slider / UI)
// ---------------------------------------------------------
public void setGlobalTrafficLevel(double level) {
    globalTrafficLevel = clamp(level, 0.1, 5.0);
    cachedImage = null;
}


    // [setGlobalTrafficLevel is Unchanged]

    // ---------------------------------------------------------
    // Render heatmap (Motion Blur & Opacity)
    // ---------------------------------------------------------
    public void render(
            Graphics2D g,
            int width,
            int height,
            Map<String, UrbanFlowPanel.NodeView> nodes,
            double zoom,
            double tx,
            double ty
    ) {
        if (cachedImage == null) {
            rebuildImage(width, height, nodes, zoom, tx, ty);
        }

        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.55f * temporalScale // Apply Temporal Pulsing
        ));

        // ADVANCED FEATURE 1: Motion Blur/Streak Effect
        // Apply a gentle blur/transform to simulate momentum.
        if (currentAdvancedMode == AdvancedMode.DEFAULT_GRADIENT) {
            AffineTransform oldTx = g.getTransform();
            
            // Apply a slight horizontal/vertical offset based on the pulse for subtle "drift"
            g.translate(2 * Math.sin(pulse), 1 * Math.cos(pulse * 0.5)); 
            
            // The cached image is drawn with this transformation
            g.drawImage(cachedImage, 0, 0, null);
            g.setTransform(oldTx);
        } else {
            // No blur in analytics mode for clear data visibility
            g.drawImage(cachedImage, 0, 0, null);
        }

        g.setComposite(old);
    }

    // ---------------------------------------------------------
    // Image building (Perlin Noise Displacement)
    // ---------------------------------------------------------
    private void rebuildImage(
            int width,
            int height,
            Map<String, UrbanFlowPanel.NodeView> nodes,
            double zoom,
            double tx,
            double ty
    ) {

        cachedImage = new BufferedImage(
                width, height,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = cachedImage.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int i = 0;
        for (UrbanFlowPanel.NodeView n : nodes.values()) {

            double raw = trafficIntensity[i] * globalTrafficLevel;
            double intensity = Math.pow(raw, 3.0); 
            intensity = clamp(intensity, 0.0, 1.0);
            
            // ADVANCED FEATURE 2: Perlin Noise Displacement
            // Offset the center based on a seeded noise function over time.
            double noiseOffset = noiseGen.noise(n.x / 100.0, n.y / 100.0, pulse * 0.5);
            int displacementX = (int) (noiseOffset * 5 * zoom * intensity);
            int displacementY = (int) (noiseGen.noise(n.x / 50.0, n.y / 50.0, pulse * 0.8) * 5 * zoom * intensity);

            int cx = (int) (n.screenX * zoom + tx) + displacementX;
            int cy = (int) (n.screenY * zoom + ty) + displacementY;
            
            int baseRadius = (int) (80 * zoom); 
            int radius = (int) (baseRadius * (1.0 + intensity * 0.5)); 
            
            // NEW: Pass change rate for additional visual effect
            drawHeatCircleWithCore(g, cx, cy, radius, intensity, trafficChangeRate[i]);

            i++;
        }

        g.dispose();
    }

    // ---------------------------------------------------------
    // Heat circle drawing - Two-Pass with Color Mode Switch
    // ---------------------------------------------------------
    private void drawHeatCircleWithCore(
            Graphics2D g,
            int cx,
            int cy,
            int radius,
            double intensity,
            double changeRate // NEW: Use change rate
    ) {
        Color finalBaseColor;
        
        // ADVANCED FEATURE 4: Advanced Color Mapping
        if (currentAdvancedMode == AdvancedMode.ANALYTICS_BINARY) {
            // High-contrast binary mode: highlights only intense nodes
            if (intensity > 0.6) {
                finalBaseColor = COLOR_BINARY_HIGH;
            } else {
                finalBaseColor = COLOR_BINARY_LOW;
            }
        } else {
            // Default continuous gradient mode
            finalBaseColor = getColorForIntensity(intensity);
        }
        
        // --- PASS 1: The Soft Outer Glow ---
        // (Only draw glow if not in transparent analytics mode)
        if (finalBaseColor.getAlpha() > 0) {
             RadialGradientPaint glowPaint = new RadialGradientPaint(
                cx, cy,                                 
                radius,                                 
                new float[]{0.0f, 0.7f, 1.0f},          
                new Color[]{
                    new Color(finalBaseColor.getRed(), finalBaseColor.getGreen(), finalBaseColor.getBlue(), 120), 
                    new Color(finalBaseColor.getRed(), finalBaseColor.getGreen(), finalBaseColor.getBlue(), 40),  
                    new Color(0, 0, 0, 0)                                                         
                }
            );

            g.setPaint(glowPaint);
            g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        }


        // --- PASS 2: The Bright Inner Core (Focus) ---
        // Draw the core highlight if the intensity is high enough
        if (intensity > 0.3) {
            
            // Use the hottest core color, regardless of mode, to draw attention.
            Color coreColor = lerpColor(finalBaseColor, COLOR_MAX_CORE, (float) Math.pow(intensity, 10));

            // Core radius is small and only expands slightly with intensity.
            int coreRadius = (int) (radius * 0.25 + radius * intensity * 0.3);
            
            // Add a visual "flash" to the core based on change rate
            float coreAlpha = 0.8f + (float) (changeRate * 2.0); // Flashes slightly when traffic changes
            coreAlpha = Math.max(0f, Math.min(1f, coreAlpha));



            RadialGradientPaint corePaint = new RadialGradientPaint(
                cx, cy,                                     
                coreRadius,                                 
                new float[]{0.0f, 0.9f, 1.0f},              
                new Color[]{
                    coreColor,                              
                    new Color(coreColor.getRed(), coreColor.getGreen(), coreColor.getBlue(), (int)(100 * coreAlpha)), 
                    new Color(0, 0, 0, 0)                   
                }
            );
            
            Composite oldComposite = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, coreAlpha));

            g.setPaint(corePaint);
            g.fillOval(cx - coreRadius, cy - coreRadius, coreRadius * 2, coreRadius * 2);
            
            g.setComposite(oldComposite); 
        }
    }
    
    // ---------------------------------------------------------
    // Continuous Color Interpolation (Unchanged)
    // ---------------------------------------------------------
    private Color getColorForIntensity(double intensity) {
        if (intensity < 0.33) {
            float t = (float) (intensity / 0.33);
            return lerpColor(COLOR_LOW, COLOR_MID, t);
        } else if (intensity < 0.66) {
            float t = (float) ((intensity - 0.33) / 0.33);
            return lerpColor(COLOR_MID, COLOR_HIGH, t);
        } else {
            float t = (float) ((intensity - 0.66) / 0.34);
            return lerpColor(COLOR_HIGH, COLOR_MAX_CORE, t);
        }
    }
    
    // ---------------------------------------------------------
    // Color Utility: Linear Interpolation (lerp) (Unchanged)
    // ---------------------------------------------------------
    private Color lerpColor(Color c1, Color c2, float t) {
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * t);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t);
        return new Color(r, g, b);
    }

    // ---------------------------------------------------------
    // Utility (Unchanged)
    // ---------------------------------------------------------
    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
    
    // ---------------------------------------------------------
    // Placeholder for a Perlin Noise Generator
    // (A real implementation requires a separate class file)
    // ---------------------------------------------------------
    private static class PerlinNoiseGenerator {
        // Simple placeholder to allow code compilation and demonstrate concept
        private final Random rand;
        public PerlinNoiseGenerator(long seed) {
            this.rand = new Random(seed);
        }
        public double noise(double x, double y, double z) {
            // Returns a repeating pattern for demonstration purposes
            return Math.sin(x * 0.5 + z * 1.0) * Math.cos(y * 0.5 + z * 1.0) * 0.5 + 0.5;
        }
    }
}