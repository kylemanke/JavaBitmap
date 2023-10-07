public class ImageProcessor {
    public static void main(String[] argc) {
        int[][][] bmp = null;

        try {
            bmp = BitMap.ReadRGB("bmp/lena.bmp");
            Display.DisplayRGB(bmp);
            BitMap.OutputRGB("bmp/test.bmp", bmp);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
