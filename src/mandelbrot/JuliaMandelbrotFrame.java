package mandelbrot;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
Shows the Mandelbrot set side by side with one of the infinitely many Julia
sets. Each point on the Julia set is generated using the same iterated
function, Z = Z^2 + C, as the Mandelbrot set. For the Mandelbrot set,
starting Z value of the point on the Mandelbrot set that the user mouses over.
@author Joey Bloom
 */
public class JuliaMandelbrotFrame extends JFrame
{
    private MandelbrotPanelDouble mand;
    private JuliaPanelDouble julia;

    private JMenuBar menubar;
    private JMenu view;
    private JPanel setViewPanel;
        private JButton setView;
        private JLabel leftLabel;
        private JTextField left;
        private JLabel rightLabel;
        private JTextField right;
        private JLabel topLabel;
        private JTextField top;
        private JLabel bottomLabel;
        private JTextField bottom;
        private JButton importView;
        private JButton exportView;
    private JMenuItem zoomControls;
    /**
     * Constructs a JuliaMandelbrotFrame
     */
    public JuliaMandelbrotFrame()
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(1,2));
        setSize(700,300);

        mand = new MandelbrotPanelDouble();
        mand.addMouseMotionListener(new MouseMotionListener(){

            @Override
            public void mouseDragged(MouseEvent e)
            {

            }

            @Override
            public void mouseMoved(MouseEvent e)
            {
                double[] cpx = mand.pixelToCoord(e.getX(), e.getY());
                julia.setC(cpx[0], cpx[1]);
            }
        });

        julia = new JuliaPanelDouble(0,0);

        add(mand);
        add(julia);

        setUpMenuBar();

        setVisible(true);

        zoomControls.doClick();
    }

    private void setUpMenuBar()
    {
        menubar = new JMenuBar();
        setJMenuBar(menubar);

        view = new JMenu("View");
        //update the text fields to display the current view when
        //the "View" menu is opened.
        view.addMenuListener(new MenuListener(){
            @Override
            public void menuSelected(MenuEvent e)
            {
                double[] currentView = mand.getFractalPanel().getView();
                left.setText(currentView[0]+"");
                right.setText(currentView[1]+"");
                top.setText(currentView[2]+"");
                bottom.setText(currentView[3]+"");
            }
            @Override
            public void menuDeselected(MenuEvent e){}
            @Override
            public void menuCanceled(MenuEvent e){}
        });
        menubar.add(view);

        setViewPanel = new JPanel();
        setViewPanel.setLayout(new GridLayout(6,1));
        view.add(setViewPanel);

        setView = new JButton("Set View Window");
        setView.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    mand.getFractalPanel().setView(
                        Double.parseDouble(left.getText()),
                        Double.parseDouble(right.getText()),
                        Double.parseDouble(top.getText()),
                        Double.parseDouble(bottom.getText())
                    );
//                    view.getPopupMenu().setVisible(false);
                    mand.repaint();
                }
                catch(NumberFormatException ex)
                {
                    JOptionPane.showMessageDialog(JuliaMandelbrotFrame.this, ex);
                }
            }
        });
        setViewPanel.add(setView);

        JPanel leftPanel = new JPanel();
            leftLabel = new JLabel("Left: ");
            leftPanel.add(leftLabel);
            left = new JTextField(20);
            leftPanel.add(left);
        setViewPanel.add(leftPanel);

        JPanel rightPanel = new JPanel();
            rightLabel = new JLabel("Right: ");
            rightPanel.add(rightLabel);
            right = new JTextField(20);
            rightPanel.add(right);
        setViewPanel.add(rightPanel);

        JPanel topPanel = new JPanel();
            topLabel = new JLabel("Top: ");
            topPanel.add(topLabel);
            top = new JTextField(20);
            topPanel.add(top);
        setViewPanel.add(topPanel);

        JPanel bottomPanel = new JPanel();
            bottomLabel = new JLabel("Bottom: ");
            bottomPanel.add(bottomLabel);
            bottom = new JTextField(20);
            bottomPanel.add(bottom);
        setViewPanel.add(bottomPanel);

        JPanel importExportPanel = new JPanel();
            importView = new JButton("Import...");
            importView.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    String name = JOptionPane.showInputDialog(
                        JuliaMandelbrotFrame.this,
                        "What is the name of the view you would like to import?",
                        "Import",
                        JOptionPane.INFORMATION_MESSAGE);
                    if(!name.endsWith(".mand"))//if user doesn't add extension, i.e. "file" instead of "file.mand"
                    {
                        name = name + ".mand";
                    }
                    try(BufferedReader in = new BufferedReader(new FileReader(new File("views\\"+name))))
                    {
                        left.setText(in.readLine());
                        right.setText(in.readLine());
                        top.setText(in.readLine());
                        bottom.setText(in.readLine());
                        setView.doClick();
                    }
                    catch(IOException ex)
                    {
                        JOptionPane.showMessageDialog(JuliaMandelbrotFrame.this, ex);
                    }
                }
            });
            importExportPanel.add(importView);
            exportView = new JButton("Export...");
            exportView.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    String name = JOptionPane.showInputDialog(
                        JuliaMandelbrotFrame.this,
                        "What is the name under which you would like to save the view?",
                        "Export",
                        JOptionPane.INFORMATION_MESSAGE);
                    if(!name.endsWith(".mand"))//if user doesn't add extension, i.e. "file" instead of "file.mand"
                    {
                        name = name + ".mand";
                    }
//                    try
//                    {
//                        File viewsFolder = new File("views");
//                        if(!viewsFolder.isDirectory())
//                        {
//                            System.out.println("the directory \"views\" does not exist...");
//                            File temp = new File("views/temp");
//                            temp.createNewFile();
//                            temp.delete();
//                        }
                        new File("views").mkdir();
//                    }
//                    catch(IOException ex)
//                    {
//
//                    }
                    try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File("views/"+name)))))
                    {
                        double[] currentView = mand.getFractalPanel().getView();
                        out.println(currentView[0]);
                        out.println(currentView[1]);
                        out.println(currentView[2]);
                        out.println(currentView[3]);
                    }
                    catch(IOException ex)
                    {
                        JOptionPane.showMessageDialog(JuliaMandelbrotFrame.this, ex);
                    }
                }
            });
            importExportPanel.add(exportView);
        setViewPanel.add(importExportPanel);

        view.add(new JSeparator());
        zoomControls = new JMenuItem("Zoom Controls...");
        zoomControls.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(JuliaMandelbrotFrame.this,
                    "Zoom Controls for the left side: " +
                    "\nZoom in: click a point or drag a box." +
                    "\nZoom out to last view: shift-click anywhere." +
                    "\nZoom out all the way: ctrl-shift-click anywhere" +
                    "\n\n Hold alt to move the cursor without changing the" +
                    "\n right side or the C= box.");
            }
        });
        view.add(zoomControls);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run()
            {
                new JuliaMandelbrotFrame();
            }
        });
    }
}
