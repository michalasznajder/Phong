package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {



        int i_res = 300;
        int j_res = 300;

        float r = 10f;

        float k_d_r = 1.0f;
        float k_d_g = 0.0f;
        float k_d_b = 1.0f;


        float k_a_r = 0.5f;
        float k_a_g = 0.5f;
        float k_a_b = 0.5f;

        float c_0 = 0;
        float c_1 = 0;
        float c_2 = 1;

        float source_x = 15.0f;
        float source_y = 15.0f;
        float source_z = 15.0f;

        float E = 1.0f;
        float A = 0.2f;

        BufferedImage resultImage = new BufferedImage(i_res, j_res, 1);

        for (int i = 0; i < i_res; i++) {
            for (int j = 0; j < j_res; j++) {

                float x_int = r*(2.0f*i/(i_res-1) - 1);
                float y_int = r*(1 - 2.0f*j/(j_res-1));
                if(x_int*x_int + y_int*y_int > r*r)
                    continue;

                float z_int = (float)Math.sqrt(r*r - x_int*x_int - y_int*y_int);
                Vector N = new Vector((x_int/r), (y_int/r), (z_int/r));
                float d = (float)Math.sqrt(((source_x-x_int)*(source_x-x_int) + (source_y-y_int)*(source_y-y_int) + (source_z-z_int)*(source_z-z_int)));
                Vector I = new Vector((source_x-x_int)/d, (source_y-y_int)/d, (source_z-z_int)/d);


//                DIFFERED LIGHT
                float differed_light_intensity = Math.min(1/c_2*d*d, 1) * E * N.dotProduct(I);
                float L_d_r = k_d_r * differed_light_intensity;
                float L_d_g = k_d_g * differed_light_intensity;
                float L_d_b = k_d_b * differed_light_intensity;

                if(L_d_r < 0){
                    L_d_r = 0;
                }
                if(L_d_g < 0){
                    L_d_g = 0;
                }
                if(L_d_b < 0){
                    L_d_b = 0;
                }



//                AMBIENT LIGHT
                float L_a_r = k_a_r * A;
                float L_a_g = k_a_g * A;
                float L_a_b = k_a_b * A;

                float L_r = L_d_r + L_a_r;
                float L_g = L_d_g + L_a_g;
                float L_b = L_d_b + L_a_b;

                if(L_r > 1){
                    L_r = 1;
                }
                if(L_g > 1){
                    L_g = 1;
                }
                if(L_b > 1){
                    L_b = 1;
                }

                Color color = new Color(L_r, L_g, L_b);
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
