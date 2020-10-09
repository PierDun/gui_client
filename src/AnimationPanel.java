import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {

    private ArrayList<ChestButton> buttonsList;

    AnimationPanel(ArrayList<ChestButton> buttonsList) {
        this.buttonsList = buttonsList;
        setLayout(null);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.black);
        g2d.drawRect(0, 0, getWidth(), getHeight());

        for(ChestButton button: buttonsList){
            this.add(button);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

}
