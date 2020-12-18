package src;

import java.awt.Graphics;
import java.util.ArrayList;

final class Figure {

   private final int[][] well = new int[4][4];
   private final ArrayList<Box> figure = new ArrayList<Box>();
   private final int type;
   private final int size;
   private final int color;
   private int y = 0;
   private int x = 3;
   private final Tetris outer;

   Figure(final Tetris outer) {
      this.outer = outer;
      type = outer.getRandom().nextInt(outer.dot.length);
      size = outer.dot[type][4][0];
      color = outer.dot[type][4][1];
      if (size == 4) {
         y = -1;
      }
      for (int quar = 0 ; quar < size ; quar++) {
         System.arraycopy(outer.dot[type][quar], 0, well[quar], 0, outer.dot[type][quar].length);
      }
      createWell();
   }

   void createWell() {

      for (int w = 0 ; w < size ; w++) {
         for (int h = 0 ; h < size ; h++) {
            if (well[h][w] == 1) {
               figure.add(new Box(w + this.x, h + this.y, outer));
            }
         }
      }
   }

   boolean touchToWell() {
      return figure.stream().anyMatch((box) -> (outer.getForm()[box.getY() + 1][box.getX()] > 0));
   }

   boolean crossToWell() {
      return figure.stream().anyMatch((box) -> (outer.getForm()[box.getY()][box.getX()] > 0));
   }

   void fixToWell() {
      figure.stream().forEach((box) -> {
         outer.getForm()[box.getY()][box.getX()] = color;
      });
   }

   boolean widthToWell(int col) {

      for (Box box : figure) {
         if (col == outer.LEFT && (box.getX() == 0 || outer.getForm()[box.getY()][box.getX() - 1] > 0)) {
            return true;
         }
         if (col == outer.RIGHT && (box.getX() == outer.FORM_WIDTH - 1 || outer.getForm()[box.getY()][box.getX() + 1] > 0)) {
            return true;
         }
      }
      return false;
   }

   void move(int col) {

      if (!widthToWell(col)) {
         int dx = col - 38;
         figure.stream().forEach((box) -> {
            box.setX(box.getX() + dx);
         });
         x += dx;
      }
   }

   void crossDown() {
      figure.stream().forEach((box) -> {
         box.setY(box.getY() + 1);
      });
      y++;
   }

   void drop() {

      while (!touchToWell()) {
         crossDown();
      }
   }

   boolean collidesAt() {

      for (int w = 0 ; w < size ; w++) {
         for (int h = 0 ; h < size ; h++) {
            if (well[h][w] == 1) {
               if (h + this.y < 0) {
                  return true;
               }
               if (w + this.x < 0 || w + this.x > outer.FORM_WIDTH - 1) {
                  return true;
               }
               if (outer.getForm()[h + this.y][w + this.x] > 0) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   void rotateWell(int col) {

      for (int w = 0 ; w < size / 2 ; w++) {
         for (int h = w ; h < size - 1 - w ; h++) {
            if (col == outer.RIGHT) {

               int tmp = well[size - 1 - h][w];
               well[size - 1 - h][w] = well[size - 1 - w][size - 1 - h];
               well[size - 1 - w][size - 1 - h] = well[h][size - 1 - w];
               well[h][size - 1 - w] = well[w][h];
               well[w][h] = tmp;
            } else {

               int tmp = well[w][h];
               well[w][h] = well[h][size - 1 - w];
               well[h][size - 1 - w] = well[size - 1 - w][size - 1 - h];
               well[size - 1 - w][size - 1 - h] = well[size - 1 - h][w];
               well[size - 1 - h][w] = tmp;
            }
         }
      }
   }

   void rotate() {

      rotateWell(outer.RIGHT);
      if (!collidesAt()) {
         figure.clear();
         createWell();
      } else {
         rotateWell(outer.LEFT);
      }
   }

   void paint(Graphics g) {
      figure.stream().forEach((Box box) -> {
         box.paint(g, color);
      });
   }

}
