package Tools;

import java.util.ArrayList;

public class Buffer {
    private ArrayList<Character> buffer;

    public Buffer() {
        this.buffer = new ArrayList<>();
    }

    public void addCharacter(Character c) {
        buffer.add(c);
    }

    public char getCharacter(int index) {
        if (index >= 0 && index < buffer.size()) {
            return buffer.get(index);
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
    }

    public int getSize() {
        return buffer.size();
    }
    public void clear() {
        buffer.clear();
    }
    public Character getLastCharacter() {
        if (!buffer.isEmpty()) {
            return buffer.get(buffer.size() - 1);
        } else {
            throw new IllegalStateException("Buffer is empty");
        }
    }


    public boolean isEmpty() {
        return buffer.isEmpty();
    }
}