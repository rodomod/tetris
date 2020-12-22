import java.awt.*;
import java.util.ArrayList;

final class Figure {
    private final ArrayList<Box> figure = new ArrayList<Box>();
    private final int[][] well = new int[4][4];
    private final int type;
    private final int size;
    private final int color;
    private int x = 3;
    private int y = 0;
    private final Tetris out;

    Figure(final Tetris out) {
        this.out = out;
        type = out.getRandom().nextInt(out.dot.length);
        size = out.dot[type][4][0];
        color = out.dot[type][4][1];
        if (size == 4) {
            y = -1;
        }
        for (int i = 0; i < size; i++) {
            System.arraycopy(out.dot[type][i], 0, well[i], 0, out.dot[type][i].length);
        }
        createWell();

    }


    void createWell() {
        //создать фигуру ...
        for (int width = 0; width < size; width++) {
            for (int height = 0; height < size; height++) //проходим по массиву well и создаём нашу фигуру
            {
                if (well[height][width] == 1) {
                    figure.add(new Box(width + this.x, height + this.y, out));
                }
            }
        }
    }

    boolean touchToWell() {
        //коснулась ли фигура дна
        for (Box box : figure) {
            if (out.getForm()[box.getY() + 1][box.getX()] > 0) {
                return true;
            }
        }
        return false;
    }

    boolean crossToWell() {
        //есть ли пространство для новой фигуры
        for (Box box : figure) {
            if (out.getForm()[box.getY()][box.getX()] > 0) {
                return true;
            }
        }
        return false;
    }

    void fixToWell() {
        //изменение цвета фигуры при фиксации
        for (Box box : figure) {
            out.getForm()[box.getY()][box.getX()] = color;
        }
    }

    boolean widthToWell(int col) {
        //проверка на косание фигурой стены LEFT,RIGHT...
        for (Box box : figure) {
            if (col == out.LEFT && (box.getX() == 0 || out.getForm()[box.getY()][box.getX() - 1] > 0)) {
                return true;
            }
            if (col == out.RIGHT && (box.getX() == out.FORM_WIDTH - 1 || out.getForm()[box.getY()][box.getX() + 1] > 0)) {
                return true;
            }
        }
        return false;
    }

    void move(int col) {
        //передвижение фигуры в право или лево
        if (!widthToWell(col)) {
            int dx = col - 38;
            for (Box box : figure) {
                box.setX(box.getX() + dx);
            }
            x += dx;
        }
    }

    void crossDown() {
        //передвижение фигуры вниз
        for (Box box : figure) {
            box.setY(box.getY() + 1);
        }
        y++;
    }

    void drop() {
        // падение в низ
        while (!touchToWell()) {
            crossDown();
        }
    }

    boolean collidesAt() {
        //(коллизии вращения фигуры)при проверке косания со стеной
        for (int width = 0; width < size; width++) {
            for (int height = 0; height < size; height++) {
                if (well[height][width] == 1) {
                    if (height + this.y < 0) {
                        return true;
                    }
                    if (width + this.x < 0 || width + this.x > out.FORM_WIDTH - 1) {
                        return true;
                    }
                    if (out.getForm()[height + this.y][width + this.x] > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void rotateWell(int col) {
        // ротация образа фигуры
        for (int i = 0; i < size / 2; i++) {
            for (int j = i; j < size - 1 - i; j++) {
                if (col == out.RIGHT) {
                    //по часовой стрелке
                    int tmp = well[size - 1 - j][i];
                    well[size - 1 - j][i] = well[size - 1 - i][size - 1 - j];
                    well[size - 1 - i][size - 1 - j] = well[j][size - 1 - i];
                    well[j][size - 1 - i] = well[i][j];
                    well[i][j] = tmp;
                } else {
                    //против часовой стрелки
                    int tmp = well[i][j];
                    well[i][j] = well[j][size - 1 - i];
                    well[j][size - 1 - i] = well[size - 1 - i][size - 1 - j];
                    well[size - 1 - i][size - 1 - j] = well[size - 1 - j][i];
                    well[size - 1 - j][i] = tmp;
                }
            }
        }
    }

    void rotate() {
        //вращение фигуры(ротация)
        rotateWell(out.RIGHT);
        if (!collidesAt()) {
            figure.clear();
            createWell();
        } else {
            rotateWell(out.LEFT);
        }
    }

    void paint(Graphics g) {
        for (Box box : figure) {
            box.paint(g, color);
        }
    }

}
