package Kratka;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class ColorScale {

    public double max = 0;
    public double min = 10;

    public double minColor = Color.BLUE.getHue();
    public double maxColor = Color.RED.getHue();

    public ColorScale(double min, double max){
        this.min = min;
        this.max = max;
    }

    public void changeMin(double min){
        this.min = min;
    }

    public void changeMax(double max){
        this.max = max;
    }

    public void changeMinColor(Color min){this.minColor = min.getHue();}

    public void changeMaxColor(Color max){this.maxColor = max.getHue();}
    public Color ColorOfValue (double value){
        if (value < min || value > max){
            return Color.BLACK;
        }
        double hue = minColor + maxColor - minColor * (value - min)/(max - min);
        return Color.hsb(hue, 1.0, 1.0);
    }

    public Image DrawColorScale(int width, int height){
        WritableImage image = new WritableImage(width,height);
        PixelWriter pixelWriter = image.getPixelWriter();
        for (int i = 0; i < width; i++){
            double value = min + (max - min) * i / width;
            Color color = ColorOfValue(value);
            for (int j = 0; j < height; j++)
                pixelWriter.setColor(i,j,color);
        }
        return image;
    }
}
