public class NodeView {
    private final String name;
    private final double x, y; // logical coords
    private int screenX, screenY;

    public NodeView(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName(){ return name; }
    public double getX(){ return x; }
    public double getY(){ return y; }

    public int getScreenX(){ return screenX; }
    public int getScreenY(){ return screenY; }

    public void setScreenPosition(int sx, int sy) {
        this.screenX = sx;
        this.screenY = sy;
    }
}
