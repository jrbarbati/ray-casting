public class Point {
    float x;
    float y;

    public Point(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public float distanceTo(Point point)
    {
        return (float) Math.sqrt((point.x - this.x) * (point.x - this.x) + (point.y - this.y) * (point.y - this.y));
    }
}
