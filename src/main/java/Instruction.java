public class Instruction {
    public enum Instructions {
        CLS,
        RET,
        JP,
        CALL,
        RTS,
        SE, SNE, LD
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
        VF
    }

    protected Instructions instruction;
    protected Operand op1, op2;
    protected byte value;
    protected boolean is_value_2_bytes = false;
    protected short extended_value;
    protected boolean no_value = false;

    private Instruction(Instructions i) {
        this.instruction = i;
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
            byte x = BitHelper.get_octet(opcode, 3);
            short kk = BitHelper.get_lsb(opcode);
            Operand op1 = decodeOperand(x);
            return new Instruction(Instructions.SE, op1, null, kk);
        } else if (opcode < 0x5000) {
            // 4xkk - SNE Vx, byte
            // Skip next instruction if Vx != kk.
            // The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
            byte x = BitHelper.get_octet(opcode, 3);
            short kk = BitHelper.get_lsb(opcode);
            Operand op1 = decodeOperand(x);
            return new Instruction(Instructions.SNE, op1, null, kk);
        } else if (opcode < 0x6000) {
            // 5xy0 - SE Vx, Vy
            // Skip next instruction if Vx = Vy.
            // The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
            byte x = BitHelper.get_octet(opcode, 3);
            byte y = BitHelper.get_octet(opcode, 2);
            Operand op1 = decodeOperand(x);
            Operand op2 = decodeOperand(y);
            return new Instruction(Instructions.SE, op1, op2);
        } else if (opcode < 0x7000) {
            // 6xkk - LD Vx, byte
            // Set Vx = kk.
            // The interpreter puts the value kk into register Vx.
            byte x = BitHelper.get_octet(opcode, 3);
            byte nn = BitHelper.get_lsb(opcode);
            Operand op1 = decodeOperand(x);
            return new Instruction(Instructions.LD, op1, null, nn);
        }

        return null;
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
