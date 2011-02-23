package lsdsoft.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Ural-Geo</p>
 * @author lsdsoft
 * @version 0.9
 */

public class FIFOBuffer {
    private int[] buffer;
    private int capacity;
    private int head = 0;
    private int tail = 0;

    public FIFOBuffer() {
        this(64);
    }
    public FIFOBuffer(int capacity) {
        buffer = new int[capacity];
        this.capacity = capacity;
    }
    public void clear() {
        head = 0;
        tail = 0;
    }
    public int size() {
        return (head - tail + capacity) % capacity;
    }
    public void push(int value) throws Exception{
        if(isFull()) {
            throw new Exception( "Buffer full" );
        }
        buffer[head] = value;
        head = (head + 1) % capacity;

    }
    public int pop() throws Exception {
        if(isEmpty()) {
            throw new Exception( "Buffer empty" );
        }
        int index = tail;
        tail = (tail + 1) % capacity;
        return buffer[index];
    }

    public boolean isEmpty() {
        return head == tail;
    }
    public boolean isFull() {
        return (head + 1) % capacity == tail;
    }

    public String toString() {
        StringBuffer str = new StringBuffer(64);
        int tmphead = head;
        int tmptail = tail;
        while(tmptail != tmphead) {
            str.append((char) buffer[tmptail]);
            tmptail = (tmptail + 1) % capacity;
        }
        return str.toString();
    }

}
