import java.util.Arrays;
import java.lang.Math.*;

public class ImageProcessor {
    public static void main(String[] args) {
        // Check arguments
        if (args.length != 2) {
            System.err.println("arguments: inputFile outputFile");
            return;
        }

        int[][] bmp = null;

        try {
            bmp = BitMap.ReadGreyScale(args[0]);
            Display.DisplayGreyscale(bmp);
            BitMap.OutputGreyScale(args[1], bmp);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void FlipHorizontally(int[][] bmp) {
        // TODO: Write code to flip the bmp matrix horizontally
        int height = bmp.length;
        int width = bmp[0].length;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width/2; ++j) {
                int temp = bmp[i][j];
                bmp[i][j] = bmp[i][width-1-j];
                bmp[i][width-1-j] = temp;
            }
        }
    }

    public static void FlipVertically(int[][] bmp) {
        // TODO: Write code to flip the bmp matrix vertically
        int height = bmp.length;
        int width = bmp[0].length;
        for (int i = 0; i < height/2; ++i) {
            for (int j = 0; j < width; ++j) {
                int temp = bmp[i][j];
                bmp[i][j] = bmp[height - 1 - i][j];
                bmp[height - 1 - i][j] = temp;
            }
        }
    }

    public static void Transpose(int[][] bmp) {
        // TODO: Write code to transpose the matrix
        int height = bmp.length;
        int width = bmp[0].length;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < i; ++j) {
                int temp = bmp[i][j];
                bmp[i][j] = bmp[j][i];
                bmp[j][i] = temp;
            }
        }
    }

    public static void GaussianBlur(int[][] bmp, int k, double sigma) {
        // K must be odd
        if (k % 2 == 0)
            return;
        double[][] kernel = GenerateGaussianKernel(k, sigma);

        // Challenge:
        int mid = k / 2;
        int height = bmp.length;
        int width = bmp[0].length;
        int[][] result = new int[height][width];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int new_pixel = 0;
                for (int y = -mid; y <= mid; ++y) {
                    for (int x = -mid; x <= mid; ++x) {
                        // Check bounds
                        if ((i + y) < 0 || (i + y) >= height || (j + x) < 0 || (j + x) >= width) {
                            continue;
                        }
                        new_pixel += (int)(kernel[mid + y][mid+x] * bmp[i+y][x+j]);
                    }
                }
                bmp[i][j] = new_pixel;
            }
        }
    }


    // Ignore code down here unless you are curious
    public static double[][] GenerateGaussianKernel(int k, double sigma) {
        double[][] kernel = new double[k][k];

        int mid = k / 2;

        // Fill out the quadrants
        double sum = 0;
        for (int i = 0; i < mid; ++i) {
            for (int j = 0; j < mid; ++j) {
                double gaus = Gaussian(-3 + j, -3 + i, sigma);
                kernel[i][j] = gaus;
                kernel[i][k-1-j] = gaus;
                kernel[k-1-i][j] = gaus;
                kernel[k-1-i][k-1-j] = gaus;
                sum += (gaus * 4);
            }
        }

        // Fill out the middle
        for (int i = 0; i < mid; ++i) {
            double gaus = Gaussian(-3+i, 0, sigma);
            kernel[mid][i] = gaus;
            kernel[mid][k-1-i] = gaus;
            kernel[i][mid] = gaus;
            kernel[k-1-i][mid] = gaus;
            sum += (gaus * 4);
        }

        // Fill middle
        kernel[mid][mid] = Gaussian(0, 0, sigma);
        sum += kernel[mid][mid];

        // Normalize
        for (int i = 0; i < k; ++i) {
            for (int j = 0; j < k; ++j) {
                kernel[i][j] /= sum;
            }
        }

        return kernel;
    }

    public static double Gaussian(int x, int y, double sigma) {
        double exponent = -1 * ((Math.pow(x, 2) + Math.pow(y,2)) / (2 * Math.pow(sigma, 2)));
        double base = 1.0 / (2 * Math.PI * Math.pow(sigma, 2));
        return base * Math.exp(exponent);
    }
}
