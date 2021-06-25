package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {


        renderMultiple();


    }

    public static void render(){


        Surface surface = new Surface(
                new Coefficients(0.6f, 0.2f, 0.6f), // DIFFERED COEFFICIENTS
                new Coefficients(0.4f, 0.3f, 0.4f), // SPECULAR COEFFICIENTS
                new Coefficients(0.02f, 0.02f, 0.02f), // AMBIENT COEFFICIENTS
                10
        );
        Coefficients ambient = new Coefficients(1f, 1f, 1f); // AMBIENT RGB INTENSITIES

        List<Light> lightSources = new ArrayList<>();
        lightSources.add(
                new Light(10, 10, 15,
                        new Coefficients(1f, 0f, 0f))
        );

        lightSources.add(
                new Light(20, -5, 15,
                        new Coefficients(0f, 1f, 0f))
        );


        lightSources.add(
                new Light(-15, -15, 15,
                        new Coefficients(0f, 0f, 1f))
        );

        int i_res = 300;
        int j_res = 300;

        float r = 10f;

        float c_0 = 0.1f;
        float c_1 = 0.01f;
        float c_2 = 0.001f;

        BufferedImage resultImage = new BufferedImage(i_res, j_res, 1);

        for (int i = 0; i < i_res; i++) {
            for (int j = 0; j < j_res; j++) {

                float x_int = r*(2.0f*i/(i_res-1) - 1);
                float y_int = r*(1 - 2.0f*j/(j_res-1));
                if(x_int*x_int + y_int*y_int > r*r)
                    continue;

                float L_r = 0;
                float L_g = 0;
                float L_b = 0;

                for(Light light: lightSources) {


                    float z_int = (float) Math.sqrt(r * r - x_int * x_int - y_int * y_int);

                    Vector N = new Vector((x_int / r), (y_int / r), (z_int / r));
                    float d = (float) Math.sqrt(((light.x - x_int) * (light.x - x_int) + (light.y - y_int) * (light.y - y_int) + (light.z - z_int) * (light.z - z_int)));
                    Vector I = new Vector((light.x - x_int) / d, (light.y - y_int) / d, (light.z - z_int) / d);

                    float att = Math.min(1 / (c_2 * d * d + c_1 * d + c_0), 1);
//                DIFFERED LIGHT

                    float differed_light_intensity_r = att * light.E.r * N.dotProduct(I);
                    float differed_light_intensity_g = att * light.E.g * N.dotProduct(I);
                    float differed_light_intensity_b = att * light.E.b * N.dotProduct(I);

                    float L_d_r = Math.max(surface.D.r * differed_light_intensity_r, 0);
                    float L_d_g = Math.max(surface.D.g * differed_light_intensity_g, 0);
                    float L_d_b = Math.max(surface.D.b * differed_light_intensity_b, 0);

//                GLOSSY LIGHT
                    Vector from_observer = new Vector(0, 0, -1);
//                r=d−2(d⋅n)n

                    Vector Os = from_observer.subtract(N.multiply(2 * N.dotProduct(from_observer)));

                    float L_s_r = 0;
                    float L_s_g = 0;
                    float L_s_b = 0;

                    if (N.dotProduct(I) > 0) {
                        float specular_light_intensity_r = (float) (att * light.E.r * Math.pow(I.dotProduct(Os), surface.g));
                        float specular_light_intensity_g = (float) (att * light.E.g * Math.pow(I.dotProduct(Os), surface.g));
                        float specular_light_intensity_b = (float) (att * light.E.b * Math.pow(I.dotProduct(Os), surface.g));

                        L_s_r = Math.max(surface.S.r * specular_light_intensity_r, 0);
                        L_s_g = Math.max(surface.S.g * specular_light_intensity_g, 0);
                        L_s_b = Math.max(surface.S.b * specular_light_intensity_b, 0);
                    }


//                AMBIENT LIGHT
                    float L_a_r = surface.A.r * ambient.r;
                    float L_a_g = surface.A.g * ambient.g;
                    float L_a_b = surface.A.b * ambient.b;

                    L_r += L_d_r + L_a_r + L_s_r;
                    L_g += L_d_g + L_a_g + L_s_g;
                    L_b += L_d_b + L_a_b + L_s_b;
                }



                Color color = new Color(Math.min(1, L_r), Math.min(1, L_g), Math.min(1, L_b));
                resultImage.setRGB(i, j, color.getRGB());
            }
        }

        try {
            ImageIO.write(resultImage, "png", new File("gypswithtexture.png"));
        } catch (IOException var14) {
            System.out.println("The image cannot be stored");
        }

    }

    public static void withTexture() throws IOException {
        Surface surface = new Surface(
                new Coefficients(0.7f, 0.7f, 0.7f), // DIFFERED COEFFICIENTS
                new Coefficients(0.5f, 0.5f, 0.5f), // SPECULAR COEFFICIENTS
                new Coefficients(0.3f, 0.3f, 0.3f), // AMBIENT COEFFICIENTS
                10
        );
        Coefficients ambient = new Coefficients(1f, 1f, 1f); // AMBIENT RGB INTENSITIES

        List<Light> lightSources = new ArrayList<>();
        lightSources.add(
                new Light(1, 1, 15,
                        new Coefficients(0f, 1f, 1f))
        );






        float r = 10f;

        float c_0 = 0.1f;
        float c_1 = 0.01f;
        float c_2 = 0.001f;

        BufferedImage resultImage = ImageIO.read(new File("magda.png"));

        int i_res = resultImage.getHeight();
        int j_res = resultImage.getWidth();

        for (int i = 0; i < i_res; i++) {
            for (int j = 0; j < j_res; j++) {

                float x_int = r*(2.0f*i/(i_res-1) - 1);
                float y_int = r*(1 - 2.0f*j/(j_res-1));
                if(x_int*x_int + y_int*y_int > r*r){
                    resultImage.setRGB(i, j, Color.BLACK.getRGB());
                    continue;
                }

                float L_r = 0;
                float L_g = 0;
                float L_b = 0;

                for(Light light: lightSources) {


                    float z_int = (float) Math.sqrt(r * r - x_int * x_int - y_int * y_int);

                    Vector N = new Vector((x_int / r), (y_int / r), (z_int / r));
                    float d = (float) Math.sqrt(((light.x - x_int) * (light.x - x_int) + (light.y - y_int) * (light.y - y_int) + (light.z - z_int) * (light.z - z_int)));
                    Vector I = new Vector((light.x - x_int) / d, (light.y - y_int) / d, (light.z - z_int) / d);

                    float att = Math.min(1 / (c_2 * d * d + c_1 * d + c_0), 1);
//                DIFFERED LIGHT

                    float differed_light_intensity_r = att * light.E.r * N.dotProduct(I);
                    float differed_light_intensity_g = att * light.E.g * N.dotProduct(I);
                    float differed_light_intensity_b = att * light.E.b * N.dotProduct(I);

                    float L_d_r = Math.max(surface.D.r * differed_light_intensity_r, 0);
                    float L_d_g = Math.max(surface.D.g * differed_light_intensity_g, 0);
                    float L_d_b = Math.max(surface.D.b * differed_light_intensity_b, 0);

//                GLOSSY LIGHT
                    Vector from_observer = new Vector(0, 0, -1);
//                r=d−2(d⋅n)n

                    Vector Os = from_observer.subtract(N.multiply(2 * N.dotProduct(from_observer)));

                    float L_s_r = 0;
                    float L_s_g = 0;
                    float L_s_b = 0;

                    if (N.dotProduct(I) > 0) {
                        float specular_light_intensity_r = (float) (att * light.E.r * Math.pow(I.dotProduct(Os), surface.g));
                        float specular_light_intensity_g = (float) (att * light.E.g * Math.pow(I.dotProduct(Os), surface.g));
                        float specular_light_intensity_b = (float) (att * light.E.b * Math.pow(I.dotProduct(Os), surface.g));

                        L_s_r = Math.max(surface.S.r * specular_light_intensity_r, 0);
                        L_s_g = Math.max(surface.S.g * specular_light_intensity_g, 0);
                        L_s_b = Math.max(surface.S.b * specular_light_intensity_b, 0);
                    }


//                AMBIENT LIGHT
                    float L_a_r = surface.A.r * ambient.r;
                    float L_a_g = surface.A.g * ambient.g;
                    float L_a_b = surface.A.b * ambient.b;

                    L_r += L_d_r + L_a_r + L_s_r;
                    L_g += L_d_g + L_a_g + L_s_g;
                    L_b += L_d_b + L_a_b + L_s_b;
                }

                Color currentColor = new Color(resultImage.getRGB(i, j));

                Color color = new Color(Math.min(1, L_r*currentColor.getRed()/255), Math.min(1, L_g*currentColor.getBlue()/255), Math.min(1, L_b*currentColor.getGreen()/255));
                resultImage.setRGB(i, j, color.getRGB());
            }
        }

        try {
            ImageIO.write(resultImage, "png", new File("magdawkuli.png"));
        } catch (IOException var14) {
            System.out.println("The image cannot be stored");
        }

    }


    public static void renderMultiple(){


        Surface surface = new Surface(
                new Coefficients(0.3f, 0.3f, 0.3f), // DIFFERED COEFFICIENTS
                new Coefficients(0.4f, 0.4f, 0.4f), // SPECULAR COEFFICIENTS
                new Coefficients(0.1f, 0.1f, 0.1f), // AMBIENT COEFFICIENTS
                10
        );
        Coefficients ambient = new Coefficients(1f, 1f, 1f); // AMBIENT RGB INTENSITIES

        List<Light> lightSources = new ArrayList<>();
        lightSources.add(
                new Light(1, 1, 1,
                        new Coefficients(0f, 1f, 0f))
        );

        lightSources.add(
                new Light(-10, 10, 20,
                        new Coefficients(0f, 0f, 1f))
        );

        lightSources.add(
                new Light(-10, -15, 20,
                        new Coefficients(1f, 0f, 0f))
        );

        List<Sphere> spheres = new ArrayList<>();

        //                (0, 0, 0, 10)
//                (-7, 0, -7, 3)
//                (7, 0, -7, 3)
//                (0, 0, -15, 2)


        spheres.add(new Sphere(0, 0, 0, 10));
        spheres.add(new Sphere(-7, 0, -12, 3));
        spheres.add(new Sphere(7, 5, 15, 3));
        spheres.add(new Sphere(0, 0, 15, 2));

        int i_res = 300;
        int j_res = 300;

        float r = 10f;

        float c_0 = 0.1f;
        float c_1 = 0.01f;
        float c_2 = 0.001f;

        BufferedImage resultImage = new BufferedImage(i_res, j_res, 1);

        Sphere firstSphere = spheres.get(0);

        for (int i = 0; i < i_res; i++) {
            for (int j = 0; j < j_res; j++) {

                float x_int = firstSphere.r*(2.0f*i/(i_res-1) - 1);
                float y_int = firstSphere.r*(1 - 2.0f*j/(j_res-1));
//                if(x_int*x_int + y_int*y_int > firstSphere.r*firstSphere.r)
//                    continue;

                float L_r = 0;
                float L_g = 0;
                float L_b = 0;

                for(Light light: lightSources) {

//                    FIND THE RIGHT SPHERE - WITH MAX Z
                    float z_int = (float) Math.sqrt(firstSphere.r * firstSphere.r - x_int * x_int - y_int * y_int);
                    Sphere sphere = firstSphere;
                    if(Float.isNaN(z_int)){
                        z_int = Float.MIN_VALUE;
                    }
                    for(Sphere s : spheres){
                        float biggest_z_int = s.z + ((float) Math.sqrt(s.r * s.r - ((x_int-s.x) * (x_int-s.x)) - ((y_int-s.y) * (y_int-s.y))));
                        if(biggest_z_int > z_int){
                            z_int = biggest_z_int;
                            sphere = s;
                        }
                    }
                    if(z_int == Float.MIN_VALUE){
                        continue;
                    }

                    Vector N = new Vector(((x_int- sphere.x) / sphere.r), ((y_int- sphere.y) / sphere.r), ((z_int- sphere.z) / sphere.r));
                    float d = (float) Math.sqrt(((light.x - x_int) * (light.x - x_int) + (light.y - y_int) * (light.y - y_int) + (light.z - z_int) * (light.z - z_int)));
                    Vector I = new Vector((light.x - x_int) / d, (light.y - y_int) / d, (light.z - z_int) / d);

                    float att = Math.min(1 / (c_2 * d * d + c_1 * d + c_0), 1);
//                DIFFERED LIGHT

                    float differed_light_intensity_r = att * light.E.r * N.dotProduct(I);
                    float differed_light_intensity_g = att * light.E.g * N.dotProduct(I);
                    float differed_light_intensity_b = att * light.E.b * N.dotProduct(I);

                    float L_d_r = Math.max(surface.D.r * differed_light_intensity_r, 0);
                    float L_d_g = Math.max(surface.D.g * differed_light_intensity_g, 0);
                    float L_d_b = Math.max(surface.D.b * differed_light_intensity_b, 0);

//                GLOSSY LIGHT
                    Vector from_observer = new Vector(0, 0, -1);
//                r=d−2(d⋅n)n

                    Vector Os = from_observer.subtract(N.multiply(2 * N.dotProduct(from_observer)));

                    float L_s_r = 0;
                    float L_s_g = 0;
                    float L_s_b = 0;

                    if (Os.dotProduct(I) > 0) {
                        float specular_light_intensity_r = (float) (att * light.E.r * Math.pow(I.dotProduct(Os), surface.g));
                        float specular_light_intensity_g = (float) (att * light.E.g * Math.pow(I.dotProduct(Os), surface.g));
                        float specular_light_intensity_b = (float) (att * light.E.b * Math.pow(I.dotProduct(Os), surface.g));

                        L_s_r = Math.max(surface.S.r * specular_light_intensity_r, 0);
                        L_s_g = Math.max(surface.S.g * specular_light_intensity_g, 0);
                        L_s_b = Math.max(surface.S.b * specular_light_intensity_b, 0);
                    }


//                AMBIENT LIGHT
                    float L_a_r = surface.A.r * ambient.r;
                    float L_a_g = surface.A.g * ambient.g;
                    float L_a_b = surface.A.b * ambient.b;

                    L_r += L_d_r + L_a_r + L_s_r;
                    L_g += L_d_g + L_a_g + L_s_g;
                    L_b += L_d_b + L_a_b + L_s_b;
                }





                Color color = new Color(Math.min(1, L_r), Math.min(1, L_g), Math.min(1, L_b));
                resultImage.setRGB(i, j, color.getRGB());
            }
        }

        try {
            ImageIO.write(resultImage, "png", new File("test.png"));
        } catch (IOException var14) {
            System.out.println("The image cannot be stored");
        }

    }

}
