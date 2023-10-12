import java.util.Properties;
import java.util.Scanner;

public class ImageProcessor {
    public static void FlipHorizontally(int[][] bmp) {
        // TODO: Write code to flip the bmp matrix horizontally
        System.out.println("This function has not been implemented yet.");
    }

    public static void FlipHorizontally(int[][][] bmp) {
        // TODO: Write code to flip the bmp matrix horizontally
        System.out.println("This function has not been implemented yet.");
    }

    public static void FlipVertically(int[][] bmp) {
        // TODO: Write code to flip the bmp matrix vertically
        System.out.println("This function has not been implemented yet.");
    }

    public static void FlipVertically(int[][][] bmp) {
        // TODO: Write code to flip the bmp matrix vertically
        System.out.println("This function has not been implemented yet.");
    }

    public static void Transpose(int[][] bmp) {
        // TODO: Write code to transpose the matrix
        System.out.println("This function has not been implemented yet.");
    }

    public static void Transpose(int[][][] bmp) {
        // TODO: Write code to transpose the matrix
        System.out.println("This function has not been implemented yet.");
    }

    public static void Invert(int[][] bmp) {
        // TODO: Write code to invert the matrix
        System.out.println("This function has not been implemented yet.");
    }

    public static void Invert(int[][][] bmp) {
        // TODO: Write code to invert the matrix
        System.out.println("This function has not been implemented yet.");
    }

    public static void GaussianBlur(int[][] bmp, int k, double sigma) {
        // TODO
        System.out.println("This function has not been implemented yet.");
    }

    public static void GaussianBlur(int[][][] bmp, int k, double sigma) {
        // TODO
        System.out.println("This function has not been implemented yet.");
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
                        Transpose(bmp);
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
                        Transpose(bmp);
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
