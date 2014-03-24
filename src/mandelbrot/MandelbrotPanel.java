package mandelbrot;


import mandelbrot.BigComplex;
import java.awt.Color;
import java.awt.Graphics;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 *
 * @author Joey Bloom
 */
public class MandelbrotPanel extends JPanel
{
    private BigDecimal left;
    private BigDecimal top;
    private BigDecimal right;
    private BigDecimal bottom;

    private MathContext mc = new MathContext(4, RoundingMode.HALF_EVEN);

    /**
     * Constructs a MandelbrotPanel to display the mandlebrot
     * set, between [-2.5,1] on the real axis and [-i,i] on
     * the imaginary axis.
     */
    public MandelbrotPanel()
    {
        left = BigDecimal.valueOf(-2.5);
        right = BigDecimal.valueOf(1);
        top = BigDecimal.valueOf(-1);
        bottom = BigDecimal.valueOf(1);
    }

    private BigDecimal pixelsW;
    private BigDecimal pixelsH;
    private BigDecimal horizontalIncrement;
    private BigDecimal verticalIncrement;

    /**
     * Paints a portion of the mandlebrot set on the panel.
     * @param g
     */
    @Override
    public void paintComponent(Graphics g)
    {
        pixelsW = BigDecimal.valueOf(getWidth());
        pixelsH = BigDecimal.valueOf(getHeight());
        horizontalIncrement = right.subtract(left).divide(pixelsW,mc);
        verticalIncrement = bottom.subtract(top).divide(pixelsH,mc);
        for(int x = 0; x < getWidth(); x++)
        {
            for(int y = 0; y < getHeight(); y++)
            {
                g.setColor(mandlebrotColor(x,y));
                g.drawLine(x, y, x, y);
            }
        }
    }

    /**
     * Determines how to Color a pixel of the current view of the mandlebrot set.
     * @param x the x coordinate of a pixel on the screen
     * @param y the y coordinate of a pixel on the screen
     * @return the Color of a pixel on the screen
     */
    private Color mandlebrotColor(int x, int y)
    {
        BigDecimal a = left.add(BigDecimal.valueOf(x).multiply(horizontalIncrement,mc));
        BigDecimal b = top.add(BigDecimal.valueOf(y).multiply(verticalIncrement,mc));
        BigComplex c = new BigComplex(a,b);

        BigComplex z = c;
        for(int i = 0; i < 32; i++)
        {
            z = z.multiply(z,mc).add(c,mc); //z = z^2 + c
            if(z.magnitudeSquared() > 4.0)
            {
                return Color.white;
            }
        }
        return Color.black;
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Mandelbrot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(116,138);

        frame.add(new MandelbrotPanel());

        frame.setVisible(true);
//        System.out.println(BigDecimal.valueOf(1).divide(BigDecimal.valueOf(3),64,RoundingMode.CEILING));
    }
}
