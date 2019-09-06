import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.*;
import java.util.List;

public class Main implements Runnable, MouseMotionListener, KeyListener, ChangeListener
{
    private static final int WIDTH = 750;
    private static final int HEIGHT = 750;
    private static final int NUM_LINES = 8;

    private JFrame mainFrame = new JFrame("Java Ray Casting");
    private Canvas leftCanvas;
    private Canvas rightCanvas;
    private JSlider fovSlider = new JSlider(0, 360);
    private List<Boundary> boundaries;
    private Random random = new Random();

    private Particle particle;
    private Map<Integer, Boolean> keys = new HashMap<>();

    private Main(int numberOfRays)
    {
        fovSlider.setValue(75);
        fovSlider.addChangeListener(this);

        particle = new Particle(new Point(0, 0), new FieldOfView(fovSlider.getValue()), numberOfRays);
        boundaries = buildBoundaries();

        leftCanvas = new Canvas();
        leftCanvas.setSize(WIDTH, HEIGHT);
        leftCanvas.addMouseMotionListener(this);

        rightCanvas = new Canvas();
        rightCanvas.setSize(WIDTH, HEIGHT);
        rightCanvas.addMouseMotionListener(this);

        mainFrame.add(leftCanvas, BorderLayout.WEST);
        mainFrame.add(rightCanvas, BorderLayout.EAST);
        mainFrame.add(fovSlider, BorderLayout.SOUTH);

        mainFrame.setFocusable(true);
        mainFrame.addKeyListener(this);

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(WIDTH * 2, HEIGHT);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        new Thread(this).start();
    }

    private List<Boundary> buildBoundaries()
    {
        List<Boundary> lines = new ArrayList<>();

        for (int i = 0; i < NUM_LINES; i++)
        {
            lines.add(new Boundary(
                            random.nextInt(WIDTH),
                            random.nextInt(HEIGHT),
                            random.nextInt(WIDTH),
                            random.nextInt(HEIGHT)
                    )
            );
        }

        lines.add(new Boundary(0, 0, WIDTH, 0));
        lines.add(new Boundary(0, 0, 0, HEIGHT));
        lines.add(new Boundary(0, HEIGHT, WIDTH, HEIGHT));
        lines.add(new Boundary(WIDTH, HEIGHT, WIDTH, 0));

        return lines;
    }

    @Override
    public void run()
    {
        while (true) render();
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        particle.location.x = e.getX();
        particle.location.y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        mouseDragged(e);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        keyPressed(e);
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyChar() == 'a')
            particle.fov.rotate(-1);
        if (e.getKeyChar() == 'd')
            particle.fov.rotate(1);

        particle.updateRays();
    }

    @Override
    public void keyReleased(KeyEvent e)
    {}

    @Override
    public void stateChanged(ChangeEvent e)
    {
        if (fovSlider != e.getSource())
            return;

        particle.updateFov(((JSlider) e.getSource()).getValue());
        mainFrame.setFocusable(true);
    }

    private void render()
    {
        BufferStrategy leftBufferStrategy = leftCanvas.getBufferStrategy();
        BufferStrategy rightBufferStrategy = rightCanvas.getBufferStrategy();

        if (leftBufferStrategy == null || rightBufferStrategy == null)
        {
            leftCanvas.createBufferStrategy(2);
            rightCanvas.createBufferStrategy(2);
            return;
        }

        Graphics leftSideGraphics = leftBufferStrategy.getDrawGraphics();
        Graphics rightSideGraphics = rightBufferStrategy.getDrawGraphics();

        leftSideGraphics.setColor(Color.BLACK);
        leftSideGraphics.fillRect(0, 0, leftCanvas.getWidth(), leftCanvas.getHeight());

        rightSideGraphics.setColor(Color.BLACK);
        rightSideGraphics.fillRect(0, 0, mainFrame.getWidth(), mainFrame.getHeight());

        leftSideGraphics.setColor(Color.GREEN);
        drawBoundaries(leftSideGraphics, boundaries);

        List<Ray> rays = particle.castRays(boundaries);
        leftSideGraphics.setColor(Color.WHITE);
        drawRays(leftSideGraphics, rays);

        drawVisualField(rightSideGraphics, rays);

        rightSideGraphics.dispose();
        leftBufferStrategy.show();

        leftSideGraphics.dispose();
        rightBufferStrategy.show();
    }

    private void drawBoundaries(Graphics g, List<Boundary> boundaries)
    {
        for (Boundary boundary : boundaries)
            g.drawLine((int) boundary.start.x, (int) boundary.start.y, (int) boundary.end.x, (int) boundary.end.y);
    }

    private void drawRays(Graphics g, List<Ray> rays)
    {
        for (Ray ray : rays)
        {
            if (ray.collision == null)
                continue;

            g.drawLine((int) ray.origin.x, (int) ray.origin.y, (int) ray.collision.x, (int) ray.collision.y);
        }
    }

    private void drawVisualField(Graphics g, List<Ray> rays)
    {
        for (int i = 0; i < rays.size(); i++)
        {
            Ray ray = rays.get(i);

            if (ray.collision == null)
                continue;

            int sliceWidth = Math.round((float) WIDTH / (float) rays.size());
            int sliceHeight = calculateHeight(ray);
            int sliceBrightness = calculateBrightness(ray);

            g.setColor(new Color(sliceBrightness, sliceBrightness, sliceBrightness, sliceBrightness));
            g.drawRect (i * sliceWidth, (HEIGHT / 2) - (sliceHeight / 2), sliceWidth, sliceHeight);
            g.fillRect (i * sliceWidth, (HEIGHT / 2) - (sliceHeight / 2), sliceWidth, sliceHeight);
        }
    }

    private int calculateBrightness(Ray ray)
    {
        if (ray.collision == null)
            return 0;

        return normalize(ray.origin.distanceTo(ray.collision), 255);
    }

    private int calculateHeight(Ray ray)
    {
        if (ray.collision == null)
            return 0;

        return normalize(ray.origin.distanceTo(ray.collision), HEIGHT);
    }

    private int normalize(float distance, int max)
    {
        if (distance > WIDTH)
            return 0;

        float a = distance / WIDTH;
        return max - ((int) (a * max));
    }

    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.out.println("Usage: java -jar ");
            System.exit(1);
        }

        try
        {
            new Main(Integer.parseInt(args[0]));
        }
        catch (NumberFormatException e)
        {
            System.out.println(e.getMessage());
        }
    }
}