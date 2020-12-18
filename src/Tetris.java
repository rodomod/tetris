package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Tetris extends JFrame {

   final String Title = "tetris";
   final int BOX_SIZE = 25;
   final int ARC = 6;
   final int FORM_WIDTH = 10;
   final int FORM_HEIGHT = 21;
   final int DX = 7;
   final int DY = 26;
   final int RUN = 180;
   final int LEFT = 37;
   final int UP = 38;
   final int RIGHT = 39;
   final int DOWN = 40;
   final int SHOW_DEL = 400;
   final int[][][] dot = {
      {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {4, 0x00f0f0}},
      {{0, 0, 0, 0}, {0, 1, 1, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}, {4, 0xf0f000}},
      {{1, 0, 0, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0x0000f0}},
      {{0, 0, 1, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xf0a000}},
      {{0, 1, 1, 0}, {1, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0x00f000}},
      {{1, 1, 1, 0}, {0, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xa000f0}},
      {{1, 1, 0, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xf00000}}
   };
   final int[] score = {100, 300, 700, 1500};
   private int gameScore = 0;
   private int[][] form = new int[FORM_HEIGHT + 1][FORM_WIDTH];
   private JFrame frame;
   private Canvas canvas = new Canvas(this);
   private Random random = new Random();
   private Figure figure = new Figure(this);
   private boolean overGame = false;
   final int[][] MSG = {
      {0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0},
      {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0},
      {1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0},
      {1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0},
      {0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0},
      {1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1},
      {1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1},
      {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0},
      {0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0}
   };

   public static void main(String[] args) {
      new Tetris().init();
   }

   Tetris() {
      setTitle(Title);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setBounds(RUN, RUN, FORM_WIDTH * BOX_SIZE + DX, FORM_HEIGHT * BOX_SIZE + DY);
      setResizable(false);
      setLocationRelativeTo(null);
      canvas.setBackground(Color.black);
      addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            if (!isOverGame()) {
               if (e.getKeyCode() == LEFT) {
                  getFigure().move(e.getKeyCode());
               }
               if (e.getKeyCode() == RIGHT) {
                  getFigure().move(e.getKeyCode());
               }
               if (e.getKeyCode() == UP) {
                  getFigure().rotate();
               }
               if (e.getKeyCode() == DOWN) {
                  getFigure().drop();
               }
            }
            getCanvas().repaint();
         }
      });
      add(BorderLayout.CENTER, canvas);
      setVisible(true);
      Arrays.fill(form[FORM_HEIGHT], 1);
   }

   void init() {
      while (!isOverGame()) {
         try {
            Thread.sleep(SHOW_DEL);
         } catch (InterruptedException e) {
         }
         getCanvas().repaint();
         curentString();
         if (getFigure().touchToWell()) {
            getFigure().fixToWell();
            setFigure(new Figure(this));
            setOverGame(getFigure().crossToWell());

         } else {
            getFigure().crossDown();
         }
      }
   }

   void curentString() {
      int height = FORM_HEIGHT - 1;
      int curentHeight = 0;
      while (height > 0) {
         int curent = 1;
         for (int width = 0 ; width < FORM_WIDTH ; width++) {
            curent *= Integer.signum(getForm()[height][width]);
         }
         if (curent > 0) {
            curentHeight++;
            for (int h = height ; h > 0 ; h--) {
               System.arraycopy(getForm()[h - 1], 0, getForm()[h], 0, FORM_WIDTH);
            }
         } else {
            height--;
         }
      }
      if (curentHeight > 0) {
         setGameScore(getGameScore() + score[curentHeight - 1]);
         setTitle(Title + " : " + getGameScore());
      }
   }

   /**
    * @return the overGame
    */
   public boolean isOverGame() {
      return overGame;
   }

   /**
    * @param overGame the overGame to set
    */
   public void setOverGame(boolean overGame) {
      this.overGame = overGame;
   }

   /**
    * @return the figure
    */
   public Figure getFigure() {
      return figure;
   }

   /**
    * @param figure the figure to set
    */
   public void setFigure(Figure figure) {
      this.figure = figure;
   }

   /**
    * @return the random
    */
   public Random getRandom() {
      return random;
   }

   /**
    * @param random the random to set
    */
   public void setRandom(Random random) {
      this.random = random;
   }

   /**
    * @return the canvas
    */
   public Canvas getCanvas() {
      return canvas;
   }

   /**
    * @param canvas the canvas to set
    */
   public void setCanvas(Canvas canvas) {
      this.canvas = canvas;
   }

   /**
    * @return the frame
    */
   public JFrame getFrame() {
      return frame;
   }

   /**
    * @param frame the frame to set
    */
   public void setFrame(JFrame frame) {
      this.frame = frame;
   }

   /**
    * @return the form
    */
   public int[][] getForm() {
      return form;
   }

   /**
    * @param form the form to set
    */
   public void setForm(int[][] form) {
      this.form = form;
   }

   /**
    * @return the gameScore
    */
   public int getGameScore() {
      return gameScore;
   }

   /**
    * @param gameScore the gameScore to set
    */
   public void setGameScore(int gameScore) {
      this.gameScore = gameScore;
   }

}
