import jdk.jshell.spi.ExecutionControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Random;

public class CPU {
    /**
     * Registers
     */
    private final byte[] registers = new byte[16];  // General purpose registers
    private short I;                                // Index Register
    private short PC;                               // Program Counter
    private byte SP;                                // Stack Pointer
    // TODO: Read about VF register
    private short VF;                               // Variable Flag register

    /**
     * Timers
     */
    private byte delay_timer;
    private byte sound_timer;

    /**
     * RAM
     */
    private final byte[] RAM = new byte[4096];

    /**
     * Stack
     */
    private final short[] stack = new short[16];

    private static Logger logger = LoggerFactory.getLogger(CPU.class);

    // Connected display, CPU will manipulate pixels.
    private Display display;

    private Random random;

    private boolean[] keypad = new boolean[16];

    /**
     * Creates new Chip-8 CPU.
     * Address 0x200 is start of the program in memory.
     */
    public CPU(byte[] rom_program, Display display) {
        this.SP = 0;
        this.PC = 0x200;
        this.display = display;

        // Start with known seed.
        long seed = 123;
        this.random = new Random(seed);

        // Load program
        for (int i = 0; i < rom_program.length; i++)
            this.RAM[0x200 + i] = rom_program[i];

        // Load font palette
        for (int i = 0; i < Font.font_palette.length; i++)
            this.RAM[0x50 + i] = Font.font_palette[i];
    }

    /**
     * Single clock cycle.
     */
    public void tick() {
        logger.debug("Tick");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Arrays.toString(this.registers)).append("\t");
        stringBuilder.append("PC: ").append(String.format("0x%02X", this.PC)).append("\t");
        stringBuilder.append("SP: ").append(String.format("0x%02X", this.SP));
        logger.debug(stringBuilder.toString());

        // Fetch next instruction
        short opcode = fetch_instruction();

        // Decode instruction (for printing only)
        Instruction i = Decoder.decodeInstruction(opcode);
        logger.debug(String.format("0x%04X", opcode) + ": " + i);

        // Execute instruction
        execute_instruction(opcode);

        this.PC += 2;
    }

    /**
     * Fetches the instruction OPCODE at the current PC.
     * @return
     */
    private short fetch_instruction() {
        byte msb = this.RAM[this.PC];
        byte lsb = this.RAM[this.PC+1];
        return (short) ((msb << 8) + lsb);
    }

    private void execute_instruction(short opcode) {
        byte x = Decoder.get_nibble(opcode, 3);
        Instruction.Operand vx = Decoder.decodeOperand(x);

        byte y = Decoder.get_nibble(opcode, 2);
        Instruction.Operand vy = Decoder.decodeOperand(y);

        byte kk = Decoder.get_lsb(opcode);
        short nnn = (short) (opcode & 0x0FFF);
        byte nn = Decoder.get_lsb(opcode);
        short lsb = Decoder.get_lsb(opcode);
        byte first_nibble = Decoder.get_nibble(opcode, 1);

        if (opcode < 0x1000) {
            if (opcode == 0x00E0) {
                // 00E0 - CLS
                // Clear the display.
                this.display.cls();
            } else if (opcode == 0x00EE) {
                // 00EE - RET
                // Return from a subroutine.
                // The interpreter sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
                SP -= 1;
                PC = stack[SP];
            } else {
                // 0nnn - SYS addr
                // Jump to a machine code routine at nnn.
                // This instruction is only used on the old computers on which Chip-8 was originally implemented. It is ignored by modern interpreters.
                // TODO: 03-Jan-23 Complete
                throw new RuntimeException();
            }
        } else if (opcode < 0x2000) {
            // 1nnn - JP addr
            // Jump to location nnn.
            // The interpreter sets the program counter to nnn.
            PC = nnn;
        } else if (opcode < 0x3000) {
            // 2nnn - CALL addr
            // Call subroutine at nnn.
            // The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
            stack[SP] = PC;
            SP += 1;
            PC = nnn;
        } else if (opcode < 0x4000) {
            // 3xkk - SE Vx, byte
            // Skip next instruction if Vx = kk.
            // The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2.
            if (registers[x] == kk)
                PC += 2;
        } else if (opcode < 0x5000) {
            // 4xkk - SNE Vx, byte
            // Skip next instruction if Vx != kk.
            // The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
            if (registers[x] != kk)
                PC += 2;
        } else if (opcode < 0x6000) {
            // 5xy0 - SE Vx, Vy
            // Skip next instruction if Vx = Vy.
            // The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
            if (registers[x] == registers[y])
                PC += 2;
        } else if (opcode < 0x7000) {
            // 6xkk - LD Vx, byte
            // Set Vx = kk.
            // The interpreter puts the value kk into register Vx.
            registers[x] = kk;
        } else if (opcode < 0x8000) {
            // 7xkk - ADD Vx, byte
            // Set Vx = Vx + kk.
            // Adds the value kk to the value of register Vx, then stores the result in Vx.
            registers[x] += kk;
        } else if (opcode < 0x9000) {
            switch (lsb) {
                case 0:
                    // 8xy0 - LD Vx, Vy
                    // Set Vx = Vy.
                    // Stores the value of register Vy in register Vx.
                    registers[x] = registers[y];
                    break;
                case 1:
                    // 8xy1 - OR Vx, Vy
                    // Set Vx = Vx OR Vy.
                    // Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx. A bitwise OR compares the corrseponding bits from two values, and if either bit is 1, then the same bit in the result is also 1. Otherwise, it is 0.
                    registers[x] |= registers[y];
                    break;
                case 2:
                    // 8xy2 - AND Vx, Vy
                    // Set Vx = Vx AND Vy.
                    // Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx. A bitwise AND compares the corrseponding bits from two values, and if both bits are 1, then the same bit in the result is also 1. Otherwise, it is 0.
                    registers[x] &= registers[y];
                    break;
                case 3:
                    // 8xy3 - XOR Vx, Vy
                    // Set Vx = Vx XOR Vy.
                    // Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx. An exclusive OR compares the corrseponding bits from two values, and if the bits are not both the same, then the corresponding bit in the result is set to 1. Otherwise, it is 0.
                    registers[x] ^= registers[y];
                    break;
                case 4:
                    // 8xy4 - ADD Vx, Vy
                    // Set Vx = Vx + Vy, set VF = carry.
                    // The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1, otherwise 0. Only the lowest 8 bits of the result are kept, and stored in Vx.
                    int sum = registers[x] + registers[y];
                    if (sum > 255)
                        registers[0xF] = 1;
                    else
                        registers[0xF] = 0;
                    registers[x] = (byte) (sum & 0xFF);
                    break;
                case 5:
                    // 8xy5 - SUB Vx, Vy
                    // Set Vx = Vx - Vy, set VF = NOT borrow.
                    // If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
                    if (registers[x] > registers[y])
                        registers[0xF] = 1;
                    else
                        registers[0xF] = 0;
                    registers[x] -= registers[y];
                    break;
                case 6:
                    // 8xy6 - SHR Vx {, Vy}
                    // Set Vx = Vx SHR 1.
                    // If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0. Then Vx is divided by 2.
                    registers[0xF] = (byte) (registers[x] & 0x1);
                    registers[x] >>= 1;
                    break;
                case 7:
                    // 8xy7 - SUBN Vx, Vy
                    // Set Vx = Vy - Vx, set VF = NOT borrow.
                    // If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.
                    if (registers[y] > registers[x])
                        registers[0xF] = 1;
                    else
                        registers[0xF] = 0;
                    registers[x] = (byte) (registers[y] - registers[x]);
                    break;
                case 0xE:
                    // 8xyE - SHL Vx {, Vy}
                    // Set Vx = Vx SHL 1.
                    // If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
                    registers[0xF] = (byte) ((registers[x] & 0x80) >> 7);
                    registers[x] <<= 1;
                    break;
                default:
                    throw new IllegalArgumentException("Impossible instruction, first octet not in range 0-7 and not equal to E. First octet: " + lsb);
            };
        } else if (opcode < 0xA000) {
            // 9xy0 - SNE Vx, Vy
            // Skip next instruction if Vx != Vy.
            // The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2.
            if (registers[x] != registers[y])
                PC += 2;
        } else if (opcode < 0xB000) {
            // Annn - LD I, addr
            // Set I = nnn.
            // The value of register I is set to nnn.
            I = nnn;
        } else if (opcode < 0xC000) {
            // Bnnn - JP V0, addr
            // Jump to location nnn + V0.
            // The program counter is set to nnn plus the value of V0.
            PC = (short) (registers[0] + nnn);
        } else if (opcode < 0xD000) {
            // Cxkk - RND Vx, byte
            // Set Vx = random byte AND kk.
            // The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk.
            // The results are stored in Vx. See instruction 8xy2 for more information on AND.
            byte rnd = (byte) (random.nextInt() % 0xF);
            registers[x] = (byte) (rnd & kk);
        } else if (opcode < 0xE000) {
            // Dxyn - DRW Vx, Vy, nibble
            // Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
            /*
            The interpreter reads n bytes from memory, starting at the address stored in I.
            These bytes are then displayed as sprites on screen at coordinates (Vx, Vy).
            Sprites are XORed onto the existing screen. If this causes any pixels to be erased,
            VF is set to 1, otherwise it is set to 0. If the sprite is positioned so part of it is outside
            the coordinates of the display, it wraps around to the opposite side of the screen.
            See instruction 8xy3 for more information on XOR, and section 2.4, Display,
            for more information on the Chip-8 screen and sprites.
             */
            registers[0xF] = 0;
            byte xPos = (byte) (registers[x] % Display.WIDTH);
            byte yPos = (byte) (registers[y] % Display.HEIGHT);
            byte height = first_nibble;

            for (int row = 0; row < height; row++) {
                byte spriteByte = this.RAM[this.I + row];
                for (int col = 0; col < 8; col++) {
                    byte spritePixel = (byte) (spriteByte & (0x80 >> col)); // TODO: It should be boolean
                    int pixel_index = (yPos + row) * Display.WIDTH + (xPos + col);
                    boolean pixel = display.getPixel(row, col);

                    if (spritePixel != 0) {
                        if (pixel)
                            registers[0xF] = 1;
                        boolean a = pixel ^  true; // TODO: Something here
                        display.setPixel(row, col, a);
                    }
                }
            }
        } else if (opcode < 0xF000) {
            byte key = registers[x];
            switch (lsb) {
                case 0x9E:
                    // Ex9E - SKP Vx
                    // Skip next instruction if key with the value of Vx is pressed.
                    // Checks the keyboard, and if the key corresponding to the value of Vx is currently in the down position, PC is increased by 2.
                    if (keypad[key])
                        PC += 2;
                    break;
                case 0xA1:
                    // ExA1 - SKNP Vx
                    // Skip next instruction if key with the value of Vx is not pressed.
                    // Checks the keyboard, and if the key corresponding to the value of Vx is currently in the up position, PC is increased by 2.
                    if ( ! keypad[key])
                        PC += 2;
                    break;
                default:
                    throw new IllegalArgumentException("Impossible instruction, first octet isn't 0x9E or 0xA1, first octet: " + lsb);
            };
        } else {
            switch (lsb) {
                case 0x07:
                    // Fx07 - LD Vx, DT
                    // Set Vx = delay timer value.
                    // The value of DT is placed into Vx.
                    registers[x] = delay_timer;
                    break;
                case 0x0A:
                    // Fx0A - LD Vx, K
                    // Wait for a key press, store the value of the key in Vx.
                    // All execution stops until a key is pressed, then the value of that key is stored in Vx.
                    logger.info("Waiting for key press");
                    boolean keyPressed = false;
                    while (! keyPressed) {
                        for (int i = 0; i < 0xF; i++) {
                            if (keypad[i]) {
                                registers[x] = (byte) i;
                                keyPressed = true;
                                break;
                            }
                        }
                    }
                    break;
                case 0x15:
                    // Fx15 - LD DT, Vx
                    // Set delay timer = Vx.
                    // DT is set equal to the value of Vx.
                    delay_timer = registers[x];
                    break;
                case 0x18:
                    // Fx18 - LD ST, Vx
                    // Set sound timer = Vx.
                    // ST is set equal to the value of Vx.
                    sound_timer = registers[x];
                    break;
                case 0x1E:
                    // Fx1E - ADD I, Vx
                    // Set I = I + Vx.
                    // The values of I and Vx are added, and the results are stored in I.
                    this.I += registers[x];
                    break;
                case 0x29:
                    // Fx29 - LD F, Vx
                    // Set I = location of sprite for digit Vx.
                    // The value of I is set to the location for the hexadecimal sprite corresponding to the value of Vx. See section 2.4, Display, for more information on the Chip-8 hexadecimal font.
                    this.I = (short) (0x50 + ( 5 * registers[x] ));
                    break;
                case 0x33:
                    // Fx33 - LD B, Vx
                    // Store BCD representation of Vx in memory locations I, I+1, and I+2.
                    // The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.
                    byte value = registers[x];
                    this.RAM[this.I + 2] = (byte) (value % 10);
                    value /= 10;
                    this.RAM[this.I + 1] = (byte) (value % 10);
                    value /= 10;
                    this.RAM[this.I] = (byte) (value % 10);
                    break;
                case 0x55:
                    // Fx55 - LD [I], Vx
                    // Store registers V0 through Vx in memory starting at location I.
                    // The interpreter copies the values of registers V0 through Vx into memory, starting at the address in I.
                    for (int i = 0; i <= x; i++)
                        this.RAM[this.I + i] = registers[i];
                    break;
                case 0x65:
                    // Fx65 - LD Vx, [I]
                    // Read registers V0 through Vx from memory starting at location I.
                    // The interpreter reads values from memory starting at location I into registers V0 through Vx.
                    for (int i = 0; i <= x; i++)
                        registers[i] = this.RAM[this.I + i];
                    break;
                default:
                    throw new IllegalArgumentException("Can't decode opcode: " + String.format("0x%02", opcode));
            };
        }
    }
}
