package src;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

@SuppressWarnings("serial")
class Canvas extends JPanel {

   private final Tetris outer;

   Canvas(final Tetris outer) {
      this.outer = outer;
   }

   @Override
   public void paint(Graphics g) {
      super.paint(g);
      for (int w = 0 ; w < outer.FORM_WIDTH ; w++) {
         for (int h = 0 ; h < outer.FORM_HEIGHT ; h++) {
            if (w < outer.FORM_WIDTH - 1 && h < outer.FORM_HEIGHT - 1) {
               g.setColor(Color.lightGray);
               g.drawLine((w + 1) * outer.BOX_SIZE - 2, (h + 1) * outer.BOX_SIZE, (w + 1) * outer.BOX_SIZE + 2, (h + 1) * outer.BOX_SIZE);
               g.drawLine((w + 1) * outer.BOX_SIZE, (h + 1) * outer.BOX_SIZE - 2, (w + 1) * outer.BOX_SIZE, (h + 1) * outer.BOX_SIZE + 2);
            }
            if (outer.getForm()[h][w] > 0) {
               g.setColor(new Color(outer.getForm()[h][w]));
               g.fill3DRect(w * outer.BOX_SIZE + 1, h * outer.BOX_SIZE + 1, outer.BOX_SIZE - 1, outer.BOX_SIZE - 1, true);
            }
         }
      }
      if (outer.isOverGame()) {
         g.setColor(Color.white);
         for (int h = 0 ; h < outer.MSG.length ; h++) {
            for (int w = 0 ; w < outer.MSG[h].length ; w++) {
               if (outer.MSG[h][w] == 1) {
                  g.fill3DRect(w * 11 + 18, h * 11 + 160, 10, 10, true);
               }
            }
         }
      } else {
         outer.getFigure().paint(g);
      }
   }

}
