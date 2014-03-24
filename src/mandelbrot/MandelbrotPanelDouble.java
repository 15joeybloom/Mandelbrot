package mandelbrot;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.*;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

/**
 Assignment #10
 Shows the Mandelbrot set using double-precision floating point
 types.
 <p/>
 Each pixel on the Mandelbrot set is colored according to the number
 of iterations of the function Z = Z^2 + C before Z escapes to infinity.
 Z and C are complex numbers. Z begins at 0, and C is the complex
 coordinates of the pixel being colored. If, after a fixed large number
 of iterations of the equation, the magnitude of Z is less than 2,
 then the pixel is colored black. Otherwise, the magnitude of Z has
 escaped the radius of 2 and will tend toward infinity, so the pixel
 is colored based on the number of iterations until Z escaped. I am
 using the smooth coloring method described here:
 <a>http://linas.org/art-gallery/escape/escape.html</a>
 <p/>
 @author Joey Bloom
 */
public class MandelbrotPanelDouble extends JPanel
{
    private GlassPane glassPane;
    private FractalPanel fractalPanel;
    private JPanel infoPanel;
    private JLabel cEquals;
    private JTextField cText;
    private Stack<double[]> views = new Stack<>();

    /**
     Constructs a MandelbrotPanel to display the mandlebrot
     set, between [-2.5,1] on the real axis and [-i,i] on
     the imaginary axis.
     */
    public MandelbrotPanelDouble()
    {
        setLayout(new BorderLayout());
        setUpFractalPanel();
        setUpInfoPanel();
    }

    private void setUpFractalPanel()
    {
        fractalPanel = new FractalPanel();
        fractalPanel.setLayout(new OverlayLayout(fractalPanel));
        fractalPanel.setView(-5, 2, -2, 2);

        glassPane = new GlassPane();
        glassPane.setOpaque(false);
        glassPane.setBounds(fractalPanel.getBounds());

        MouseInputAdapter adapter = new MouseInputAdapter()
        {
            private Point dragBoxTopLeft;

            @Override
            public void mousePressed(MouseEvent e)
            {
                dragBoxTopLeft = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e)
            {
                glassPane.getRect().setBounds(dragBoxTopLeft.x, dragBoxTopLeft.y, e.getX()
                    - dragBoxTopLeft.x, e.getY() - dragBoxTopLeft.y);
                glassPane.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                glassPane.getRect().setBounds(0, 0, 0, 0);
                Point dragBoxBottomRight = e.getPoint();

                //if a single "click" or if rectangle
                //area <= 4
                if(dragBoxBottomRight.equals(dragBoxTopLeft)
                    || (dragBoxBottomRight.x - dragBoxTopLeft.x)
                    * (dragBoxBottomRight.y * dragBoxTopLeft.y) <= 4)
                {
                    return;
                }
                double[] topLeftCorner = fractalPanel.pixelToCoord(dragBoxTopLeft.x, dragBoxTopLeft.y);
                double[] bottomRightCorner = fractalPanel.pixelToCoord(e.getX(), e.getY());
                views.push(fractalPanel.getView());
                fractalPanel.setView(
                    topLeftCorner[0], bottomRightCorner[0],
                    topLeftCorner[1], bottomRightCorner[1]);
                glassPane.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e)
            {
                //if alt is pressed, don't change cText
                if(e.isAltDown())
                {
                    return;
                }
                double[] cpx = fractalPanel.pixelToCoord(e.getX(), e.getY());
                cText.setText(new BigComplex(BigDecimal.valueOf(Double.valueOf(cpx[0])), BigDecimal.valueOf(Double.valueOf(cpx[1]))).toString());
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                if(e.isShiftDown())
                {
                    if(e.isControlDown())//if ctrl-shift-leftclick
                    {
                        //zoom all the way out
                        views.clear();
                        fractalPanel.setView(-5.0, 2.0, -2.0, 2.0);
//                        dragBoxTopLeft.setLocation(0, 0);
                    }
                    else //if shift-leftclick
                    {
                        //zoom out to previous view
                        if(views.isEmpty())
                        {
                            return;
                        }
                        fractalPanel.setView(views.pop());
                    }
                }
                else //if leftclick only
                {
                    //zoom 2x on point of click
                    double[] oldView = fractalPanel.getView();
                    double[] targetCoords = fractalPanel.pixelToCoord(e.getX(), e.getY());
                    double newLeft = (oldView[0] + targetCoords[0]) / 2.0;
                    double newRight = (oldView[1] + targetCoords[0]) / 2.0;
                    double newTop = (oldView[2] + targetCoords[1]) / 2.0;
                    double newBottom = (oldView[3] + targetCoords[1]) / 2.0;
                    views.push(fractalPanel.getView());
                    fractalPanel.setView(newLeft, newRight, newTop, newBottom);
                }
                fractalPanel.repaint();
            }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);

        fractalPanel.add(glassPane);
        add(fractalPanel, BorderLayout.CENTER);
    }

    private void setUpInfoPanel()
    {
        infoPanel = new JPanel();

        cEquals = new JLabel("C = ");
        cText = new JTextField(27);
        cText.setEditable(false);

        infoPanel.add(cEquals);
        infoPanel.add(cText);

        add(infoPanel, BorderLayout.SOUTH);
    }

    /**
     Returns the FractalPanel displayed on this MandelbrotPanelDouble
     <p/>
     @return the FractalPanel
     */
    public FractalPanel getFractalPanel()
    {
        return fractalPanel;
    }

    /**
     Returns the GlassPane displayed on this MandelbrotPanelDouble
     <p/>
     @return the GlassPane
     */
    public GlassPane getGlassPane()
    {
        return glassPane;
    }

    /**
     Translates the coordinates of a pixel on the screen to an imaginary
     number based on the current view of the mandlebrot set
     <p/>
     @param x
     @param y
     @return a double[] of length 2 of the form {real, imaginary}
     */
    public double[] pixelToCoord(int x, int y)
    {
        return fractalPanel.pixelToCoord(x, y);
    }

    /**
     Within the MandelbrotPanelDouble, this is the panel
     on which the fractal appears. The GlassPane goes on
     top of this.
     */
    protected class FractalPanel extends JPanel
    {
        private double left;
        private double top;
        private double right;
        private double bottom;
        private int itr = 200;
        private boolean needsRepaint = true;
        //for efficiency, store the generated fractal in this variable
        //as an image so you don't have to regenerate everytime you
        //drag the zoom box.
        private BufferedImage storedImage;
        //these are only instance variables for efficiency purposes.
        //they are dependant upon the preceding variables.
        private double pixelsW;
        private double pixelsH;
        private double horizontalIncrement;
        private double verticalIncrement;

        /**
         Constructs a FractalPanel.
         */
        public FractalPanel()
        {
            super();
            addComponentListener(new ComponentListener()
            {
                @Override
                public void componentResized(ComponentEvent e)
                {
                    needsRepaint = true;
                    repaint();
//                    System.out.println("resized");
                }

                @Override
                public void componentMoved(ComponentEvent e)
                {
                }

                @Override
                public void componentShown(ComponentEvent e)
                {
                }

                @Override
                public void componentHidden(ComponentEvent e)
                {
                }
            });
        }

        /**
         Paints a portion of the Mandelbrot set on the panel.
         <p/>
         @param g
         */
        @Override
        public void paintComponent(Graphics g)
        {
            if(needsRepaint)
            {
                storedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                Graphics imageG = storedImage.getGraphics();
                pixelsW = getWidth();
                pixelsH = getHeight();
                horizontalIncrement = (right - left) / pixelsW;
                verticalIncrement = (bottom - top) / pixelsH;
                for(int x = 0; x < pixelsW; x++)
                {
                    for(int y = 0; y < pixelsH; y++)
                    {
                        imageG.setColor(mandlebrotColor(x, y));
                        imageG.drawRect(x, y, 0, 0);
                    }
                    needsRepaint = false;
                }
            }
            g.drawImage(storedImage, 0, 0, null);

        }

        /**
         Determines how to Color a pixel of the current view of the Mandelbrot
         set.
         <p/>
         @param x the x coordinate of a pixel on the screen
         @param y the y coordinate of a pixel on the screen
         @return the Color of a pixel on the screen
         */
        private Color mandlebrotColor(int x, int y)
        {
            final double cA = left + x * horizontalIncrement; //real component of c
            final double cB = top + y * verticalIncrement;    //imaginary component of c

            double zA = cA; //real component of z
            double zB = cB; //imaginary component of z
            for(int i = 0; i < itr; i++)
            {
                double zATemp = (zA * zA - zB * zB) + cA; //z = z^2 + c
                zB = (2 * zA * zB) + cB;
                zA = zATemp;
                if(zA * zA + zB * zB > 4.0)//if escaped
                {
//                    return new Color(4004*i); //bands of color

                    //smooth coloring
                    //these two more iterations reduce the size
                    //of the error term
                    zATemp = (zA * zA - zB * zB) + cA; //z = z^2 + c
                    zB = (2 * zA * zB) + cB;
                    zA = zATemp;
                    zATemp = (zA * zA - zB * zB) + cA; //z = z^2 + c
                    zB = (2 * zA * zB) + cB;
                    zA = zATemp;

                    double magnitude = Math.sqrt(zA * zA + zB * zB);
                    float fraction = (float) (i
                        - (Math.log10(Math.log10(magnitude))) / Math.log10(2.0))
                        / itr;

                    return Color.getHSBColor(.6f + 10f * fraction, .6f, 1);
                }
            }
            return Color.black; //if not escaped
        }

        /**
         Changes the coordinates of a pixel on the screen to an imaginary
         number based on the current view of the Mandelbrot set.
         <p/>
         @param x x coordinate of the pixel
         @param y y coordinate of the pixel
         @return a double[] of length 2 of the form {real, imaginary}
         */
        public double[] pixelToCoord(double x, double y)
        {
            return new double[]
                {
                    left + x * horizontalIncrement,
                    top + y * verticalIncrement,
                };
        }

        /**
         Sets the view of the Mandelbrot set.
         <p/>
         @param left   the lowest number on the real axis
         @param right  the highest number on the real axis <br/>
                       precondition: <code>left &lt right </code>
         @param top    the lowest number on the imaginary axis
         @param bottom the highest number on the imaginary axis <br/>
                       precondition: <code> top &gt bottom </code>
         */
        public void setView(double left, double right, double top, double bottom)
        {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
            needsRepaint = true;
        }

        /**
         Sets the view of the Mandelbrot set based on an array that has the
         bounds of the view.
         <p/>
         @param view a double[] of length 4 of the form <br/>
                     <code>{left,right,top,bottom}</code>
         @see MandelbrotPanelDouble.setView(double,double,double,double)
         */
        public void setView(double[] view)
        {
            setView(view[0], view[1], view[2], view[3]);
        }

        /**
         Gets the view of the Mandelbrot set
         <p/>
         @return a double[] of length 4 of the form <br/>
                 {left,right,top,bottom}
         */
        public double[] getView()
        {
            return new double[]
                {
                    left, right, top, bottom
                };
        }
    }

    private class GlassPane extends JPanel
    {
        private Rectangle rect;

        public GlassPane()
        {
            super();
            rect = new Rectangle();
        }

        @Override
        public void paintComponent(Graphics g)
        {
            g.setColor(Color.white);
            g.setXORMode(Color.black);
            ((Graphics2D) g).draw(rect);
        }

        public Rectangle getRect()
        {
            return rect;
        }
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Mandlebrot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(816, 638);

        frame.add(new MandelbrotPanelDouble());

        frame.setVisible(true);
//        System.out.println(BigDecimal.valueOf(1).divide(BigDecimal.valueOf(3),64,RoundingMode.CEILING));
    }
}
