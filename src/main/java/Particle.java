import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Particle
{
    private final float numberOfRays;
    Point location;
    List<Ray> rays;
    FieldOfView fov;

    public Particle(float x1, float y1, int fovDegrees, int numberOfRays)
    {
        this(new Point(x1, y1), new FieldOfView(fovDegrees), numberOfRays);
    }

    public Particle(Point location, FieldOfView fov, int numberOfRays)
    {
        this.location = location;
        this.fov = fov;
        this.numberOfRays = numberOfRays;
        updateRays();
    }

    public List<Ray> castRays(List<Boundary> boundaries)
    {
        for (Ray ray : rays)
        {
            Point closestPointOfCollision = null;
            float minDistanceToCollision = 100000f;

            for (Boundary boundary : boundaries)
            {
                Point currentPointOfCollision = ray.getPointOfCollision(boundary);

                if (currentPointOfCollision == null)
                    continue;

                float distanceToCollision = ray.origin.distanceTo(currentPointOfCollision);
                if (minDistanceToCollision > distanceToCollision)
                {
                    minDistanceToCollision = distanceToCollision;
                    closestPointOfCollision = currentPointOfCollision;
                }
            }

            if (closestPointOfCollision != null)
                ray.collision = closestPointOfCollision;
        }

        return rays;
    }

    public void updateRays()
    {
        this.rays = new CopyOnWriteArrayList<>();

        for (float i = this.fov.start; i < this.fov.end; i += (this.fov.degrees() / numberOfRays))
            rays.add(new Ray(location, i));
    }

    public void updatePosition(Point point) {
        this.location = point;
    }

    public void updateFov(int newDegrees)
    {
        fov.setDegrees(newDegrees);
        updateRays();
    }
}
