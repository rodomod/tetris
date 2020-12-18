package src;

import java.awt.Color;
import java.awt.Graphics;

final class Box {

   private int x;
   private int y;
   private final Tetris outer;

   public Box(int x, int y, final Tetris outer) {
      super();
      this.outer = outer;
      this.x = x;
      this.y = y;
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
      g.drawRoundRect(x * outer.BOX_SIZE + 1, y * outer.BOX_SIZE + 1, outer.BOX_SIZE - 2, outer.BOX_SIZE - 2, outer.ARC, outer.ARC);
   }

}
