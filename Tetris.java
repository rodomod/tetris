import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tetris extends JFrame {
    final String Title = "Tetris";
    final int blockSize = 25;   //размер одного блока
    final int ARC = 6;      //радиус закругления блока
    final int stakan_WIDTH = 10;    //размер игрового поля  width
    final int stakan_HEIGHT = 21;  //размер игрового поля height
    final int startLocation = 180;     //стартовая локация
    final int DX = 7;     //определено экспериментально
    final int DY = 26;  //определено экспериментально
    final int LEFT = 37;      //коды клавиш
    final int UP = 38;       //коды клавиш
    final int RIGHT = 39;  //коды клавиш
    final int DOWN = 40;  //коды клавиш
    final int showDel = 400;    //задержка анимации
    final int[][][] point = {
            {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}, {4, 0x00f0f0}}, // I
            {{0,0,0,0}, {0,1,1,0}, {0,1,1,0}, {0,0,0,0}, {4, 0xf0f000}}, // O
            {{1,0,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}, {3, 0x0000f0}}, // J
            {{0,0,1,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}, {3, 0xf0a000}}, // L
            {{0,1,1,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}, {3, 0x00f000}}, // S
            {{1,1,1,0}, {0,1,0,0}, {0,0,0,0}, {0,0,0,0}, {3, 0xa000f0}}, // T
            {{1,1,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}, {3, 0xf00000}}  // Z
    };
    final int[] score = {100,300,500,700};
    int gameScore = 0;
    int[][] stakan = new int[stakan_HEIGHT + 1][stakan_WIDTH];  //мой стакан -> stakan
    JFrame frame;
    Canvas canvas = new Canvas();
    Random random = new Random();
    Figure figure = new Figure();
    boolean overGame = false;
    final int[][] MSG = {
            {0,1,1,0,0,1,0,0,1,0,0,1,1,0,0,1,1,1,0,0},
            {1,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0,1,0},
            {1,0,0,1,0,1,0,1,0,0,1,1,1,1,0,1,1,1,0,0},
            {1,0,0,1,0,1,1,0,0,0,1,0,0,0,0,1,0,0,1,0},
            {0,1,1,0,0,1,0,0,0,0,0,1,1,0,0,1,0,0,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,1,1,0,0,0,1,1,0,0,0,1,0,1,0,0,0,1,1,0},
            {1,0,0,0,0,1,0,0,1,0,1,0,1,0,1,0,1,0,0,1},
            {1,0,1,1,0,1,1,1,1,0,1,0,1,0,1,0,1,1,1,1},
            {1,0,0,1,0,1,0,0,1,0,1,0,1,0,1,0,1,0,0,0},
            {0,1,1,0,0,1,0,0,1,0,1,0,1,0,1,0,0,1,1,0},
            };

    public static void main(String[] args) {

        new Tetris().init();
    }
    Tetris(){
        setTitle(Title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(startLocation, startLocation, stakan_WIDTH * blockSize + DX, stakan_HEIGHT * blockSize + DY);
        setResizable(false);                 //не изменять размер стакана
        setLocationRelativeTo(null);        //игра в центре экрана
        canvas.setBackground(Color.black); // определить цвет фона
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(!overGame) {
                    if(e.getKeyCode() == DOWN) figure.drop();
                    if(e.getKeyCode() == UP) figure.rotate();
                    if(e.getKeyCode() == LEFT || e.getKeyCode() == RIGHT) figure.move(e.getKeyCode());
                }
                canvas.repaint();
            }
        });
        add(BorderLayout.CENTER, canvas);
        setVisible(true);
        Arrays.fill(stakan[stakan_HEIGHT], 1);   //инициализация дна стакана...создаём площадку дна
    }
    void init() {
        while(!overGame) {  // пока неОверГейм крутится цикл while(!overGame)
            try {          //три строки обеспечивают задержку(sleep)анимации...перед прорисовкой
                Thread.sleep(showDel);
            } catch (Exception e) { e.printStackTrace(); }
            canvas.repaint();   //перерисовка
            curentString();    //проверка заполнения строк
            if(figure.isTouchToWell()) { // коснулась ли фигура дна или упавших фигур
                figure.fixToWell();         //фиксируем(изменяем цвет) после касания
                figure = new Figure();     //создаем новую фигуру
                overGame = figure.isCrossToWell(); //Есть ли пространство для новой фигуры?тоесть не закончилась ли игра

            } else {
                figure.isCrossDown(); //фигура продолжает падать
            }
        }
    }
    void curentString() {        //проверка заполнения строк
        int height = stakan_HEIGHT - 1;
        int curentHeight = 0;
        while (height > 0) {
            int curent = 1;
            for(int width = 0; width < stakan_WIDTH; width++)
                curent *= Integer.signum(stakan[height][width]);
            if(curent > 0) {
                curentHeight++;
                for(int i = height; i > 0; i--) System.arraycopy(stakan[i-1], 0, stakan[i], 0, stakan_WIDTH);
            } else
                height--;
        }
        if(curentHeight > 0) {
            gameScore += score[curentHeight - 1];
            setTitle(Title + " : " + gameScore);
        }
    }
    class Figure {
        private ArrayList<Block> figure = new ArrayList<Block>();
        private int[][] well = new int[4][4];
        private int type, size, color;
        private int x = 3, y = 0;   //начало в левом верхнем углу

        Figure() {
            type = random.nextInt(point.length);
            size = point[type][4][0];
            color = point[type][4][1];
            if(size == 4) y = -1;
            for(int i = 0; i < size; i++)
                System.arraycopy(point[type][i], 0, well[i], 0, point[type][i].length);
            createWell();
        }
        void createWell() {  //создать из формы ...
            for(int x = 0; x < size; x++)
                for(int y = 0; y < size; y++)  //проходим по массиву well и создаём нашу фигуру
                    if(well[y][x] == 1) figure.add(new Block(x + this.x, y + this.y));
        }
        boolean isTouchToWell() { //коснулась ли фигура дна
            for(Block block : figure) if(stakan[block.getY() + 1][block.getX()] > 0) return true;
            return false;
        }
        boolean isCrossToWell() { //есть ли пространство для новой фигуры?
            for(Block block : figure) if(stakan[block.getY()][block.getX()] > 0) return true;
            return false;
        }
        void fixToWell() {  //изменение цвета фигуры при фиксации

            for(Block block : figure) stakan[block.getY()][block.getX()] = color;
        }
        boolean TouchToWall(int col) {  //проверка на косание фигурой стены LEFT,RIGHT...
            for(Block block : figure) {
                if(col == LEFT && (block.getX() == 0 || stakan[block.getY()][block.getX() - 1] > 0)) return true;
                if(col == RIGHT && (block.getX() == stakan_WIDTH - 1 || stakan[block.getY()][block.getX() + 1] > 0)) return true;
            }
            return false;
        }
        void move(int col) {   //передвижение фигуры в право или лево
            if(!TouchToWall(col)) {
                int dx = col - 38;      // LEFT = -1, RIGHT = 1
                for(Block block : figure) block.setX(block.getX() + dx);
                x += dx;
            }
        }
        void isCrossDown() {   //падение фигуры
            for(Block block : figure) block.setY(block.getY() + 1);
            y++;
        }
        void drop() { // быстрое падение в низ стакана

            while(!isTouchToWell()) isCrossDown();
        }
        boolean collidesAt() { // /**проверка на выход за пределы стакана(коллизии вращения фигуры)*/
            for(int x = 0; x < size; x++)
                for(int y = 0; y < size; y++)
                    if(well[y][x] == 1) {
                        if(y + this.y < 0) return true;
                        if(x + this.x < 0 || x + this.x > stakan_WIDTH - 1) return true;
                        if(stakan[y + this.y][x + this.x] > 0) return true;
                    }
            return false;
        }
        void rotateWell(int col) { // ротация образа фигуры
            for(int i = 0; i < size/2; i++)
                for(int j = i; j < size-1-i; j++)
                    if(col == RIGHT) {    //по часовой стрелке
                        int tmp = well[size-1-j][i];
                        well[size-1-j][i] = well[size-1-i][size-1-j];
                        well[size-1-i][size-1-j] = well[j][size-1-i];
                        well[j][size-1-i] = well[i][j];
                        well[i][j] = tmp;
                    } else {    //против часовой стрелки
                        int tmp = well[i][j];
                        well[i][j] = well[j][size-1-i];
                        well[j][size-1-i] = well[size-1-i][size-1-j];
                        well[size-1-i][size-1-j] = well[size-1-j][i];
                        well[size-1-j][i] = tmp;
                    }
        }
        void rotate() {    //вращение фигуры  ротация
            rotateWell(RIGHT);
            if(!collidesAt()) {
                figure.clear();
                createWell();
            } else
                rotateWell(LEFT);
        }
        void paint(Graphics g) {

            for(Block block : figure) block.paint(g, color);
        }
    }
    class Block {   //блок -- строительный элемент для рисунка
        private int x, y;

        public Block(int x, int y) {
            setX(x);
            setY(y);
        }

        void setX(int x) { this.x = x; }
        void setY(int y) { this.y = y; }

        int getX() { return x; }
        int getY() { return y; }

        void paint(Graphics g, int color) {
            g.setColor(new Color(color));
            g.drawRoundRect(x* blockSize +1, y* blockSize +1, blockSize -2, blockSize -2, ARC, ARC);
        }
    }
    class Canvas extends JPanel {      // холст для рисования
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for(int x = 0; x < stakan_WIDTH; x++)
                for(int y = 0; y < stakan_HEIGHT; y++) {
                    if(x < stakan_WIDTH - 1 && y < stakan_HEIGHT - 1) {
                        g.setColor(Color.lightGray);
                        g.drawLine((x+1)* blockSize -2, (y+1)* blockSize, (x+1)* blockSize +2, (y+1)* blockSize);
                        g.drawLine((x+1)* blockSize, (y+1)* blockSize -2, (x+1)* blockSize, (y+1)* blockSize +2);
                    }
                    if(stakan[y][x] > 0) {
                        g.setColor(new Color(stakan[y][x]));
                        g.fill3DRect(x* blockSize +1, y* blockSize +1, blockSize -1, blockSize -1, true);
                    }
                }
            if(overGame) {
                g.setColor(Color.white);
                for(int y = 0; y < MSG.length; y++)
                    for(int x = 0; x < MSG[y].length; x++)
                        if(MSG[y][x] == 1) g.fill3DRect(x*11+18, y*11+160, 10, 10, true);
            } else
                figure.paint(g);
        }
    }
}