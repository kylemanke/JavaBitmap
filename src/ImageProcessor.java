import javax.imageio.ImageTranscoder;
import java.util.Properties;
import java.util.Scanner;

public class ImageProcessor {
    public static void FlipHorizontally(int[][] bmp) {
        // TODO: Write code to flip the bmp matrix horizontally
        int temp;
        int n = bmp.length, m = bmp[0].length;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m / 2; ++j) {
                temp = bmp[i][j];
                bmp[i][j] = bmp[i][m-j-1];
                bmp[i][m-j-1] = temp;
            }
        }
    }

    public static void FlipHorizontally(int[][][] bmp) {
        // TODO: Write code to flip the bmp matrix horizontally
        int[] temp;
        int n = bmp.length, m = bmp[0].length, rgb = 3;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m/2; ++j) {
                temp = bmp[i][m-1-j];
                bmp[i][m-1-j] = bmp[i][j];
                bmp[i][j] = temp;
            }
        }
    }

    public static void FlipVertically(int[][] bmp) {
        // TODO: Write code to flip the bmp matrix vertically
        int temp;
        int n = bmp.length, m = bmp[0].length;
        for (int i = 0; i < n/2; ++i) {
            for (int j = 0; j < m; ++j) {
                temp = bmp[i][j];
                bmp[i][j] = bmp[n-1-i][j];
                bmp[n-1-i][j] = temp;
            }
        }
    }

    public static void FlipVertically(int[][][] bmp) {
        // TODO: Write code to flip the bmp matrix vertically
        int[] temp;
        int n = bmp.length, m = bmp[0].length;
        for (int i= 0; i < n/2; ++i) {
            for (int j = 0; j < m; ++j) {
                temp = bmp[i][j];
                bmp[i][j] = bmp[n-1-i][j];
                bmp[n-1-i][j] = temp;
            }
        }
    }

    public static int[][] Transpose(int[][] bmp) {
        // TODO: Write code to transpose the matrix
        int n = bmp.length, m = bmp[0].length;
        int[][] new_bmp = new int[m][n];

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                new_bmp[j][i] = bmp[i][j];
            }
        }

        return new_bmp;
    }

    public static int[][][] Transpose(int[][][] bmp) {
        // TODO: Write code to transpose the matrix
        int n = bmp.length, m = bmp[0].length;
        int[][][] new_bmp = new int[m][n][];

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                new_bmp[j][i] = bmp[i][j];
            }
        }

        return new_bmp;
    }

    public static void Invert(int[][] bmp) {
        // TODO: Write code to invert the matrix
        int n = bmp.length, m = bmp[0].length;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                bmp[i][j] = 255 - bmp[i][j];
            }
        }
    }

    public static void Invert(int[][][] bmp) {
        // TODO: Write code to invert the matrix
        int n = bmp.length, m = bmp[0].length;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                bmp[i][j][0] = 255 - bmp[i][j][0];
                bmp[i][j][1] = 255 - bmp[i][j][1];
                bmp[i][j][2] = 255 - bmp[i][j][2];
            }
        }
    }

    // Gaussian Blurring: thanks to https://homepages.inf.ed.ac.uk/rbf/HIPR2/gsmooth.htm
    public static int[][] GaussianBlur(int[][] bmp, int k, double sigma) throws Exception {
        // Kernel must be odd and positive
        if (k % 2 == 0) {
            throw new Exception("Kernel must have an odd size.");
        }
        k = Math.abs(k);

        // Sigma must be positive
        sigma = Math.abs(sigma);
        if (sigma > 15)
            sigma = 15;

        // Generate the kernel
        double[][] kernel = generate2DKernel(k, sigma);

        // Create the output array
        int n = bmp.length, m = bmp[0].length;
        int[][] output = new int[n][m];

        // Perform the pass
        int k_2 = k/2;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                for (int z = -k_2; z <= k_2; ++z) {
                    for (int y = -k_2; y <= k_2; ++y) {
                        if (i + z >= 0 && i + z < n && j + y >= 0 && j + y < m) {
                            output[i][j] += (int)(kernel[k_2 + z][k_2 + y] * bmp[i + z][j + y]);
                        }
                    }
                }
            }
        }

        return output;
    }

    // TODO: Fix gaussian
    public static int[][][] GaussianBlur(int[][][] bmp, int k, double sigma) throws Exception {
        // Check k
        if (k % 2 == 0)
            throw new Exception("Kernel size must be odd.");
        k = Math.abs(k);

        // Check sigma
        sigma = Math.abs(sigma);
        if (sigma > 15)
            sigma = 15;

        // Generate the kernel
        double[][] kernel = generate2DKernel(k, sigma);

        // Create the output array
        int n = bmp.length, m = bmp[0].length;
        int[][][] output = new int[n][m][3];

        // Perform the convolution
        int k_2 = k/2;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                double r = 0.0;
                double g = 0.0;
                double b = 0.0;
                for (int z = -k_2; z <= k_2; ++z) {
                    for (int y = -k_2; y <= k_2; ++y) {
                        if (i + z >= 0 && i + z < n && j + y >= 0 && j + y < m) {
                            r += (kernel[k_2 + z][k_2 + y] * bmp[i + z][j + y][0]);
                            g += (kernel[k_2 + z][k_2 + y] * bmp[i + z][j + y][1]);
                            b += (kernel[k_2 + z][k_2 + y] * bmp[i + z][j + y][2]);
                        }
                    }
                }
                output[i][j][0] = (int) r;
                output[i][j][1] = (int) g;
                output[i][j][2] = (int) b;
            }
        }

        return output;
    }

    private static double[][] generate2DKernel(int k, double sigma) {
        double[][] kernel_2d = new double[k][k];

        // Generate the kernel
        double sum = 0;
        int k_2 = k/2;
        for (int i = 0; i < k_2; ++i) {
            for (int j = 0; j < k_2; ++j) {
                double val = gaussian(-k_2 + i, -k_2 + j, sigma);
                kernel_2d[i][j] = val;
                kernel_2d[i][k - 1 - j] = val;
                kernel_2d[k - 1 - i][j] = val;
                kernel_2d[k - 1 - i][k - 1 - j] = val;
                sum += 4 * val;
            }
        }
        for (int i = 0; i < k_2; ++i) {
            double val = gaussian(-k_2 + i, 0, sigma);
            kernel_2d[i][k_2] = val;
            kernel_2d[k_2][i] = val;
            kernel_2d[k-1-i][k_2] = val;
            kernel_2d[k_2][k-1-i] = val;
            sum += 4 * val;
        }
        kernel_2d[k_2][k_2] = gaussian(0,0, sigma);
        sum += kernel_2d[k_2][k_2];

        // Normalize
        for (int i = 0; i < k; ++i) {
            for (int j = 0; j < k; ++j) {
                kernel_2d[i][j] /= sum;
            }
        }

        return kernel_2d;
    }

    private static double gaussian(int x, int y, double sigma) {
        double two_sigma_sqr = 2 * sigma * sigma;
        double x_sqr = x * x;
        double y_sqr = y * y;
        double exp = Math.exp(-(x_sqr + y_sqr) / two_sigma_sqr);
        return (1/(two_sigma_sqr * Math.PI)) * exp;
    }

    public static void main(String[] args) {
        // Get system properties
        Properties properties = System.getProperties();

        // Create a scanner class
        Scanner scanner = new Scanner(System.in);

        // Get the file path
        String filepath = "";
        if (properties.getProperty("os.name").equals("Linux")) {
            System.out.print("Input the desired filepath (default: bmp/lena.bmp): ");
            filepath = scanner.nextLine();
            if (filepath.isEmpty())
                filepath = "bmp/lena.bmp";
        } else { // Windows
            System.out.print("Input the desired filepath (default: bmp\\lena.bmp): ");
            filepath = scanner.nextLine();
            if (filepath.isEmpty())
                filepath = "bmp\\lena.bmp";
        }
        System.out.println();

        // Determine whether it is grey or rgb
        String color_scheme = "";
        System.out.print("Pick a color scheme\n [0] Greyscale\n [1] RGB\nChoice (default: 0): ");
        color_scheme = scanner.nextLine();
        if (!color_scheme.equals("1"))
            color_scheme = "0";
        System.out.println();

        // Fetch the options
        String option = "";
        System.out.println("Input desired effect:");
        System.out.println(" [0] Flip Horizontally");
        System.out.println(" [1] Flip Vertically");
        System.out.println(" [2] Transpose");
        System.out.println(" [3] Invert Colors");
        System.out.println(" [4] Blur");
        System.out.print("Choice (default: 0): ");
        option = scanner.nextLine();
        if (!option.equals("0") && !option.equals("1") && !option.equals("2") && !option.equals("3") && !option.equals("4"))
            option = "0";
        System.out.println();

        // Get the output file path
        String out_path = "";
        System.out.print("Input the output filepath (default: output.bmp): ");
        out_path = scanner.nextLine();
        if (out_path.isEmpty())
            out_path = "output.bmp";
        System.out.println();

        try {
            // Let's go through the options
            if (color_scheme.equals("0")) {
                // Read in the bitmap
                int[][] bmp = BitMap.ReadGreyScale(filepath);

                // Run the correct option
                switch (option) {
                    case "0":
                        FlipHorizontally(bmp);
                        break;
                    case "1":
                        FlipVertically(bmp);
                        break;
                    case "2":
                        bmp = Transpose(bmp);
                        break;
                    case "3":
                        Invert(bmp);
                        break;
                    default:
                        int k = 7;
                        double sigma = 3;
                        bmp = GaussianBlur(bmp, k, sigma);
                        break;
                }

                // Display the file
                Display.DisplayGreyscale(bmp);

                // Output the file
                BitMap.OutputGreyScale(out_path, bmp);
            } else {
                // Read in the bitmap
                int[][][] bmp = BitMap.ReadRGB(filepath);

                // Run the correct option
                switch (option) {
                    case "0":
                        FlipHorizontally(bmp);
                        break;
                    case "1":
                        FlipVertically(bmp);
                        break;
                    case "2":
                        bmp = Transpose(bmp);
                        break;
                    case "3":
                        Invert(bmp);
                        break;
                    default:
                        int k = 7;
                        double sigma = 3;
                        bmp = GaussianBlur(bmp, k, sigma);
                        break;
                }

                // Show the file
                Display.DisplayRGB(bmp);

                // Output the file
                BitMap.OutputRGB(out_path, bmp);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
