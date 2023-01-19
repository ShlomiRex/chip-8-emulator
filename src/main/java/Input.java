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

    private final Map<Character, Integer> keyboard_to_keypad_index_map;

    private static final char[] allowed_keys = new char[]{
            '1','2','3','4',
            'q','w','e','r',
            'a','s','d','f',
            'z','x','c','v'
    };

    /**
     * Responsible for handling keyboard input and mapping to CHIP-8 keypad.
     * Map:
     * Chip-8 Key  Keyboard
     * ----------  ---------
     *   1 2 3 C    1 2 3 4
     *   4 5 6 D    q w e r
     *   7 8 9 E    a s d f
     *   A 0 B F    z x c v
     */
    public Input() {
        keyboard_to_keypad_index_map = new HashMap<>();
        keyboard_to_keypad_index_map.put('1', 1);
        keyboard_to_keypad_index_map.put('2', 2);
        keyboard_to_keypad_index_map.put('3', 3);
        keyboard_to_keypad_index_map.put('4', 0xC);

        keyboard_to_keypad_index_map.put('q', 4);
        keyboard_to_keypad_index_map.put('w', 5);
        keyboard_to_keypad_index_map.put('e', 6);
        keyboard_to_keypad_index_map.put('r', 0xD);

        keyboard_to_keypad_index_map.put('a', 7);
        keyboard_to_keypad_index_map.put('s', 8);
        keyboard_to_keypad_index_map.put('d', 9);
        keyboard_to_keypad_index_map.put('f', 0xE);

        keyboard_to_keypad_index_map.put('z', 0xA);
        keyboard_to_keypad_index_map.put('x', 0);
        keyboard_to_keypad_index_map.put('c', 0xB);
        keyboard_to_keypad_index_map.put('v', 0xF);
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
        //char mapped_keypad_key = this.keymap.get(keyboard_key);
        // Convert keypad key to keypad index
        //int keypad_index = this.keypad_map.get(mapped_keypad_key);
        //logger.debug("Key pressed: '"+e.getKeyChar()+"' -> '"+mapped_keypad_key+"', keypad index: "+keypad_index);
        int keypad_index = this.keyboard_to_keypad_index_map.get(keyboard_key);
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
        //char mapped_keypad_key = this.keymap.get(keyboard_key);
        // Convert keypad key to keypad index
        //int keypad_index = this.keypad_map.get(mapped_keypad_key);
        //logger.debug("Key released: '"+keyboard_key+"' -> '"+mapped_keypad_key+"', keypad index: "+keypad_index);
        int keypad_index = this.keyboard_to_keypad_index_map.get(keyboard_key);
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
