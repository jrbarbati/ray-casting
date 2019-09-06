public class Ray
{
    Point origin;
    Point direction;
    Point collision;

    public Ray(int x, int y, float degrees)
    {
        this(new Point(x, y), degrees);
    }

    public Ray(Point origin, float degrees)
    {
        this(
                origin,
                new Point(
                        (float) Math.cos(degrees * (Math.PI / 180)),
                        (float) Math.sin(degrees * (Math.PI / 180))
                )
        );
    }

    public Ray(Point origin, Point direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Point getPointOfCollision(Boundary boundary)
    {
        float t = intersectsWith(boundary);
        return t != -1
                ? new Point(
                    boundary.start.x + (t * (boundary.end.x - boundary.start.x)),
                    boundary.start.y + (t * (boundary.end.y - boundary.start.y))
                )
                : null;
    }

    private float intersectsWith(Boundary boundary)
    {
        float denominator = (boundary.start.x - boundary.end.x) * (this.origin.y - (this.origin.y + this.direction.y)) - (boundary.start.y - boundary.end.y) * (this.origin.x - (this.origin.x + this.direction.x));

        if (denominator == 0)
            return -1f;

        float t = ((boundary.start.x - this.origin.x) * (this.origin.y - (this.origin.y + this.direction.y)) - (boundary.start.y - this.origin.y) * (this.origin.x - (this.origin.x + this.direction.x))) / denominator;
        float u = -((boundary.start.x - boundary.end.x) * (boundary.start.y - this.origin.y) - (boundary.start.y - boundary.end.y) * (boundary.start.x - this.origin.x)) / denominator;

        return 0 < t && t < 1 && u > 0 ? t : -1f;
    }
}
