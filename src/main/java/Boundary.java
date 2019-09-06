public class Boundary
{
    Point start;
    Point end;

    public Boundary(int x1, int y1, int x2, int y2)
    {
        this(new Point(x1, y1), new Point(x2, y2));
    }

    public Boundary(Point start, Point end)
    {
        this.start = start;
        this.end = end;
    }
}
