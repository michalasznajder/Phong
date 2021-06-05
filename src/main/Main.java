package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {


        Surface surface = new Surface(
                new Coefficients(1f, 1f, 1f), // DIFFERED COEFFICIENTS
                new Coefficients(1f, 1f, 1f), // SPECULAR COEFFICIENTS
                new Coefficients(0.5f, 0.5f, 0.5f), // AMBIENT COEFFICIENTS
                10
        );
        Coefficients ambient = new Coefficients(0.1f, 0.1f, 0.1f); // AMBIENT RGB INTENSITIES

        List<Light> lightSources = new ArrayList<>();
        lightSources.add(
                new Light(0, 0, 20,
                        new Coefficients(1f, 0f, 0f))
        );

        lightSources.add(
                new Light(0, 0, 20,
                        new Coefficients(0f, 0f, 1f))
        );

        lightSources.add(
                new Light(0, 0, 20,
                        new Coefficients(0f, 0f, 0f))
        );

        int i_res = 300;
        int j_res = 300;

        float r = 10f;

        float c_0 = 0.1f;
        float c_1 = 0.01f;
        float c_2 = 0.01f;

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

                    if (N.dotProduct(I) > 0.5) {
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
            ImageIO.write(resultImage, "png", new File("sphere.png"));
        } catch (IOException var14) {
            System.out.println("The image cannot be stored");
        }





    }
}
