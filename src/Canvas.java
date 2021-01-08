import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {
    private final Tetris out;

    Canvas(final Tetris out) {
        this.out = out;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int x = 0; x < out.FORM_WIDTH; x++) {
            for (int y = 0; y < out.FORM_HEIGHT; y++) {
                if (x < out.FORM_WIDTH - 1 && y < out.FORM_HEIGHT - 1) {
                    g.setColor(Color.pink);
                    g.drawLine((x + 1) * out.BOX_SIZE - 2, (y + 1) * out.BOX_SIZE, (x + 1) * out.BOX_SIZE + 2, (y + 1) * out.BOX_SIZE);
                    g.drawLine((x + 1) * out.BOX_SIZE, (y + 1) * out.BOX_SIZE - 2, (x + 1) * out.BOX_SIZE, (y + 1) * out.BOX_SIZE + 2);
                }
                if (out.getForm()[y][x] > 0) {
                    g.setColor(new Color(out.getForm()[y][x]));
                    g.fill3DRect(x * out.BOX_SIZE + 1, y * out.BOX_SIZE + 1, out.BOX_SIZE - 1, out.BOX_SIZE - 1, true);
                }
            }
        }
        if (out.isOverGame()) {
            g.setColor(Color.white);
            for (int y = 0; y < out.MSG.length; y++) {
                for (int x = 0; x < out.MSG[y].length; x++) {
                    if (out.MSG[y][x] == 1) {
                        g.fill3DRect(x * 11 + 18, y * 11 + 160, 10, 10, true);
                    }
                }
            }
        } else {
            out.getFigure().paint(g);
        }
    }

}

