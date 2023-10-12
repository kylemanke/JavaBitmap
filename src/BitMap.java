// TODO: Maybe add support for RLE, currently does not support compression
// Import necessary File Library
import com.sun.net.httpserver.Headers;

import java.io.*;
import java.lang.Math;

public class BitMap {
    private static int BUFFER_SIZE = 2048;
    private static int HEADER_SIZE = 54;

    public static int[][] ReadGreyScale(String file_name) throws Exception {
        // Open the file
        int[][] bmp = null;
        try (FileInputStream in = new FileInputStream(file_name)) {
            // Read in the header
            BitMapHeader header = new BitMapHeader(in);

            // Read in the image as greyscale
            // TODO: Maybe add support for RLE
            if (header.bits_per_pixel_ == 24) {
                bmp = Read24Bit2Grey(in, header);
            } else if (header.bits_per_pixel_ == 8) {
                bmp = Read8Bit2Grey(in, header);
            } else if (header.bits_per_pixel_ == 4) {
                bmp = Read4Bit2Grey(in, header);
            } else if (header.bits_per_pixel_ == 1) {
                bmp = Read1Bit2Grey(in, header);
            } else {
                throw new Exception("Unsupported bits per pixel format");
            }
        } catch (FileNotFoundException e) {
            throw new Exception("Failed to open file: " + file_name);
        } catch (SecurityException e) {
            throw new Exception("Access restricted to file: " + file_name);
        }
        return bmp;
    }

    public static int[][][] ReadRGB(String file_name) throws Exception {
        // open the file
        int[][][] bmp = null;
        try (FileInputStream in = new FileInputStream(file_name)) {
            // Read in the header
            BitMapHeader header = new BitMapHeader(in);

            // Read in the image
            if (header.bits_per_pixel_ == 24) {
                bmp = Read24Bit2RGB(in, header);
            } else if (header.bits_per_pixel_ == 8) {
                bmp = Read8Bit2RGB(in, header);
            } else if (header.bits_per_pixel_ == 4) {
                bmp = Read4Bit2RGB(in, header);
            } else if (header.bits_per_pixel_ == 1) {
                bmp = Read1Bit2RGB(in, header);
            } else {
                throw new Exception("Unsupported bits per pixel format");
            }
        } catch (FileNotFoundException e) {
            throw new Exception("Failed to open file: " + file_name);
        } catch (SecurityException e) {
            throw new Exception("Access restricted to file: " + file_name);
        }
        return bmp;
    }

    //*********************
    // Following functions are used to output the actual bmp file
    //*********************
    public static void OutputGreyScale(String file_name, int[][] bmp) throws Exception {
        // Open the file
        int height = bmp.length;
        int width = bmp[0].length;
        int padding = (4 - width % 4) % 4;
        try (FileOutputStream out = new FileOutputStream(file_name, false)) {
            // Output the header
            OutputGreyHeader(out, height, width);

            // Output the bmp file
            // Buffer outputs for performance
            int idx = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int i = height - 1; i >= 0; --i) {
                for (int j = 0; j < width; ++j) {
                    if (idx == BUFFER_SIZE) {
                        out.write(buffer, 0, idx);
                        idx = 0;
                    }
                    buffer[idx++] = (byte)bmp[i][j];
                }
                // Put padding if necessary
                for (int j = 0; j < padding; ++j) {
                    if (idx == BUFFER_SIZE) {
                        out.write(buffer, 0, idx);
                        idx = 0;
                    }
                    buffer[idx++] = 0;
                }
            }
            out.write(buffer, 0, idx);
        } catch (FileNotFoundException e) {
            throw new Exception("Failed to open file: " + file_name);
        } catch (SecurityException e) {
            throw new Exception("Access restricted to file: " + file_name);
        }
    }

    public static void OutputRGB(String file_name, int[][][] bmp) throws Exception {
        // Open the file
        int height = bmp.length;
        int width = bmp[0].length;
        int padding = (4 - (width * 3) % 4) % 4;
        try (FileOutputStream out = new FileOutputStream(file_name, false)) {
            // Output the header
            OutputRGBHeader(out, height, width);

            // Output the bmp file
            // Using a buffer
            int idx = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int i = height - 1; i >= 0; --i) {
                for (int j = 0; j < width; ++j) {
                    for (int k = 2; k >= 0; --k) {
                        if (idx >= BUFFER_SIZE) {
                            out.write(buffer, 0, idx);
                            idx = 0;
                        }
                        buffer[idx++] = (byte)bmp[i][j][k];
                    }
                }
                // Output padding
                for (int j = 0; j < padding; ++j) {
                    if (idx >= BUFFER_SIZE) {
                        out.write(buffer, 0, idx);
                        idx = 0;
                    }
                    buffer[idx++] = 0;
                }
            }
            out.write(buffer, 0, idx);
        } catch (FileNotFoundException e) {
            throw new Exception("Failed to open file: " + file_name);
        } catch (SecurityException e) {
            throw new Exception("Access restricted to file: " + file_name);
        }
    }

    //*********************
    // Following functions are used to output necessary header for bmp file
    //*********************
    private static void OutputRGBHeader(FileOutputStream out, int height, int width) throws Exception {
        try {
            // Create the header
            byte[] header = new byte[14];

            // Calculate file size
            int padding = (4 - (width * 3) % 4) % 4;
            int file_size = HEADER_SIZE + height * width * 3 + padding * height;
            int data_offset = HEADER_SIZE;
            int image_size = height * width * 3 + padding * height;

            header[0] = 0x42;
            header[1] = 0x4D;
            header[2] = (byte) file_size;
            header[3] = (byte) (file_size >> 8);
            header[4] = (byte) (file_size >> 16);
            header[5] = (byte) (file_size >> 24);
            header[10] = (byte) data_offset;
            header[11] = (byte) (data_offset >> 8);
            header[12] = (byte) (data_offset >> 16);
            header[13] = (byte) (data_offset >> 24);

            // Output the header
            out.write(header);

            // Create the InfoHeader
            byte[] info_header = new byte[40];

            info_header[0] = 0x28;
            info_header[4] = (byte)width;
            info_header[5] = (byte)(width >> 8);
            info_header[6] = (byte)(width >> 16);
            info_header[7] = (byte)(width >> 24);
            info_header[8] = (byte)height;
            info_header[9] = (byte)(height >> 8);
            info_header[10] = (byte)(height >> 16);
            info_header[11] = (byte)(height >> 24);
            info_header[12] = 0x01;
            info_header[14] = 0x18;
            info_header[20] = (byte)(image_size);
            info_header[21] = (byte)(image_size >> 8);
            info_header[22] = (byte)(image_size >> 16);
            info_header[23] = (byte)(image_size >> 24);

            // Output the header
            out.write(info_header);
        } catch (IOException e) {
            throw new Exception("Failed to write to the file");
        }
    }

    private static void OutputGreyHeader(FileOutputStream out, int height, int width) throws Exception {
        try {
            // Create the header
            byte[] header = new byte[14];

            // Calculate file size
            int padding = (4 - width % 4) % 4;
            int file_size = HEADER_SIZE + (256 * 4) + (height * (width + padding));
            int data_offset = HEADER_SIZE + 256 * 4;
            int image_size = height * (width + padding);

            header[0] = 0x42;
            header[1] = 0x4D;
            header[2] = (byte) file_size;
            header[3] = (byte) (file_size >> 8);
            header[4] = (byte) (file_size >> 16);
            header[5] = (byte) (file_size >> 24);
            header[10] = (byte) data_offset;
            header[11] = (byte) (data_offset >> 8);
            header[12] = (byte) (data_offset >> 16);
            header[13] = (byte) (data_offset >> 24);

            // Output the header
            out.write(header);

            // Create the InfoHeader
            byte[] info_header = new byte[40];

            info_header[0] = 0x28;
            info_header[4] = (byte)width;
            info_header[5] = (byte)(width >> 8);
            info_header[6] = (byte)(width >> 16);
            info_header[7] = (byte)(width >> 24);
            info_header[8] = (byte)height;
            info_header[9] = (byte)(height >> 8);
            info_header[10] = (byte)(height >> 16);
            info_header[11] = (byte)(height >> 24);
            info_header[12] = 0x01;
            info_header[14] = 0x08;
            info_header[20] = (byte)(image_size);
            info_header[21] = (byte)(image_size >> 8);
            info_header[22] = (byte)(image_size >> 16);
            info_header[23] = (byte)(image_size >> 24);
            info_header[33] = 0x01;

            // Output the info header
            out.write(info_header);

            // Create the color table
            byte[] color_table = new byte[1024];
            for (int i = 0; i < 256; ++i) {
                color_table[i * 4] = (byte)i;
                color_table[i * 4 + 1] = (byte)i;
                color_table[i * 4 + 2] = (byte)i;
            }

            // Write the color table
            out.write(color_table);
        } catch (IOException e) {
            throw new Exception("Failed to write to the file");
        }
    }

    //*********************
    // Following functions are used to read BMP files into an RGB format
    //*********************
    private static int[][][] Read24Bit2RGB(FileInputStream in, BitMapHeader header) throws Exception {
        // Initialize array
        int[][][] bmp = new int[header.bmp_height_][header.bmp_width_][3];

        // Get padding
        int padding = (4 - ((header.bmp_width_ * 3) % 4)) % 4;

        // Declare the buffer
        byte[] buffer = new byte[BUFFER_SIZE];

        // Read in the bitmap
        try {
            int bytes_read = 0;
            int idx = 0;
            for (int i = header.bmp_height_-1; i >= 0; --i) {
                for (int j = 0; j < header.bmp_width_; ++j) {
                    for (int k = 2; k >= 0; --k) {
                        if (idx >= bytes_read) {
                            idx = 0;
                            bytes_read = in.read(buffer);
                            if (bytes_read == 0) {
                                throw new Exception("Failed to read enough bytes");
                            }
                        }
                        bmp[i][j][k] = (byte)buffer[idx++];
                    }
                }
                // Skip padding
                idx += padding;
            }
        } catch (IOException e) {
            throw new Exception("Failed to read in bitmap");
        }
        return bmp;
    }

    private static int[][][] Read8Bit2RGB(FileInputStream in, BitMapHeader header) throws Exception {
        // Check header for encoding
        if (header.compression_ != 0)
            throw new Exception("File is encoded, this is unsupported.");

        // Initialize array
        int[][][] bmp = new int[header.bmp_height_][header.bmp_width_][3];

        // Get padding
        int padding = (4 - (header.bmp_width_ % 4)) % 4;

        // Declare the buffer
        byte[] buffer = new byte[BUFFER_SIZE];

        // Read in the bitmap
        try {
            int bytes_read = 0;
            int idx = 0;
            for (int i = header.bmp_height_-1; i >= 0; --i) {
                for (int j = 0; j < header.bmp_width_; ++j) {
                    if (idx >= bytes_read) {
                        idx = 0;
                        bytes_read = in.read(buffer);
                        if (bytes_read == 0)
                            throw new Exception("Failed to read in enough bytes from bitmap");
                    }
                    bmp[i][j][0] = header.color_table_[buffer[idx] & 0xFF][2];
                    bmp[i][j][1] = header.color_table_[buffer[idx] & 0xFF][1];
                    bmp[i][j][2] = header.color_table_[buffer[idx] & 0xFF][0];
                    ++idx;
                }
                // Skip padding
                idx += padding;
            }
        } catch (IOException e) {
            throw new Exception("Failed to read in bitmap");
        }
        return bmp;
    }

    private static int[][][] Read4Bit2RGB(FileInputStream in, BitMapHeader header) throws Exception {
        // Check header for encoding
        if (header.compression_ != 0)
            throw new Exception("File is encoded, this is unsupported.");

        // Initialize array
        int[][][] bmp = new int[header.bmp_height_][header.bmp_width_][3];

        // Get padding
        int padding = (4 - ((header.bmp_width_ / 2) % 4)) % 4;

        // Declare the buffer
        byte[] buffer = new byte[BUFFER_SIZE];

        // Read in the bitmap
        try {
            int bytes_read = 0;
            int idx = 0;
            for (int i = header.bmp_height_-1; i >= 0; --i) {
                for (int j = 0; j < header.bmp_width_; j += 2) {
                    if (idx >= bytes_read) {
                        idx = 0;
                        bytes_read = in.read(buffer);
                        if (bytes_read == 0)
                            throw new Exception("Failed to read in enough bytes from bitmap");
                    }
                    int color_idx = (buffer[idx] & 0xFF) >> 4;
                    bmp[i][j][0] = header.color_table_[buffer[color_idx] & 0xFF][2];
                    bmp[i][j][1] = header.color_table_[buffer[color_idx] & 0xFF][1];
                    bmp[i][j][2] = header.color_table_[buffer[color_idx] & 0xFF][0];

                    if (j+1 >= header.bmp_width_) continue;

                    color_idx = buffer[idx++] & 0xF;
                    bmp[i][j+1][0] = header.color_table_[buffer[color_idx] & 0xFF][2];
                    bmp[i][j+1][1] = header.color_table_[buffer[color_idx] & 0xFF][1];
                    bmp[i][j+1][2] = header.color_table_[buffer[color_idx] & 0xFF][0];
                }
                // Skip padding
                idx += padding;
            }
        } catch (IOException e) {
            throw new Exception("Failed to read in bitmap");
        }
        return bmp;
    }

    private static int[][][] Read1Bit2RGB(FileInputStream in, BitMapHeader header) throws Exception {
        // Initialize array
        int[][][] bmp = new int[header.bmp_height_][header.bmp_width_][3];

        // Get padding
        int padding = (4 - ((header.bmp_width_ / 8) % 4)) % 4;

        // Declare the buffer
        byte[] buffer = new byte[BUFFER_SIZE];

        // Read in the bitmap
        try {
            int bytes_read = 0;
            int idx = 0;
            for (int i = header.bmp_height_-1; i >= 0; --i) {
                for (int j = 0; j < header.bmp_width_; j += 8) {
                    if (idx >= bytes_read) {
                        idx = 0;
                        bytes_read = in.read(buffer);
                        if (bytes_read == 0)
                            throw new Exception("Failed to read in enough bytes from bitmap");
                    }
                    for (int k = 0; k < 8; ++k) {
                        if (j + k >= header.bmp_width_) break;
                        int color_idx = (buffer[idx] & (1 << (7-k))) >> (7-k);
                        bmp[i][j+k][0] = header.color_table_[buffer[color_idx] & 0xFF][2];
                        bmp[i][j+k][1] = header.color_table_[buffer[color_idx] & 0xFF][1];
                        bmp[i][j+k][2] = header.color_table_[buffer[color_idx] & 0xFF][0];
                    }
                }
                // Skip padding
                idx += padding;
            }
        } catch (IOException e) {
            throw new Exception("Failed to read in bitmap");
        }
        return bmp;
    }

    //*********************
    // Following functions are used to read BMP files into a Greyscale format
    //*********************
    private static int RGB2Grey(int r, int g, int b) {
        return (int)(0.299 * r + 0.587 * g + 0.114 * b);
    }

    private static int[][] Read24Bit2Grey(FileInputStream in, BitMapHeader header) throws Exception {
        // Initialize the array
        int[][] bmp = new int[header.bmp_height_][header.bmp_width_];

        // Get padding
        int padding = (4 - ((header.bmp_width_ * 3) % 4)) % 4;

        // Declare the buffer
        byte[] buffer = new byte[BUFFER_SIZE];

        // Read in the buffer
        try {
            int bytes_read = 0;
            int idx = 0;
            int[] bgr = new int[3];
            for (int i = 0; i < header.bmp_height_; ++i) {
                for (int j = 0; j < header.bmp_width_; ++j) {
                    for (int k = 0; k < 3; ++k) {
                        // Populate buffer if needed
                        if (idx >= bytes_read) {
                            idx = 0;
                            bytes_read = in.read(buffer);
                            if (bytes_read == 0)
                                throw new Exception("Failed to read in enough bytes from bitmap");
                        }
                        bgr[k] = (buffer[idx++] & 0xFF);
                    }
                    bmp[header.bmp_height_-1-i][j] = RGB2Grey(bgr[2], bgr[1], bgr[0]);
                }
                // Skip padding
                idx += padding;
            }
        } catch (IOException e) {
            throw new Exception("Failed to read in bitmap");
        }
        return bmp;
    }

    private static int[][] Read8Bit2Grey(FileInputStream in, BitMapHeader header) throws Exception {
        // Check header for encoding
        if (header.compression_ != 0)
                throw new Exception("File is encoded, this is unsupported.");

        // Initialize the array
        int[][] bmp = new int[header.bmp_height_][header.bmp_width_];

        // Get padding
        int padding = (4 - (header.bmp_width_ % 4)) % 4;

        // Declare the buffer
        byte[] buffer = new byte[BUFFER_SIZE];

        // Read in the buffer
        try {
            int bytes_read = 0;
            int idx = 0;
            for (int i = header.bmp_height_ - 1; i >= 0; --i) {
                for (int j = 0; j < header.bmp_width_; ++j) {
                    if (idx >= bytes_read) {
                        idx = 0;
                        bytes_read = in.read(buffer);
                        if (bytes_read == 0)
                            throw new Exception("Failed to read in enough bytes from bitmap");
                    }
                    bmp[i][j] = RGB2Grey(header.color_table_[buffer[idx] & 0xFF][2], header.color_table_[buffer[idx] & 0xFF][1], header.color_table_[buffer[idx] & 0xFF][0]);
                    idx++;
                }
                // Skip padding
                idx += padding;
            }
        } catch (IOException e) {
            throw new Exception("Failed to read in bitmap");
        }
        return bmp;
    }

    private static int[][] Read4Bit2Grey(FileInputStream in, BitMapHeader header) throws Exception {
        // Check header for encoding
        if (header.compression_ != 0)
                throw new Exception("File is encoded, this is unsupported");

        // Initialize the array
        int[][] bmp = new int[header.bmp_height_][header.bmp_width_];

        int padding = (4 - ((header.bmp_width_ / 2) % 4)) % 4;

        // Declare the buffer
        byte[] buffer = new byte[BUFFER_SIZE];

        // Read in the buffer
        try {
            int bytes_read = 0;
            int idx = 0;
            for (int i = header.bmp_height_ - 1; i >= 0; --i) {
                for (int j = 0; j < header.bmp_width_; ++j) {
                    if (idx >= bytes_read * 2) {
                        idx = 0;
                        bytes_read = in.read(buffer);
                        if (bytes_read == 0) {
                            throw new Exception("Failed to read in enough bytes");
                        }
                    }
                    int color_idx = 0;
                    if (idx % 2 == 0) {
                        color_idx = (buffer[idx/2] & 0xFF) >> 4;
                    } else {
                        color_idx = buffer[idx/2] & 0xF;
                    }
                    bmp[i][j] = RGB2Grey(header.color_table_[color_idx][2], header.color_table_[color_idx][1], header.color_table_[color_idx][0]);
                    ++idx;
                }
                // Skip padding
                idx += (padding * 2);
            }
        } catch (IOException e) {
            throw new Exception("Failed to read in bitmap");
        }
        return bmp;
    }

    private static int[][] Read1Bit2Grey(FileInputStream in, BitMapHeader header) throws Exception {
        // Initialize the array
        int[][] bmp = new int[header.bmp_height_][header.bmp_width_];

        // Calc padding
        int padding = (4 - (header.bmp_width_ / 8) % 4) % 4;

        // Initialize the buffer
        byte[] buffer = new byte[BUFFER_SIZE];

        // Read in the file
        try {
            int bytes_read = 0;
            int idx = 0;
            for (int i = header.bmp_height_-1; i >= 0; --i) {
                for (int j = 0; j < header.bmp_width_; ++j) {
                    if (idx >= bytes_read * 8) {
                        idx = 0;
                        bytes_read = in.read(buffer);
                        if (bytes_read == 0)
                            throw new Exception("Failed to read in enough bytes");
                    }
                    int bit_idx = 7 - (idx % 8);
                    int color_idx = (buffer[idx/8] & (0x1 << bit_idx)) >> (bit_idx);
                    bmp[i][j] = RGB2Grey(header.color_table_[color_idx][2], header.color_table_[color_idx][1], header.color_table_[color_idx][0]);
                    ++idx;
                }
                // Skip padding
                idx += (padding * 8);
            }
        } catch (IOException e) {
            throw new Exception("Failed to read in the bitmap");
        }
        return bmp;
    }
}

// Class describing the header field of a bitmap image
// More info can be found here if curious: http://www.ece.ualberta.ca/~elliott/ee552/studentAppNotes/2003_w/misc/bmp_file_format/bmp_file_format.htm
class BitMapHeader {
    // Class constants
    public static int HEADER_SIZE = 14;
    public static int INFO_HEADER_SIZE = 40;

    // Header fields
    public int file_size_;
    public int data_offset_;

    // Info Header fields
    public int info_header_size_;
    public int bmp_width_;
    public int bmp_height_;
    public char planes_;
    public char bits_per_pixel_;
    public int compression_;
    public int image_size_;
    public int x_pixels_per_m_;
    public int y_pixels_per_m_;
    public int colors_used_;
    public int important_colors_;

    // Color Table Field
    public byte[][] color_table_;

    // Constructor
    // Must remember bitmap is in little endian format
    public BitMapHeader(FileInputStream in) throws Exception {
        try {
            // Read in the header
            byte[] header = new byte[HEADER_SIZE];
            Counter offset = new Counter();
            if (in.read(header) != HEADER_SIZE)
                throw new Exception("Failed to read in 14 bytes for the header");

            // Check signature
            if (header[0] != 0x42 && header[1] != 0x4D)
                throw new Exception("Signature is malformed");
            offset.set(2);

            // Read in file size
            file_size_ = ReadIntLE(header, offset);


            // Skip reserved 4 bytes
            offset.set(offset.value() + 4);

            // Read in data offset
            data_offset_ = ReadIntLE(header, offset);

            // Read in the InfoHeader
            byte[] info_header = new byte[INFO_HEADER_SIZE];
            offset.reset();
            if (in.read(info_header) != INFO_HEADER_SIZE)
                throw new Exception("Failed to read in InfoHeader");

            // InfoHeader
            info_header_size_ = ReadIntLE(info_header, offset);
            bmp_width_ = ReadIntLE(info_header, offset);
            bmp_height_ = ReadIntLE(info_header, offset);
            planes_ = ReadCharLE(info_header, offset);
            bits_per_pixel_ = ReadCharLE(info_header, offset);
            compression_ = ReadIntLE(info_header, offset);
            image_size_ = ReadIntLE(info_header, offset);
            x_pixels_per_m_ = ReadIntLE(info_header, offset);
            y_pixels_per_m_ = ReadIntLE(info_header, offset);
            colors_used_ = ReadIntLE(info_header, offset);
            important_colors_ = ReadIntLE(info_header, offset);

            // Read in the Color Table
            // Goes blue green red
            int num_entries = 0;
            if (bits_per_pixel_ <= 8) {
                num_entries = (int)Math.pow(2, bits_per_pixel_);
            } else if (colors_used_ > 0) {
                num_entries = colors_used_;
            }
            if (num_entries == 0) {
                color_table_ = null;
                return;
            }

            offset.reset();
            byte[] color_table_buffer = new byte[num_entries * 4];
            color_table_ = new byte[num_entries][3];
            if (in.read(color_table_buffer) < num_entries)
                throw new Exception("Failed to read in color palette");

            for (int i = 0; i < num_entries; ++i) {
                color_table_[i][0] = color_table_buffer[offset.inc()]; // Blue
                color_table_[i][1] = color_table_buffer[offset.inc()]; // Green
                color_table_[i][2] = color_table_buffer[offset.inc()]; // Red
                offset.inc(); // Blank
            }
        } catch (IOException e) {
            throw new Exception("Failed to parse bitmap header");
        }
    }

    int ReadIntLE(byte[] buffer, Counter offset) {
        int ret = (buffer[offset.inc()] & 0xFF);
        ret |= (buffer[offset.inc()] & 0xFF) << 8;
        ret |= (buffer[offset.inc()] & 0xFF) << 16;
        ret |= (buffer[offset.inc()] & 0xFF) << 24;
        return ret;
    }

    char ReadCharLE(byte[] buffer, Counter offset) {
        char ret = (char)(buffer[offset.inc()] & 0xFF);
        ret |= (char) ((buffer[offset.inc()] & 0xFF) << 8);
        return ret;
    }
}

// Create a mutable counter
class Counter {
    private int val_;
    public Counter() { val_ = 0; }
    public int value() { return val_; }
    public void set(int val) { val_ = val; }
    public void reset() { val_ = 0; }
    public int inc() { return val_++; }
    public int dec() { return val_--; }
}
