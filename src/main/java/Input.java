public class Input {
    public boolean is_valid_input(char keycode) {
        if (keycode >= '0' && keycode <= '9')
            return true;
        if (keycode >= 'A' && keycode <= 'F')
            return true;
        return false;
    }
}
