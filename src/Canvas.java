import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

class Canvas extends JPanel {
   private final Tetris outer;

   Canvas(final Tetris outer) {
      this.outer = outer;
   }

   @Override
   public void paint(Graphics g) {
      super.paint(g);
      for (int x = 0 ; x < outer.FORM_WIDTH ; x++) {
         for (int y = 0 ; y < outer.FORM_HEIGHT ; y++) {
            if (x < outer.FORM_WIDTH - 1 && y < outer.FORM_HEIGHT - 1) {
               g.setColor(Color.lightGray);
               g.drawLine((x + 1) * outer.BOX_SIZE - 2, (y + 1) * outer.BOX_SIZE, (x + 1) * outer.BOX_SIZE + 2, (y + 1) * outer.BOX_SIZE);
               g.drawLine((x + 1) * outer.BOX_SIZE, (y + 1) * outer.BOX_SIZE - 2, (x + 1) * outer.BOX_SIZE, (y + 1) * outer.BOX_SIZE + 2);
            }
            if (outer.getForm()[y][x] > 0) {
               g.setColor(new Color(outer.getForm()[y][x]));
               g.fill3DRect(x * outer.BOX_SIZE + 1, y * outer.BOX_SIZE + 1, outer.BOX_SIZE - 1, outer.BOX_SIZE - 1, true);
            }
         }
      }
      if (outer.isOverGame()) {
         g.setColor(Color.white);
         for (int y = 0 ; y < outer.MSG.length ; y++) {
            for (int x = 0 ; x < outer.MSG[y].length ; x++) {
               if (outer.MSG[y][x] == 1) {
                  g.fill3DRect(x * 11 + 18, y * 11 + 160, 10, 10, true);
               }
            }
         }
      } else {
         outer.getFigure().paint(g);
      }
   }
   
}
