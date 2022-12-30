import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Logger logger = LoggerFactory.getLogger(Main.class);

        String rom_filename = "C:\\Users\\Shlomi\\Desktop\\Projects\\chip-8-emulator\\src\\main\\resources\\test_opcode.ch8";
        File f = new File(rom_filename);
        FileInputStream fileInputStream = new FileInputStream(f);
        byte[] program = new byte[1024];
        int bytes_read = fileInputStream.read(program);

        logger.debug("Loading ROM, bytes: " + bytes_read);

        CPU cpu = new CPU(program);

        cpu.tick();
        cpu.tick();
        cpu.tick();
    }
}