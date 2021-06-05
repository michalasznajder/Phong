package main;

public class Light {
    public float x;
    public float y;
    public float z;

    public Coefficients E;

    public Light(float x, float y, float z, Coefficients e) {
        this.x = x;
        this.y = y;
        this.z = z;
        E = e;
    }
}
