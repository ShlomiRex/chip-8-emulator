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
        K, // Key pressed by user
        ST, // Sound timer value
        F, // Font
        B, // Binary coded decimal
        I_ARRAY; // Represents registers V0 to Vx starting at memory location I

        @Override
        public String toString() {
            if (this == I_ARRAY) {
                return "[I]";
            } else {
                return super.toString();
            }
        }
    }

    protected Instructions instruction;
    protected Operand op1, op2;
    protected byte value;
    protected boolean is_value_2_bytes = false;
    protected short extended_value;
    protected boolean no_value = false;

    Instruction(Instructions i) {
        this.instruction = i;
        this.no_value = true;
    }

    Instruction(Instructions i, Operand op1, Operand op2, byte value) {
        this.instruction = i;
        this.op1 = op1;
        this.op2 = op2;
        this.value = value;
    }

    Instruction(Instructions i, Operand op1, Operand op2, short value) {
        this.instruction = i;
        this.op1 = op1;
        this.op2 = op2;
        this.extended_value = value;
        this.is_value_2_bytes = true;
    }

    Instruction(Instructions i, short value) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.instruction.name()).append(" ");

        if (this.op1 != null) {
            sb.append(this.op1).append(", ");

            // Display op2 only if op1 is not null, and op2 is not null.
            if (this.op2 != null)
                sb.append(this.op2).append(", ");
        }

        sb.append(String.format("0x%02X", is_value_2_bytes ? this.extended_value : this.value));
        sb.append(" ("+(is_value_2_bytes ? this.extended_value : this.value)+")");
        return sb.toString();
    }
}
