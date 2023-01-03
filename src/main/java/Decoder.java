public class Decoder {
    public static Instruction decodeInstruction(short opcode) {
        byte x = get_nibble(opcode, 3);
        Instruction.Operand vx = decodeOperand(x);

        byte y = get_nibble(opcode, 2);
        Instruction.Operand vy = decodeOperand(y);

        short kk = get_lsb(opcode);
        short nnn = (short) (opcode & 0x0FFF);
        byte nn = get_lsb(opcode);
        short lsb = get_lsb(opcode);
        byte first_nibble = get_nibble(opcode, 1);

        if (opcode < 0x1000) {
            if (opcode == 0x00E0) {
                // 00E0 - CLS
                // Clear the display.
                return new Instruction(Instruction.Instructions.CLS);
            } else if (opcode == 0x00EE) {
                // 00EE - RET
                // Return from a subroutine.
                // The interpreter sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
                return new Instruction(Instruction.Instructions.RET);
            } else {
                // 0nnn - SYS addr
                // Jump to a machine code routine at nnn.
                // This instruction is only used on the old computers on which Chip-8 was originally implemented. It is ignored by modern interpreters.
                return new Instruction(Instruction.Instructions.CALL,  nnn);
            }
        } else if (opcode < 0x2000) {
            // 1nnn - JP addr
            // Jump to location nnn.
            // The interpreter sets the program counter to nnn.

            return new Instruction(Instruction.Instructions.JP, nnn);
        } else if (opcode < 0x3000) {
            // 2nnn - CALL addr
            // Call subroutine at nnn.
            // The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
            return new Instruction(Instruction.Instructions.CALL, nnn);
        } else if (opcode < 0x4000) {
            // 3xkk - SE Vx, byte
            // Skip next instruction if Vx = kk.
            // The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2.
            return new Instruction(Instruction.Instructions.SE, vx, null, kk);
        } else if (opcode < 0x5000) {
            // 4xkk - SNE Vx, byte
            // Skip next instruction if Vx != kk.
            // The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
            return new Instruction(Instruction.Instructions.SNE, vx, null, kk);
        } else if (opcode < 0x6000) {
            // 5xy0 - SE Vx, Vy
            // Skip next instruction if Vx = Vy.
            // The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
            return new Instruction(Instruction.Instructions.SE, vx, vy);
        } else if (opcode < 0x7000) {
            // 6xkk - LD Vx, byte
            // Set Vx = kk.
            // The interpreter puts the value kk into register Vx.
            return new Instruction(Instruction.Instructions.LD, vx, null, nn);
        } else if (opcode < 0x8000) {
            // 7xkk - ADD Vx, byte
            // Set Vx = Vx + kk.
            // Adds the value kk to the value of register Vx, then stores the result in Vx.
            return new Instruction(Instruction.Instructions.ADD, vx, null, kk);
        } else if (opcode < 0x9000) {
            return switch (lsb) {
                case 0 ->
                    // 8xy0 - LD Vx, Vy
                    // Set Vx = Vy.
                    // Stores the value of register Vy in register Vx.
                        new Instruction(Instruction.Instructions.LD, vx, vy);
                case 1 ->
                    // 8xy1 - OR Vx, Vy
                    // Set Vx = Vx OR Vy.
                    // Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx. A bitwise OR compares the corrseponding bits from two values, and if either bit is 1, then the same bit in the result is also 1. Otherwise, it is 0.
                        new Instruction(Instruction.Instructions.OR, vx, vy);
                case 2 ->
                    // 8xy2 - AND Vx, Vy
                    // Set Vx = Vx AND Vy.
                    // Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx. A bitwise AND compares the corrseponding bits from two values, and if both bits are 1, then the same bit in the result is also 1. Otherwise, it is 0.
                        new Instruction(Instruction.Instructions.AND, vx, vy);
                case 3 ->
                    // 8xy3 - XOR Vx, Vy
                    // Set Vx = Vx XOR Vy.
                    // Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx. An exclusive OR compares the corrseponding bits from two values, and if the bits are not both the same, then the corresponding bit in the result is set to 1. Otherwise, it is 0.
                        new Instruction(Instruction.Instructions.XOR, vx, vy);
                case 4 ->
                    // 8xy4 - ADD Vx, Vy
                    // Set Vx = Vx + Vy, set VF = carry.
                    // The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1, otherwise 0. Only the lowest 8 bits of the result are kept, and stored in Vx.
                        new Instruction(Instruction.Instructions.ADD, vx, vy);
                case 5 ->
                    // 8xy5 - SUB Vx, Vy
                    // Set Vx = Vx - Vy, set VF = NOT borrow.
                    // If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
                        new Instruction(Instruction.Instructions.SUB, vx, vy);
                case 6 ->
                    // 8xy6 - SHR Vx {, Vy}
                    // Set Vx = Vx SHR 1.
                    // If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0. Then Vx is divided by 2.
                        new Instruction(Instruction.Instructions.SHR, vx, null);
                case 7 ->
                    // 8xy7 - SUBN Vx, Vy
                    // Set Vx = Vy - Vx, set VF = NOT borrow.
                    // If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.
                        new Instruction(Instruction.Instructions.SUBN, vx, vy);
                case 0xE ->
                    // 8xyE - SHL Vx {, Vy}
                    // Set Vx = Vx SHL 1.
                    // If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
                        new Instruction(Instruction.Instructions.SHL, vx, null);
                default ->
                        throw new IllegalArgumentException("Impossible instruction, first octet not in range 0-7 and not equal to E. First octet: " + lsb);
            };
        } else if (opcode < 0xA000) {
            // 9xy0 - SNE Vx, Vy
            // Skip next instruction if Vx != Vy.
            // The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2.
            return new Instruction(Instruction.Instructions.SNE, vx, vy);
        } else if (opcode < 0xB000) {
            // Annn - LD I, addr
            // Set I = nnn.
            // The value of register I is set to nnn.
            return new Instruction(Instruction.Instructions.LD, Instruction.Operand.I, null, nnn);
        } else if (opcode < 0xC000) {
            // Bnnn - JP V0, addr
            // Jump to location nnn + V0.
            // The program counter is set to nnn plus the value of V0.
            return new Instruction(Instruction.Instructions.JP, Instruction.Operand.V0, null, nnn);
        } else if (opcode < 0xD000) {
            // Cxkk - RND Vx, byte
            // Set Vx = random byte AND kk.
            // The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk.
            // The results are stored in Vx. See instruction 8xy2 for more information on AND.
            return new Instruction(Instruction.Instructions.RND, vx, null, kk);
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
            return new Instruction(Instruction.Instructions.DRW, vx, vy, first_nibble);
        } else if (opcode < 0xF000) {
            return switch (lsb) {
                case 0x9E ->
                    // Ex9E - SKP Vx
                    // Skip next instruction if key with the value of Vx is pressed.
                    // Checks the keyboard, and if the key corresponding to the value of Vx is currently in the down position, PC is increased by 2.
                        new Instruction(Instruction.Instructions.SKP, vx, null);
                case 0xA1 ->
                    // ExA1 - SKNP Vx
                    // Skip next instruction if key with the value of Vx is not pressed.
                    // Checks the keyboard, and if the key corresponding to the value of Vx is currently in the up position, PC is increased by 2.
                        new Instruction(Instruction.Instructions.SKNP, vx, null);
                default ->
                        throw new IllegalArgumentException("Impossible instruction, first octet isn't 0x9E or 0xA1, first octet: " + lsb);
            };
        } else {
            return switch (lsb) {
                case 0x07 ->
                    // Fx07 - LD Vx, DT
                    // Set Vx = delay timer value.
                    // The value of DT is placed into Vx.
                        new Instruction(Instruction.Instructions.LD, vx, Instruction.Operand.DT);
                case 0x0A ->
                    // Fx0A - LD Vx, K
                    // Wait for a key press, store the value of the key in Vx.
                    // All execution stops until a key is pressed, then the value of that key is stored in Vx.
                        new Instruction(Instruction.Instructions.LD, vx, Instruction.Operand.K);
                case 0x15 ->
                    // Fx15 - LD DT, Vx
                    // Set delay timer = Vx.
                    // DT is set equal to the value of Vx.
                        new Instruction(Instruction.Instructions.LD, Instruction.Operand.DT, vx);
                case 0x18 ->
                    // Fx18 - LD ST, Vx
                    // Set sound timer = Vx.
                    // ST is set equal to the value of Vx.
                        new Instruction(Instruction.Instructions.LD, Instruction.Operand.ST, vx);
                case 0x1E ->
                    // Fx1E - ADD I, Vx
                    // Set I = I + Vx.
                    // The values of I and Vx are added, and the results are stored in I.
                        new Instruction(Instruction.Instructions.ADD, Instruction.Operand.I, vx);
                case 0x29 ->
                    // Fx29 - LD F, Vx
                    // Set I = location of sprite for digit Vx.
                    // The value of I is set to the location for the hexadecimal sprite corresponding to the value of Vx. See section 2.4, Display, for more information on the Chip-8 hexadecimal font.
                        new Instruction(Instruction.Instructions.LD, Instruction.Operand.F, vx);
                case 0x33 ->
                    // Fx33 - LD B, Vx
                    // Store BCD representation of Vx in memory locations I, I+1, and I+2.
                    // The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.
                        new Instruction(Instruction.Instructions.LD, Instruction.Operand.B, vx);
                case 0x55 ->
                    // Fx55 - LD [I], Vx
                    // Store registers V0 through Vx in memory starting at location I.
                    // The interpreter copies the values of registers V0 through Vx into memory, starting at the address in I.
                        new Instruction(Instruction.Instructions.LD, Instruction.Operand.I_ARRAY, vx);
                case 0x65 ->
                    // Fx65 - LD Vx, [I]
                    // Read registers V0 through Vx from memory starting at location I.
                    // The interpreter reads values from memory starting at location I into registers V0 through Vx.
                        new Instruction(Instruction.Instructions.LD, vx, Instruction.Operand.I);
                default -> throw new IllegalArgumentException("Can't decode opcode: " + String.format("0x%02", opcode));
            };
        }
    }

    public static Instruction.Operand decodeOperand(byte value) {
        String op_str = "V" + value;
        return Instruction.Operand.valueOf(op_str);
    }

    public static byte get_lsb(short input) {
        return (byte) ((input << 8) >> 8);
    }

    /**
     * Returns nibble given the nibble number. Nibble number is ordered from right to left, like so: 4321
     * @param input
     * @param nibble_num
     * @return An octet. The octet value can be from 0 to 15, no more.
     */
    public static byte get_nibble(short input, int nibble_num) {
        return switch (nibble_num) {
            case 1 -> (byte) (input & 0x000F);
            case 2 -> (byte) ((input & 0x00F0) >> 4);
            case 3 -> (byte) ((input & 0x0F00) >> 8);
            case 4 -> (byte) ((input & 0xF000) >> 12);
            default -> throw new IllegalArgumentException("Octet can be 1-4. Got: " + nibble_num);
        };
    }
}
