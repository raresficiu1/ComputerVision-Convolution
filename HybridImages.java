package uk.ac.soton.ecs.rf6g15;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.resize.BilinearInterpolation;
import org.openimaj.image.processing.resize.ResizeProcessor;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class HybridImages{

    public static void main(String[] args) throws IOException {



        MBFImage LowFrequency = ImageUtilities.readMBF(new File("src/main/motorcycle.bmp"));//Reading the first picture
        MBFImage HighFrequency = ImageUtilities.readMBF(new File("src/main/bicycle.bmp"));//Reading the second picture

        JFrame frame = DisplayUtilities.createNamedWindow("Hybrid Image", "Hybrid Image", false); //Creating the main frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setPreferredSize(new Dimension(HighFrequency.getWidth()*2, HighFrequency.getHeight()+28));//The size of the frame
        MBFImage LowFrequencyFinal = LowFrequency.process(new MyConvolution(3));    // Creating the low frequency picture
        MBFImage HighFrequencyAlmost = HighFrequency.process(new MyConvolution(7)); // Creating the low frequency part of the second picture
        MBFImage HighFrequencyFinal = getHighFrequency(HighFrequencyAlmost,HighFrequency); //By subtracting the the low frequency picture from the normal
                                                                                           // one we are left with the high frequency one

        MBFImage Final = getFinal(LowFrequencyFinal,HighFrequencyFinal) ; //Combining the 2 pictures by adding them together

        viewFinal(Final,frame);

    }


    //Creating the high Frequency picture
    public static MBFImage getHighFrequency (MBFImage lowFrequency, MBFImage original)
    {
        for (int y=0; y<original.getHeight(); y++) {
            for(int x=0; x<original.getWidth(); x++) {
                original.getBand(0).pixels[y][x] -= lowFrequency.getBand(0).pixels[y][x];
                original.getBand(1).pixels[y][x] -= lowFrequency.getBand(1).pixels[y][x];
                original.getBand(2).pixels[y][x] -= lowFrequency.getBand(2).pixels[y][x];
            }
        }
        return original;
    }

    //Adding the 2 photos together
    public static MBFImage getFinal (MBFImage lowFrequency, MBFImage highFrequency){
        MBFImage Final = new MBFImage(lowFrequency.getWidth()*2, lowFrequency.getHeight(), ColourSpace.RGB);
        Final.getBand(0).fill(255);
        Final.getBand(1).fill(255);
        Final.getBand(2).fill(255);
        for (int y=0; y<Final.getHeight(); y++) {
            for(int x=0; x<Final.getWidth()/2; x++) {
                Final.getBand(0).pixels[y][x] = lowFrequency.getBand(0).pixels[y][x]+highFrequency.getBand(0).pixels[y][x];
                Final.getBand(1).pixels[y][x] = lowFrequency.getBand(1).pixels[y][x]+highFrequency.getBand(1).pixels[y][x];
                Final.getBand(2).pixels[y][x] = lowFrequency.getBand(2).pixels[y][x]+highFrequency.getBand(2).pixels[y][x];
            }
        }

        return Final;

    }


    //Displaying them in different sizes so the effect is visible
    public static void viewFinal (MBFImage Final, JFrame frame)
    {

        MBFImage Picture1 = Final.process(new BilinearInterpolation(Final.getWidth()/2,Final.getHeight(),1f));
        MBFImage Picture2 = ResizeProcessor.halfSize(Picture1);
        MBFImage Picture3 = ResizeProcessor.halfSize(Picture2);
        MBFImage Picture4 = ResizeProcessor.halfSize(Picture3);
        MBFImage Picture5 = ResizeProcessor.halfSize(Picture4);
        Final.drawImage(Picture2,Final.getWidth()/2+5,Final.getHeight()/2);
        Final.drawImage(Picture3,Final.getWidth()/2+Final.getWidth()/4+14,Final.getHeight()/2+Final.getHeight()/4);
        Final.drawImage(Picture4,Final.getWidth()/2+Final.getWidth()/4+Final.getWidth()/8+20,Final.getHeight()/2+Final.getHeight()/4+Final.getHeight()/8);
        Final.drawImage(Picture5,(Final.getWidth()/2+Final.getWidth()/4+Final.getWidth()/8+Final.getWidth()/16)+25,Final.getHeight()/2+Final.getHeight()/4+Final.getHeight()/8+Final.getHeight()/16);
        DisplayUtilities.display(Final,frame);
    }

}



