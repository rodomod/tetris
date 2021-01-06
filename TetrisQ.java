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

public class TetrisQ extends JFrame {
	final String Title = "tetris";
	final int BOX_SIZE = 25;// размер одного блока
	final int ARC = 6;// радиус закругления блока
	final int FORM_WIDTH = 10;// ширина игрового поля[width]
	final int FORM_HEIGHT = 21;// высота игрового поля[height]
	final int DX = 7;// new X
	final int DY = 26;// new Y
	final int RUN = 180;// стартовая локация
	final int LEFT = 37;// коды клавиш...
	final int UP = 38;
	final int RIGHT = 39;
	final int DOWN = 40;
	final int SHOW_DEL = 400;// задержка анимации
	final int[][][] dot = {
    		{ { 0, 0, 0, 0 }, { 1, 1, 1, 1 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 4, 0x00f0f0 } }, // I
			{ { 0, 0, 0, 0 }, { 0, 1, 1, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 4, 0xf0f000 } }, // O
			{ { 1, 0, 0, 0 }, { 1, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 3, 0x0000f0 } }, // J
			{ { 0, 0, 1, 0 }, { 1, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 3, 0xf0a000 } }, // L
			{ { 0, 1, 1, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 3, 0x00f000 } }, // S
			{ { 1, 1, 1, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 3, 0xa000f0 } }, // T
			{ { 1, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 3, 0xf00000 } }  // Z
	};
	final int[] score = { 100, 300, 700, 1550 };// очки за собранный слой
	int gameScore = 0;
	int[][] form = new int[FORM_HEIGHT + 1][FORM_WIDTH];// игровое поле form
	JFrame frame;
	Canvas canvas = new Canvas();
	Random random = new Random();
	Figure figure = new Figure();
	boolean overGame = false;
	final int[][] MSG = {
            {1,1,1,1,0,1,0,0,0,1,0,1,1,1,0,1,1,1,1,1},
	        {1,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,0,1,0,0},
	        {1,1,1,1,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0},
	        {1,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,0,1,0,0},
	        {1,1,1,1,0,1,0,0,0,1,0,1,1,1,0,0,0,1,0,0}
			};                                   

	public static void main(String[] args) {
		new TetrisQ().init();
	}

	TetrisQ() {
		setTitle(Title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(RUN, RUN, FORM_WIDTH * BOX_SIZE + DX, FORM_HEIGHT * BOX_SIZE + DY);
		setResizable(false);// не изменять размер игрового поля
		setLocationRelativeTo(null);// игра по центру экрана
		canvas.setBackground(Color.black);// цвет фона
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (!overGame) {
					if (e.getKeyCode() == LEFT)
						figure.move(e.getKeyCode());
					if (e.getKeyCode() == RIGHT)
						figure.move(e.getKeyCode());
					if (e.getKeyCode() == UP)
						figure.rotate();
					if (e.getKeyCode() == DOWN)
						figure.drop();
				}
				canvas.repaint();
			}
		});
		add(BorderLayout.CENTER, canvas);
		setVisible(true);
		Arrays.fill(form[FORM_HEIGHT], 1);// инициализация дна...создаём площадку дна
	}

	void init() {
		while (!overGame) {// пока не overGame крутится цикл while
			try {
				Thread.sleep(SHOW_DEL); // задержка анимации...перед перерисовкой
			} catch (InterruptedException e) {
			}
			canvas.repaint();// перерисовка
			curentString();// проверка заполнения строк
			if (figure.touchToWell()) {// коснулась ли фигура дна,или упавших фигур
				figure.fixToWell();// фиксируем(изменяем цвет фигуры)после касания
				figure = new Figure();// создаем новую фигуру
				overGame = figure.crossToWell();// есть ли пространство для новой фигуры,не закончилась ли игра
			} else {
				figure.crossDown();// фигура продолжает падать
			}
		}
	}

	void curentString() {// проверка заполнения строк
		int height = FORM_HEIGHT - 1;
		int curentHeight = 0;
		while (height > 0) {
			int curent = 1;
			for (int width = 0; width < FORM_WIDTH; width++)
				curent *= Integer.signum(form[height][width]);
			if (curent > 0) {
				curentHeight++;
				for (int h = height; h > 0; h--)
					System.arraycopy(form[h - 1], 0, form[h], 0, FORM_WIDTH);
			} else {
				height--;
			}
		}
		if (curentHeight > 0) {
			gameScore += score[curentHeight - 1];
			setTitle(Title + " : " + gameScore);
		}
	}

	final class Figure {
		private final int[][] well = new int[4][4];
		private final ArrayList<Box> figure = new ArrayList<Box>();
		private final int type, size, color;
		private int y = 0, x = 3;// начало в левом верхнем углу

		Figure() {
			type = random.nextInt(dot.length);
			size = dot[type][4][0];
			color = dot[type][4][1];
			if (size == 4)
				y = -1;
			for (int quar = 0; quar < size; quar++)
				System.arraycopy(dot[type][quar], 0, well[quar], 0, dot[type][quar].length);
			createWell();
		}

		void createWell() {// создать фигуру
			for (int w = 0; w < size; w++)
				for (int h = 0; h < size; h++)
					if (well[h][w] == 1)
						figure.add(new Box(w + this.x, h + this.y));
		}

		boolean touchToWell() {// коснулась ли фигура дна
			for (Box box : figure)
				if (form[box.getY() + 1][box.getX()] > 0)
					return true;
			return false;
		}

		boolean crossToWell() {// есть ли пространство для новой фигуры
			for (Box box : figure)
				if (form[box.getY()][box.getX()] > 0)
					return true;
			return false;
		}

		void fixToWell() {// изменение цвета фигуры при фиксации
			for (Box box : figure)
				form[box.getY()][box.getX()] = color;
		}

		boolean widthToWell(int col) {// проверка на косание фигурой стены LEFT,RIGHT...
			for (Box box : figure) {
				if (col == LEFT && (box.getX() == 0 || form[box.getY()][box.getX() - 1] > 0))
					return true;
				if (col == RIGHT && (box.getX() == FORM_WIDTH - 1 || form[box.getY()][box.getX() + 1] > 0))
					return true;
			}
			return false;
		}

		void move(int col) {// передвижение фигуры в право или лево
			if (!widthToWell(col)) {
				int dx = col - 38;
				for (Box box : figure)
					box.setX(box.getX() + dx);
				x += dx;
			}
		}

		void crossDown() {// передвижение фигуры вниз
			for (Box box : figure)
				box.setY(box.getY() + 1);
			y++;
		}

		void drop() {// падение фигуры вниз
			while (!touchToWell()) {
				crossDown();
			}
		}

		boolean collidesAt() {// (коллизии вращения фигуры)проверка косания со стеной
			for (int w = 0; w < size; w++)
				for (int h = 0; h < size; h++)
					if (well[h][w] == 1) {
						if (h + this.y < 0)
							return true;
						if (w + this.x < 0 || w + this.x > FORM_WIDTH - 1)
							return true;
						if (form[h + this.y][w + this.x] > 0)
							return true;
					}
			return false;
		}

		void rotateWell(int col) {// ротация образа фигуры
			for (int w = 0; w < size / 2; w++)
				for (int h = w; h < size - 1 - w; h++)
					if (col == RIGHT) {// по часовой стрелке
						int tmp = well[size - 1 - h][w];
						well[size - 1 - h][w] = well[size - 1 - w][size - 1 - h];
						well[size - 1 - w][size - 1 - h] = well[h][size - 1 - w];
						well[h][size - 1 - w] = well[w][h];
						well[w][h] = tmp;
					} else {// против часовой стрелки
						int tmp = well[w][h];
						well[w][h] = well[h][size - 1 - w];
						well[h][size - 1 - w] = well[size - 1 - w][size - 1 - h];
						well[size - 1 - w][size - 1 - h] = well[size - 1 - h][w];
						well[size - 1 - h][w] = tmp;
					}
		}

		void rotate() {// вращение фигуры(ротация)
			rotateWell(RIGHT);
			if (!collidesAt()) {
				figure.clear();
				createWell();
			} else {
				rotateWell(LEFT);
			}
		}

		void paint(Graphics g) {
			for (Box box : figure)
				box.paint(g, color);
		}

	}

	final class Box {
		private int x;
		private int y;

		public Box(int x, int y) {
			super();
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
			g.drawRoundRect(x * BOX_SIZE + 1, y * BOX_SIZE + 1, BOX_SIZE - 2, BOX_SIZE - 2, ARC, ARC);

		}
	}

	class Canvas extends JPanel {
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			for (int w = 0; w < FORM_WIDTH; w++)
				for (int h = 0; h < FORM_HEIGHT; h++) {
					if (w < FORM_WIDTH - 1 && h < FORM_HEIGHT - 1) {
						g.setColor(Color.lightGray);
						g.drawLine((w + 1) * BOX_SIZE - 2, (h + 1) * BOX_SIZE, (w + 1) * BOX_SIZE + 2,
								(h + 1) * BOX_SIZE);
						g.drawLine((w + 1) * BOX_SIZE, (h + 1) * BOX_SIZE - 2, (w + 1) * BOX_SIZE,
								(h + 1) * BOX_SIZE + 2);
					}
					if (form[h][w] > 0) {
						g.setColor(new Color(form[h][w]));
						g.fill3DRect(w * BOX_SIZE + 1, h * BOX_SIZE + 1, BOX_SIZE - 1, BOX_SIZE - 1, true);
					}
				}
			if (overGame) {
				g.setColor(Color.white);
				for (int h = 0; h < MSG.length; h++)
					for (int w = 0; w < MSG[h].length; w++)
						if (MSG[h][w] == 1)
							g.fill3DRect(w * 11 + 18, h * 11 + 160, 10, 10, true);
			} else {
				figure.paint(g);
			}
		}
	}
}
