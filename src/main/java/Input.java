import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 * This class listens to keyboard input. It updates the local keypad variable. Each tick, the CPU keypad will be updated to be equal to this local keypad.
 */
public class Input extends KeyAdapter {

    private final boolean[] keypad = new boolean[16];

    private static final Logger logger = LoggerFactory.getLogger(Input.class);

    /**
     * Maps keyboard input to the CHIP-8 keypad. Key map:
     * Chip-8 Key  Keyboard
     * ----------  ---------
     *   1 2 3 C    1 2 3 4
     *   4 5 6 D    q w e r
     *   7 8 9 E    a s d f
     *   A 0 B F    z x c v
     */
    private final Map<Character, Character> keymap;

    /**
     * Maps keypad key to keypad index (0-16).
     */
    private final Map<Character, Integer> keypad_map;

    private static final char[] allowed_keys = new char[]{
            '1','2','3','4',
            'q','w','e','r',
            'a','s','d','f',
            'z','x','c','v'
    };

    /**
     * Responsible for handling keyboard input and mapping to CHIP-8 keypad.
     */
    public Input() {
        char[] map_keys = allowed_keys;

        char[] map_values = new char[]{
                '1','2','3','c',
                '4','5','6','d',
                '7','8','9','e',
                'a','0','b','f'
        };

        // Keyboard to Keypad map
        Map<Character, Character> keymap = new HashMap<>();
        for(int i = 0; i < map_keys.length; i++)
            keymap.put(map_keys[i], map_values[i]);
        this.keymap = keymap;

        // Keypad to Keypad index map
        Map<Character, Integer> keypad_map = new HashMap<>();
        for(int i = 0; i < map_values.length; i++) {
            keypad_map.put(map_values[i], i);
        }
        this.keypad_map = keypad_map;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        char keyboard_key = e.getKeyChar();
        boolean is_valid = is_valid_input(keyboard_key);
        if (!is_valid)
            return;
        // Convert keyboard key to keypad key
        char mapped_keypad_key = this.keymap.get(keyboard_key);
        // Convert keypad key to keypad index
        int keypad_index = this.keypad_map.get(mapped_keypad_key);
        logger.debug("Key pressed: '"+e.getKeyChar()+"' -> '"+mapped_keypad_key+"', keypad index: "+keypad_index);
        this.keypad[keypad_index] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        char keyboard_key = e.getKeyChar();
        boolean is_valid = is_valid_input(keyboard_key);
        if (!is_valid)
            return;
        // Convert keyboard key to keypad key
        char mapped_keypad_key = this.keymap.get(keyboard_key);
        // Convert keypad key to keypad index
        int keypad_index = this.keypad_map.get(mapped_keypad_key);
        logger.debug("Key released: '"+keyboard_key+"' -> '"+mapped_keypad_key+"', keypad index: "+keypad_index);
        this.keypad[keypad_index] = false;
    }

    public boolean is_valid_input(char keycode) {
        for (char c : allowed_keys)
            if (keycode == c)
                return true;
        return false;
    }

    public boolean[] get_keypad() {
        return keypad;
    }
}
