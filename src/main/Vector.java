package main;

public class Vector {
    public float x;
    public float y;
    public float z;

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float dotProduct(Vector v){
        return x*v.x + y*v.y + z*v.z;
    }

    public Vector multiply(float c){
        return new Vector(c*x, c*y, c*z);
    }

    public Vector subtract(Vector v){
        return new Vector(x-v.x, y-v.y, z-v.z);
    }
}
