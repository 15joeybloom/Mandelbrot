package mandelbrot;


import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
Assignment #10
<p/>
Shows one of the infinitely many Julia sets.
<p/>
Uses the smooth coloring method described by Paxinum here:
<a>http://stackoverflow.com/questions/369438/smooth-spectrum-for-mandelbrot-set-rendering</a>
@author Joey Bloom
 */
public class JuliaPanelDouble extends JPanel
{
    private double left;
    private double top;
    private double right;
    private double bottom;
    private int itr = 25;

    private double cA;
    private double cB;

//    private MathContext mc = new MathContext(4, RoundingMode.HALF_EVEN);

    /**
     * Constructs a MandelbrotPanel to display the julia set with
     * the c value a + bi, between [-2,2] on the real axis and [-i,i] on
     * the imaginary axis.
     */
    public JuliaPanelDouble(double a, double b)
    {
        left = -2;
        right = 2;
        top = -1;
        bottom = 1;

        cA = a;
        cB = b;
    }

    /**
     * Sets the c value for this julia set
     * @param a real component of c
     * @param b imaginary component of c
     */
    public void setC(double a, double b)
    {
        cA = a;
        cB = b;
        repaint();
    }

    private double pixelsW;
    private double pixelsH;
    private double horizontalIncrement;
    private double verticalIncrement;

    /**
     * Paints a portion of the mandlebrot set on the panel.
     * @param g
     */
    @Override
    public void paintComponent(Graphics g)
    {
        pixelsW = getWidth();
        pixelsH = getHeight();
        horizontalIncrement = (right - left) / pixelsW;
        verticalIncrement = (bottom - top) / pixelsH;
        for(int x = 0; x < pixelsW; x++)
        {
            for(int y = 0; y < pixelsH; y++)
            {
                g.setColor(juliaColor(x,y));
                g.drawLine(x, y, x, y);
            }
        }
    }

    /**
     * Determines how to Color a pixel of the current view of the julia set.
     * @param x the x coordinate of a pixel on the screen
     * @param y the y coordinate of a pixel on the screen
     * @return the Color of a pixel on the screen
     */
    private Color juliaColor(int x, int y)
    {
        double zA = left + x * horizontalIncrement; //real component of z
        double zB = top + y * verticalIncrement;    //imaginary component of z
        double smoothColor = Math.exp(-Math.sqrt(zA * zA + zB * zB));
        for(int i = 0; i < itr; i++)
        {
            double zATemp = (zA * zA - zB * zB) + cA; //z = z^2 + c
            zB = (2 * zA * zB) + cB;
            zA = zATemp;
            smoothColor += Math.exp(-Math.sqrt(zA * zA + zB * zB));
            if(zA * zA + zB * zB > 4.0)
            {
//                return new Color(0x000080 + 0x080400 * i); //bands of color
                //the following two extra iterations reduce the
                //size of the "error term" (they make the fractal
                //smoother)
                zATemp = (zA * zA - zB * zB) + cA; //z = z^2 + c
                zB = (2 * zA * zB) + cB;
                zA = zATemp;
                smoothColor += Math.exp(-Math.sqrt(zA * zA + zB * zB));
                zATemp = (zA * zA - zB * zB) + cA; //z = z^2 + c
                zB = (2 * zA * zB) + cB;
                zA = zATemp;
                smoothColor += Math.exp(-Math.sqrt(zA * zA + zB * zB));
                return Color.getHSBColor(.6f + 10f * (float)smoothColor/itr,.6f,1);

            }
        }
        return Color.black;
    }

//    public static void main(String[] args)
//    {
//        JFrame frame = new JFrame("Mandlebrot");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(116,138);
//
//        frame.add(new MandelbrotPanelDouble());
//
//        frame.setVisible(true);
////        System.out.println(BigDecimal.valueOf(1).divide(BigDecimal.valueOf(3),64,RoundingMode.CEILING));
//    }
}
