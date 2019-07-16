package uk.ac.soton.ecs.rf6g15;
import org.openimaj.image.FImage;
import org.openimaj.image.processing.convolution.Gaussian2D;
import org.openimaj.image.processor.SinglebandImageProcessor;

public class MyConvolution implements SinglebandImageProcessor <Float, FImage > {
    private float[][] kernel; //The kernel used during the convolution
    private int width;      //The width of the kernel
    private int height;        // The height of the kernel

    //Constructor that contains the kernel
    public MyConvolution(float[][] kernel) {
        height = kernel.length;
        width = kernel[0].length;
        if(height%2==1 && width%2==1)
            this.kernel = kernel;
        else
            System.out.println("Invalid Kernel try changing the size");
    }

    //Constructor that uses the size and sigma to create the kernel
    public MyConvolution(int size, float sigma) {
        if (size % 2 == 0) size++; //size must be odd
        FImage GaussianImage = Gaussian2D.createKernelImage(size, sigma);
        setKernel(GaussianImage, size, size);
    }

    //Constructor that only uses the sigma to get the kernel
    public MyConvolution(float sigma) {
        int size = (int) (8.0f * sigma + 1.0f);
        if (size % 2 == 0) size++;
        FImage GaussianImage = Gaussian2D.createKernelImage(size, sigma);
        setKernel(GaussianImage, size, size);
    }

    //Constructor that creates non-symmetrical kernels using Width and Height
    public MyConvolution(int Width, int Height, float sigma)
    {
        if(Width%2==0) Width++;
        if(Height%2==0) Height++;
        FImage GaussianImage = Gaussian2D.createKernelImage(Width,Height, sigma);
        setKernel(GaussianImage,Width,Height);
    }

    //The function that transforms a picture into a float matrix that can be used in convolution
    private void setKernel(FImage GaussianImage,int width,int height)
    {
        this.width = width;
        this.height=height;
        float[][] kernel1 = new float[height][width];
        for (int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                kernel1[y][x]= GaussianImage.pixels[y][x];
            }
        }
        this.kernel = kernel1;
    }

    //function to retrieve the kernel if needed
    public float[][] getKernel(){
        return this.kernel;
    }


    //Function that does the actual convolution
    @Override
    public void processImage(FImage image) {

        //Making sure the convoluted picture has the same dimensions as the original
        FImage convolutedImage = new FImage(image.getWidth(),image.getHeight());

        //prepare picture for convolution by making every pixel outside the picture 0
        prepareBorders(image);


        float pixel;
        for (int y=0; y<convolutedImage.getHeight(); y++) {
            for(int x=0; x<convolutedImage.getWidth(); x++) {
                pixel = 0f;
                for(int y1=0;y1<height;y1++) {
                    for(int x1=0;x1<width;x1++){
                        pixel+=kernel[y1][x1]*image.pixels[y1+y][x1+x];
                    }
                }
                convolutedImage.pixels[y][x]=pixel;
            }
        }
        image.internalAssign(convolutedImage);

    }

    //Function that makes the borders black based on the sizes of the kernel
    private void prepareBorders(FImage image){

        FImage imageWithBorders = new FImage(image.getWidth()+width,image.getHeight()+height);
        imageWithBorders.fill(0f);
        for (int y=0; y<image.getHeight(); y++) {
            for(int x=0; x<image.getWidth(); x++) {

                imageWithBorders.pixels[y+height/2][x+width/2] = image.pixels[y][x];

            }
        }
        image.internalAssign(imageWithBorders);
    }

}

