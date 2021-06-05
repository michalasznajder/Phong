package main;

public class Surface {
    public Coefficients D;
    public Coefficients S;
    public Coefficients A;
    public float g;

    public Surface(Coefficients d, Coefficients s, Coefficients a, float g) {
        D = d;
        S = s;
        A = a;
        this.g = g;
    }
}
