package mandelbrot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.event.MouseInputAdapter;

/**
 * @author Joey Bloom
 *
 */
public class DragBox
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        final Rectangle rect = new Rectangle();
        final JPanel panel = new JPanel(){

            @Override
            public void paintComponent(Graphics g)
            {
                g.clearRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.black);
                ((Graphics2D)g).draw(rect);
            }
        };
        panel.setLayout(new OverlayLayout(panel));
        MouseInputAdapter adapter = new MouseInputAdapter(){
            @Override
            public void mousePressed(MouseEvent e)
            {
                rect.setBounds(e.getX(),e.getY(),0,0);
                panel.repaint();
                System.out.println("Pressed");
            }
            @Override
            public void mouseDragged(MouseEvent e)
            {
                rect.setBounds(rect.x,rect.y,e.getX()-rect.x,e.getY()-rect.y);
                panel.repaint();
                System.out.println("Dragged");
            }
            @Override
            public void mouseReleased(MouseEvent e)
            {
                rect.setBounds(0,0,0,0);
                panel.repaint();
                System.out.println("Released");
            }
            @Override
            public void mouseClicked(MouseEvent e)
            {
                System.out.println("Clicked");
            }
        };
        panel.addMouseMotionListener(adapter);
        panel.addMouseListener(adapter);

        frame.add(panel);
        frame.setSize(300,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }
}