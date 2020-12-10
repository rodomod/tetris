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
    final int BOX_SIZE = 25;  //размер одного блока
    final int ARC = 6;      //радиус закругления блока
    final int FORM_WIDTH = 10;   //размер игрового поля  width
    final int FORM_HEIGHT = 21; //размер игрового поля height
    final int RUN = 180;     //стартовая локация
    final int DX = 7;         //new X
    final int DY = 26;       //new Y
    final int LEFT = 37;    //коды клавиш 
    final int UP = 38;     //коды клавиш 
    final int RIGHT = 39; //коды клавиш
    final int DOWN = 40; //коды клавиш
    final int SHOW_DEL = 400;    //задержка анимации
    final int[][][] dot = {
            {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}, {4, 0x00f0f0}}, // I
            {{0,0,0,0}, {0,1,1,0}, {0,1,1,0}, {0,0,0,0}, {4, 0xf0f000}}, // O
            {{1,0,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}, {3, 0x0000f0}}, // J
            {{0,0,1,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}, {3, 0xf0a000}}, // L
            {{0,1,1,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}, {3, 0x00f000}}, // S
            {{1,1,1,0}, {0,1,0,0}, {0,0,0,0}, {0,0,0,0}, {3, 0xa000f0}}, // T
            {{1,1,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}, {3, 0xf00000}}  // Z
    };
    final int[] score = {155,355,755,1555};
    int gameScore = 0;
    int[][] form = new int[FORM_HEIGHT + 1][FORM_WIDTH];  // игровое поле form
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
        setBounds(RUN, RUN, FORM_WIDTH * BOX_SIZE + DX, FORM_HEIGHT * BOX_SIZE + DY);
        setResizable(false);                 //не изменять размер игрового поля
        setLocationRelativeTo(null);        //игра в центре экрана
        canvas.setBackground(Color.black); //цвет фона
        addKeyListener(new KeyAdapter() {
            @Override
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
        Arrays.fill(form[FORM_HEIGHT], 1); //инициализация дна...создаём площадку дна
    }
    void init() {
        while(!overGame) {  // пока неОверГейм(!overGame) крутится цикл while
            try {          // задержка анимации...перед перерисовкой
               Thread.sleep(SHOW_DEL);
            } catch (InterruptedException e) {}
            canvas.repaint();   //перерисовка
            curentString();    //проверка заполнения строк
            if(figure.touchToWell()) { // коснулась ли фигура дна или упавших фигур
                figure.fixToWell();         //фиксируем(изменяем цвет фигуры)после касания
                figure = new Figure();     //создаем новую фигуру
                overGame = figure.crossToWell(); //Есть ли пространство для новой фигуры, не закончилась ли игра

            } else {
                figure.crossDown(); //фигура продолжает падать
            }
        }
    }
    void curentString() {        //проверка заполнения строк
        int height = FORM_HEIGHT - 1;
        int curentHeight = 0;
        while (height > 0) {
            int curent = 1;
            for(int width = 0; width < FORM_WIDTH; width++)
                curent *= Integer.signum(form[height][width]);
            if(curent > 0) {
                curentHeight++;
                for(int i = height; i > 0; i--) System.arraycopy(form[i-1], 0, form[i], 0, FORM_WIDTH);
            } else {
                height--;
            }
        }
        if(curentHeight > 0) {
            gameScore += score[curentHeight - 1];
            setTitle(Title + " : " + gameScore);
        }
    }
    final class Figure {
        private final ArrayList<Box> figure = new ArrayList<Box>();
        private final int[][] well = new int[4][4];
        private final int type, size, color;
        private int x = 3, y = 0;   //начало в левом верхнем углу

        Figure() {
            type = random.nextInt(dot.length);
            size = dot[type][4][0];
            color = dot[type][4][1];
            if(size == 4) y = -1;
            for(int i = 0; i < size; i++)
                System.arraycopy(dot[type][i], 0, well[i], 0, dot[type][i].length);
            createWell();
                 }
        void createWell() {  //создать фигуру ...
            for(int x = 0; x < size; x++)
                for(int y = 0; y < size; y++)  //проходим по массиву well и создаём нашу фигуру
                    if(well[y][x] == 1) figure.add(new Box(x + this.x, y + this.y));
        }
        boolean touchToWell() { //коснулась ли фигура дна
            for(Box box : figure) if(form[box.getY() + 1][box.getX()] > 0) return true;
            return false;
        }
        boolean crossToWell() { //есть ли пространство для новой фигуры
            for(Box box : figure) if(form[box.getY()][box.getX()] > 0) return true;
            return false;
        }
        void fixToWell() {  //изменение цвета фигуры при фиксации

            for(Box box : figure) form[box.getY()][box.getX()] = color;
        }
        boolean widthToWell(int col) {  //проверка на косание фигурой стены LEFT,RIGHT...
            for(Box box : figure) {
                if(col == LEFT && (box.getX() == 0 || form[box.getY()][box.getX() - 1] > 0)) return true;
                if(col == RIGHT && (box.getX() == FORM_WIDTH - 1 || form[box.getY()][box.getX() + 1] > 0)) return true;
            }
            return false;
        }
        void move(int col) {   //передвижение фигуры в право или лево
            if(!widthToWell(col)) {
                int dx = col - 38;   
                for(Box box : figure) box.setX(box.getX() + dx);
                x += dx;
            }
        }
        void crossDown() {   //передвижение фигуры вниз
            for(Box box : figure) box.setY(box.getY() + 1);
            y++;
        }
        void drop() { // падение в низ

            while(!touchToWell()) crossDown();
        }
        boolean collidesAt() { //(коллизии вращения фигуры)при проверке косания со стеной
            for(int x = 0; x < size; x++)
                for(int y = 0; y < size; y++)
                    if(well[y][x] == 1) {
                        if(y + this.y < 0) return true;
                        if(x + this.x < 0 || x + this.x > FORM_WIDTH - 1) return true;
                        if(form[y + this.y][x + this.x] > 0) return true;
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
        void rotate() {    //вращение фигуры(ротация)
            rotateWell(RIGHT);
            if(!collidesAt()) {
                figure.clear();
                createWell();
            } else {
                rotateWell(LEFT);
            }
        }
        void paint(Graphics g) {

            for(Box box : figure) box.paint(g, color);
        }
    }
    final class Box { 
        private int x, y;

        public Box(int x, int y) {
            setX(x);
            setY(y);
        }

        void setX(int x) { this.x = x; }
        void setY(int y) { this.y = y; }

        int getX() { return x; }
        int getY() { return y; }

        void paint(Graphics g, int color) {
            g.setColor(new Color(color));
            g.drawRoundRect(x* BOX_SIZE +1, y* BOX_SIZE +1, BOX_SIZE -2, BOX_SIZE -2, ARC, ARC);
        }
    }
    class Canvas extends JPanel {     
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for(int x = 0; x < FORM_WIDTH; x++)
                for(int y = 0; y < FORM_HEIGHT; y++) {
                    if(x < FORM_WIDTH - 1 && y < FORM_HEIGHT - 1) {
                        g.setColor(Color.lightGray);
                        g.drawLine((x+1)* BOX_SIZE -2, (y+1)* BOX_SIZE, (x+1)* BOX_SIZE +2, (y+1)* BOX_SIZE);
                        g.drawLine((x+1)* BOX_SIZE, (y+1)* BOX_SIZE -2, (x+1)* BOX_SIZE, (y+1)* BOX_SIZE +2);
                    }
                    if(form[y][x] > 0) {
                        g.setColor(new Color(form[y][x]));
                        g.fill3DRect(x* BOX_SIZE +1, y* BOX_SIZE +1, BOX_SIZE -1, BOX_SIZE -1, true);
                    }
                }
            if(overGame) {
                g.setColor(Color.white);
                for(int y = 0; y < MSG.length; y++)
                    for(int x = 0; x < MSG[y].length; x++)
                        if(MSG[y][x] == 1) g.fill3DRect(x*11+18, y*11+160, 10, 10, true);
            } else {
                figure.paint(g);
            }
        }
    }
}
