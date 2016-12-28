import java.io.Serializable;

/**
 * Created by Novemser on 6/5/2016.
 */
public class Sector implements Serializable {
    // 每一块大小是512Byte
    private int sizeInByte = 512;
    private Object data;
    private boolean isOccupied;

    /**
     * Gets data.
     *
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * Gets size in byte.
     *
     * @return the size in byte
     */
    public int getSizeInByte() {
        return sizeInByte;
    }

    /**
     * Sets data.
     *
     * @param data the data
     * @return the data
     */
    public Object setData(Object data) {
        this.data = data;
        setOccupied(true);
        return data;
    }

    /**
     * Sets occupied.
     *
     * @param occupied the occupied
     */
    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    /**
     * Clear data object.
     *
     * @return the object
     */
    public Object clearData() {
        Object tmp = data;
        this.data = null;
        setOccupied(false);
        return tmp;
    }
}
