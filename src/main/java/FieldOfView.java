public class FieldOfView
{
    int start;
    int end;

    public FieldOfView()
    {
        this(70);
    }

    public FieldOfView(int degrees)
    {
        setDegrees(degrees);
    }

    public void rotate(int degrees) {
        this.start += degrees;
        this.end += degrees;
    }

    public float degrees()
    {
        return this.end - this.start;
    }

    public void setDegrees(int degrees)
    {
        this.start = -(degrees / 2);
        this.end = (degrees / 2);
    }
}
