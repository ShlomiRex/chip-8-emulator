import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HexFormat;

public class Main {
    public static byte[] hexStringToByteArray(String s) {
        StringBuilder new_s = new StringBuilder();
        for (int i = 0; i < s.length(); i+=5) {
            new_s.append(s.charAt(i + 2));
            new_s.append(s.charAt(i + 3));
        }
        return HexFormat.of().parseHex(new_s.toString());
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Logger logger = LoggerFactory.getLogger(Main.class);

        // Read from ROM file
        String ch8Program = "test_rom/test_opcode.ch8";
        //String ch8Program = "other_roms/IBM Logo.ch8";
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(ch8Program);
        byte[] program = new byte[4096];
        int bytes_read = inputStream.read(program);

        // Custom programs

        // Letter E at location 1,0
        //byte[] program = hexStringToByteArray("0x12 0x09 0xF8 0x80 0x80 0xF8 0x80 0x80 0xF8 0x60 0x01 0x61 0x00 0xA2 0x02 0xD0 0x17");

        // Stickman that is moveable
        //byte[] program = hexStringToByteArray("0xC1 0x1F 0xC2 0x0F 0xA2 0x30 0xD1 0x28 0xD1 0x28 0x60 0x05 0xE0 0xA1 0x72 0xFF 0x60 0x08 0xE0 0xA1 0x72 0x01 0x60 0x07 0xE0 0xA1 0x71 0xFF 0x60 0x09 0xE0 0xA1 0x71 0x01 0xD1 0x28 0xFF 0x07 0x3F 0x00 0x12 0x24 0x6F 0x03 0xFF 0x15 0x12 0x08 0x70 0x70 0x20 0x70 0xA8 0x20 0x50 0x50");

        // Moving letter E across the columns
        //byte[] program = hexStringToByteArray("0x12 0x09 0xF8 0x80 0x80 0xF8 0x80 0x80 0xF8 0xA2 0x02 0x60 0x00 0x61 0x00 0xD0 0x17 0xD0 0x17 0x70 0x01 0xD0 0x17 0xFF 0x07 0x3F 0x00 0x12 0x17 0x6F 0x0F 0xFF 0x15 0x12 0x11");

        //int bytes_read = program.length;

        logger.debug("Loading ROM, bytes: " + bytes_read);

        StringBuilder program_bytes_str = new StringBuilder();
        for (int i = 0; i < bytes_read; i+=2) {
            byte msb = program[i];
            byte lsb = program[i+1];

            program_bytes_str
                    .append("0x")
                    .append(String.format("%02X", msb))
                    .append(String.format("%02X", lsb))
                    .append(" ");
        }
        logger.debug("Program bytes: " + program_bytes_str);

        Display display = new Display();

        //TODO: Do something here, don't instantiate Window like this.
        final Window[] window = new Window[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                window[0] = new Window(display);
            }
        });
        thread.start();
        thread.join();

        CPU cpu = new CPU(program, bytes_read, display, window[0]);

        while (true) {
            Thread.sleep(10);
            cpu.tick();
        }
    }
}