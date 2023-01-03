public class Instruction {
    public enum Instructions {
        CLS,
        RET,
        JP,
        CALL,
        RTS,
        SE, SNE, ADD, OR, AND, XOR, SUB, SHR, SUBN, SHL, RND, DRW, SKP, SKNP, LD
    }

    public enum Operand {
        V0,
        V1,
        V2,
        V3,
        V4,
        V5,
        V6,
        V7,
        V8,
        V9,
        VA,
        VB,
        VC,
        VD,
        VE,
        VF,
        I,   // The 'I' register
        DT, // Delay timer value
    }

    protected Instructions instruction;
    protected Operand op1, op2;
    protected byte value;
    protected boolean is_value_2_bytes = false;
    protected short extended_value;
    protected boolean no_value = false;

    private Instruction(Instructions i) {
        this.instruction = i;
        this.no_value = true;
    }

    private Instruction(Instructions i, Operand op1, Operand op2, byte value) {
        this.instruction = i;
        this.op1 = op1;
        this.op2 = op2;
        this.value = value;
    }

    private Instruction(Instructions i, Operand op1, Operand op2, short value) {
        this.instruction = i;
        this.op1 = op1;
        this.op2 = op2;
        this.extended_value = value;
        this.is_value_2_bytes = true;
    }

    private Instruction(Instructions i, short value) {
        this.instruction = i;
        this.extended_value = value;
        this.is_value_2_bytes = true;
    }

    public Instruction(Instructions i, Operand op1, Operand op2) {
        this.instruction = i;
        this.op1 = op1;
        this.op2 = op2;
        this.no_value = true;
    }

    public static Instruction decodeInstruction(short opcode) {
        if (opcode < 0x1000) {
            if (opcode == 0x00E0) {
                // 00E0 - CLS
                // Clear the display.
                return new Instruction(Instructions.CLS);
            } else if (opcode == 0x00EE) {
                // 00EE - RET
                // Return from a subroutine.
                // The interpreter sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
                return new Instruction(Instructions.RET);
            } else {
                // 0nnn - SYS addr
                // Jump to a machine code routine at nnn.
                // This instruction is only used on the old computers on which Chip-8 was originally implemented. It is ignored by modern interpreters.
                short nnn = (short) (opcode & 0x0FFF);
                return new Instruction(Instructions.CALL,  nnn);
            }
        } else if (opcode < 0x2000) {
            // 1nnn - JP addr
            // Jump to location nnn.
            // The interpreter sets the program counter to nnn.
            short nnn = (short) (opcode & 0x0FFF);
            return new Instruction(Instructions.JP, nnn);
        } else if (opcode < 0x3000) {
            // 2nnn - CALL addr
            // Call subroutine at nnn.
            // The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
            short nnn = (short) (opcode & 0x0FFF);
            return new Instruction(Instructions.CALL, nnn);
        } else if (opcode < 0x4000) {
            // 3xkk - SE Vx, byte
            // Skip next instruction if Vx = kk.
            // The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2.
            byte x = BitHelper.get_nibble(opcode, 3);
            short kk = BitHelper.get_lsb(opcode);
            Operand op1 = decodeOperand(x);
            return new Instruction(Instructions.SE, op1, null, kk);
        } else if (opcode < 0x5000) {
            // 4xkk - SNE Vx, byte
            // Skip next instruction if Vx != kk.
            // The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
            byte x = BitHelper.get_nibble(opcode, 3);
            short kk = BitHelper.get_lsb(opcode);
            Operand op1 = decodeOperand(x);
            return new Instruction(Instructions.SNE, op1, null, kk);
        } else if (opcode < 0x6000) {
            // 5xy0 - SE Vx, Vy
            // Skip next instruction if Vx = Vy.
            // The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
            byte x = BitHelper.get_nibble(opcode, 3);
            byte y = BitHelper.get_nibble(opcode, 2);
            Operand op1 = decodeOperand(x);
            Operand op2 = decodeOperand(y);
            return new Instruction(Instructions.SE, op1, op2);
        } else if (opcode < 0x7000) {
            // 6xkk - LD Vx, byte
            // Set Vx = kk.
            // The interpreter puts the value kk into register Vx.
            byte x = BitHelper.get_nibble(opcode, 3);
            byte nn = BitHelper.get_lsb(opcode);
            Operand op1 = decodeOperand(x);
            return new Instruction(Instructions.LD, op1, null, nn);
        } else if (opcode < 0x8000) {
            // 7xkk - ADD Vx, byte
            // Set Vx = Vx + kk.
            // Adds the value kk to the value of register Vx, then stores the result in Vx.
            byte x = BitHelper.get_nibble(opcode, 3);
            short kk = BitHelper.get_lsb(opcode);
            Operand op1 = decodeOperand(x);
            return new Instruction(Instructions.ADD, op1, null, kk);
        } else if (opcode < 0x9000) {
            byte first_octet = BitHelper.get_nibble(opcode, 1);
            byte x = BitHelper.get_nibble(opcode, 3);
            byte y = BitHelper.get_nibble(opcode, 2);
            Operand op1 = decodeOperand(x);
            Operand op2 = decodeOperand(y);
            return switch (first_octet) {
                case 0 ->
                    // 8xy0 - LD Vx, Vy
                    // Set Vx = Vy.
                    // Stores the value of register Vy in register Vx.
                        new Instruction(Instructions.LD, op1, op2);
                case 1 ->
                    // 8xy1 - OR Vx, Vy
                    // Set Vx = Vx OR Vy.
                    // Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx. A bitwise OR compares the corrseponding bits from two values, and if either bit is 1, then the same bit in the result is also 1. Otherwise, it is 0.
                        new Instruction(Instructions.OR, op1, op2);
                case 2 ->
                    // 8xy2 - AND Vx, Vy
                    // Set Vx = Vx AND Vy.
                    // Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx. A bitwise AND compares the corrseponding bits from two values, and if both bits are 1, then the same bit in the result is also 1. Otherwise, it is 0.
                        new Instruction(Instructions.AND, op1, op2);
                case 3 ->
                    // 8xy3 - XOR Vx, Vy
                    // Set Vx = Vx XOR Vy.
                    // Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx. An exclusive OR compares the corrseponding bits from two values, and if the bits are not both the same, then the corresponding bit in the result is set to 1. Otherwise, it is 0.
                        new Instruction(Instructions.XOR, op1, op2);
                case 4 ->
                    // 8xy4 - ADD Vx, Vy
                    // Set Vx = Vx + Vy, set VF = carry.
                    // The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1, otherwise 0. Only the lowest 8 bits of the result are kept, and stored in Vx.
                        new Instruction(Instructions.ADD, op1, op2);
                case 5 ->
                    // 8xy5 - SUB Vx, Vy
                    // Set Vx = Vx - Vy, set VF = NOT borrow.
                    // If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
                        new Instruction(Instructions.SUB, op1, op2);
                case 6 ->
                    // 8xy6 - SHR Vx {, Vy}
                    // Set Vx = Vx SHR 1.
                    // If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0. Then Vx is divided by 2.
                        new Instruction(Instructions.SHR, op1, null);
                case 7 ->
                    // 8xy7 - SUBN Vx, Vy
                    // Set Vx = Vy - Vx, set VF = NOT borrow.
                    // If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.
                        new Instruction(Instructions.SUBN, op1, op2);
                case 0xE ->
                    // 8xyE - SHL Vx {, Vy}
                    // Set Vx = Vx SHL 1.
                    // If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
                        new Instruction(Instructions.SHL, op1, null);
                default ->
                        throw new IllegalArgumentException("Impossible instruction, first octet not in range 0-7 and not equal to E. First octet: " + first_octet);
            };
        } else if (opcode < 0xA000) {
            // 9xy0 - SNE Vx, Vy
            // Skip next instruction if Vx != Vy.
            // The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2.
            byte x = BitHelper.get_nibble(opcode, 3);
            byte y = BitHelper.get_nibble(opcode, 2);
            Operand op1 = decodeOperand(x);
            Operand op2 = decodeOperand(y);
            return new Instruction(Instructions.SNE, op1, op2);
        } else if (opcode < 0xB000) {
            // Annn - LD I, addr
            // Set I = nnn.
            // The value of register I is set to nnn.
            short nnn = (short) (opcode & 0x0FFF);
            return new Instruction(Instructions.LD, Operand.I, null, nnn);
        } else if (opcode < 0xC000) {
            // Bnnn - JP V0, addr
            // Jump to location nnn + V0.
            // The program counter is set to nnn plus the value of V0.
            short nnn = (short) (opcode & 0x0FFF);
            return new Instruction(Instructions.JP, Operand.V0, null, nnn);
        } else if (opcode < 0xD000) {
            // Cxkk - RND Vx, byte
            // Set Vx = random byte AND kk.
            // The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk.
            // The results are stored in Vx. See instruction 8xy2 for more information on AND.
            byte x = BitHelper.get_nibble(opcode, 3);
            Operand op1 = decodeOperand(x);
            short kk = BitHelper.get_lsb(opcode);
            return new Instruction(Instructions.RND, op1, null, kk);
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
            byte x = BitHelper.get_nibble(opcode, 3);
            byte y = BitHelper.get_nibble(opcode, 2);
            Operand op1 = decodeOperand(x);
            Operand op2 = decodeOperand(y);
            byte nibble = BitHelper.get_nibble(opcode, 1);
            return new Instruction(Instructions.DRW, op1, op2, nibble);
        } else if (opcode < 0xF000) {
            short lsb = BitHelper.get_lsb(opcode);
            byte x = BitHelper.get_nibble(opcode, 3);
            Operand op1 = decodeOperand(x);
            return switch (lsb) {
                case 0x9E ->
                    // Ex9E - SKP Vx
                    // Skip next instruction if key with the value of Vx is pressed.
                    // Checks the keyboard, and if the key corresponding to the value of Vx is currently in the down position, PC is increased by 2.
                        new Instruction(Instructions.SKP, op1, null);
                case 0xA1 ->
                    // ExA1 - SKNP Vx
                    // Skip next instruction if key with the value of Vx is not pressed.
                    // Checks the keyboard, and if the key corresponding to the value of Vx is currently in the up position, PC is increased by 2.
                        new Instruction(Instructions.SKNP, op1, null);
                default ->
                        throw new IllegalArgumentException("Impossible instruction, first octet isn't 0x9E or 0xA1, first octet: " + lsb);
            };
        } else {
            short lsb = BitHelper.get_lsb(opcode);
            byte x = BitHelper.get_nibble(opcode, 3);
            Operand op1 = decodeOperand(x);
            switch (lsb) {
                case 0x07:
                    // Fx07 - LD Vx, DT
                    // Set Vx = delay timer value.
                    // The value of DT is placed into Vx.
                    return new Instruction(Instructions.LD, op1, Operand.DT);
            }
        }
    }

    private static Operand decodeOperand(byte value) {
        String op_str = "V" + value;
        return Operand.valueOf(op_str);
    }

    @Override
    public String toString() {
        return this.instruction.name() + " " + this.op1 + ", " + String.format("0x%02X", this.value);
    }
}
