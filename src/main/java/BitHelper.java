public class BitHelper {
    /**
     * Converts a byte to its hex form. The input must be from 0 to 16 in decimal.
     * @param input
     * @return
     */
    public static char byte_to_hex(byte input) {
        if (input <= 9)
            return (char) (input + '0');
        return switch (input) {
            case 10 -> 'A';
            case 11 -> 'B';
            case 12 -> 'C';
            case 13 -> 'D';
            case 14 -> 'E';
            case 15 -> 'F';
            default -> throw new RuntimeException("Expected input in range 0-15. Got: " + input);
        };
    }

    public static byte get_msb(short input) {
        return (byte) (input >> 8);
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
