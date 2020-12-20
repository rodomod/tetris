import java.awt.Color;
import java.awt.Graphics;

final class Box {
   private int x;
   private int y;
   private final Tetris outer;

   public Box(int x, int y, final Tetris outer) {
      this.outer = outer;
      setX(x);
      setY(y);
   }

   void setX(int x) {
      this.x = x;
   }

   void setY(int y) {
      this.y = y;
   }

   int getX() {
      return x;
   }

   int getY() {
      return y;
   }

   void paint(Graphics g, int color) {
      g.setColor(new Color(color));
      g.drawRoundRect(x * outer.BOX_SIZE + 1, y * outer.BOX_SIZE + 1, outer.BOX_SIZE - 2, outer.BOX_SIZE - 2, outer.ARC, outer.ARC);
   }
   
}
