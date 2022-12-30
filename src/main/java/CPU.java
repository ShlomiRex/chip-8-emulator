import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        // Fetch next instruction
        short opcode = this.RAM[this.PC];

        // Decode instruction
        logger.debug(""+String.format("0x%2X", opcode));

        // TODO: Execute instruction

        this.PC += 1;
    }

    private void decode_instruction(short opcode) {

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
