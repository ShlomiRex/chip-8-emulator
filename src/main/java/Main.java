import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    private void read_program_from_file() {

        //String rom_filename = "C:\\Users\\Shlomi\\Desktop\\Projects\\chip-8-emulator\\src\\main\\resources\\test_opcode.ch8";
//        String rom_filename = "C:\\Users\\Shlomi\\Desktop\\Projects\\chip8\\examples\\shlomi.ch8";
//        File f = new File(rom_filename);
//        FileInputStream fileInputStream = new FileInputStream(f);
//        byte[] program = new byte[1024];
        //        int bytes_read = fileInputStream.read(program);

    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);

        byte[] program = hexStringToByteArray("0x60 0x00 0x61 0x01 0x62 0x02 0x63 0x03 0x64 0x04 0x65 0x05");
        int bytes_read = program.length;

        logger.debug("Loading ROM, bytes: " + bytes_read);

        StringBuilder first_16_bytes = new StringBuilder();
        for (int i = 0; i < bytes_read; i++) {
            byte b = program[i];
            String s = String.format("0x%02X", b);
            first_16_bytes.append(s).append(" ");
        }
        logger.debug("Program first 16 bytes: " + first_16_bytes);

        Display display = new Display();
        Window window = new Window(display);
        display.setPixel(0, 0, true);
        display.setPixel(0, 1, false);
        display.setPixel(0, 2, true);
        CPU cpu = new CPU(program, display);

        cpu.tick();
        cpu.tick();
        cpu.tick();
        cpu.tick();
        cpu.tick();
        cpu.tick();
    }
}