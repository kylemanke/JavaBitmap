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
    public static void GaussianBlur(int[][] bmp, int k, double sigma) throws Exception {
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
        double[] kernel = generate1DKernel(k, sigma);

        // Create the output array
        int n = bmp.length, m = bmp[0].length;
        double[][] horizontal = new double[n][m];

        // Perform the first horizontal pass
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                for (int z = -(k/2); z <= (k/2); ++z) {
                    if (j + z >= 0 && j + z < m) {
                        horizontal[i][j] += (bmp[i][j + z] * kernel[z + (k/2)]);
                    }
                }
            }
        }

        // Perform the vertical pass
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int z = -(k/2); z <= (k/2); ++z) {
                    if (i + z >= 0 && i + z < n) {
                        bmp[i][j] = 0;
                        bmp[i][j] += (int)(horizontal[i+z][j] * kernel[z + (k/2)]);
                    }
                }
            }
        }
    }

    // TODO: Fix gaussian
    public static void GaussianBlur(int[][][] bmp, int k, double sigma) throws Exception {
        // Check k
        if (k % 2 == 0)
            throw new Exception("Kernel size must be odd.");
        k = Math.abs(k);

        // Check sigma
        sigma = Math.abs(sigma);
        if (sigma > 15)
            sigma = 15;

        // Generate the kernel
        double[] kernel = generate1DKernel(k, sigma);

        // Create the horiontal array
        int n = bmp.length, m = bmp[0].length;
        double[][][] horizontal = new double[n][m][3];

        // Perform the horizontal pass
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                for (int z = -(k/2); z <= (k/2); ++z) {
                    if (j + z >= 0 && j + z < m) {
                        horizontal[i][j][0] += (bmp[i][j+z][0] * kernel[z + (k/2)]);
                        horizontal[i][j][1] += (bmp[i][j+z][1] * kernel[z + (k/2)]);
                        horizontal[i][j][2] += (bmp[i][j+z][2] * kernel[z + (k/2)]);
                    }
                }
            }
        }

        // Perform the vertical pass
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                for (int z = -(k/2); z <= (k/2); ++z) {
                    if (i + z >= 0 && i + z < n) {
                        bmp[i][j][0] = 0; bmp[i][j][1] = 0; bmp[i][j][2] = 0;
                        bmp[i][j][0] += (int)(horizontal[i+z][j][0] * kernel[z + (k/2)]);
                        bmp[i][j][1] += (int)(horizontal[i+z][j][1] * kernel[z + (k/2)]);
                        bmp[i][j][2] += (int)(horizontal[i+z][j][2] * kernel[z + (k/2)]);
                    }
                }
            }
        }

    }

    private static double[] generate1DKernel(int k, double sigma) {
        double[] kernel_1d = new double[k];

        // Generate the kernel
        double sum = 0;
        int k_2 = k/2;
        for (int i = 0; i < k_2; ++i) {
            kernel_1d[i] = gaussian(-k_2 + i, k_2, sigma);
            kernel_1d[k - 1 - i] = kernel_1d[i];
            sum += 8 * kernel_1d[i];
        }
        kernel_1d[k_2] = gaussian(0, k_2, sigma);
        sum += 4 * kernel_1d[k_2];

        // Finish summing up the kernel
        for (int i = -k_2 + 1; i < 0; ++i) {
            for (int j = -k_2 + 1; j < 0; ++j) {
                sum += 4 * gaussian(i, j, sigma);
            }
        }

        for (int i = -k_2 + 1; i < 0; ++i) {
            sum += 4 * gaussian(i, 0, sigma);
        }

        sum += gaussian(0, 0, sigma);

        // Normalize the vector
        for (int i = 0; i < k; ++i)
            kernel_1d[i] /= Math.sqrt(sum);

        return kernel_1d;
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
                        GaussianBlur(bmp, k, sigma);
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
                        double sigma = 5;
                        GaussianBlur(bmp, k, sigma);
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
