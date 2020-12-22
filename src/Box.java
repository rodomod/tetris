import java.awt.*;

final class Box {
    private int x;
    private int y;
    private final Tetris out;

    public Box(int x, int y, final Tetris out) {
        this.x = x;
        this.y = y;
        this.out = out;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    void paint(Graphics g, int color) {
        g.setColor(new Color(color));
        g.drawRoundRect(x * out.BOX_SIZE + 1, y * out.BOX_SIZE + 1, out.BOX_SIZE - 2, out.BOX_SIZE - 2, out.ARC, out.ARC);

    }
}
