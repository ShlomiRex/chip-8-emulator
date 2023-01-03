import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

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

    /**
     * Creates new Chip-8 CPU.
     * Address 0x200 is start of the program in memory.
     */
    public CPU(byte[] rom_program) {
        this.SP = 0;
        this.PC = 0x200;

        for (int i = 0; i < rom_program.length; i++)
            this.RAM[0x200 + i] = rom_program[i];
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

        // Decode instruction
        Instruction i = Decoder.decodeInstruction(opcode);
        logger.debug(String.format("0x%04X", opcode) + ": " + i);

        // Execute instruction
        execute_instruction(i);

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

    private void execute_instruction(Instruction instruction) {
        if (instruction == null) {
            logger.error("Instruction given is null.");
            return;
        }

        // Prefabricated objects / primitives to be used
        short value = -1;
        if (instruction.is_value_2_bytes)
            value = instruction.extended_value;
        else
            value = instruction.value;

        int register_index = instruction.op1.ordinal();

        switch(instruction.instruction) {
            case LD:
                this.registers[register_index] = instruction.value;
                break;
            default:
                logger.error("Instruction not yet supported");
        }
    }

    private void push(short value) {
        //TODO: What if SP is 16? We can't push any more!
        stack[SP++] = value;
    }

    private short pop() {
        //TODO: What to do when SP is 0 and we decrement it? Overflow?
        return stack[SP--];
    }
}
