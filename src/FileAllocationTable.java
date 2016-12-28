import java.io.Serializable;

/**
 * Created by Novemser on 6/5/2016.
 */
public class FileAllocationTable implements Serializable {

    /**
     * The Fat 10240items.
     */
    public int[] FAT = new int[10240];
    /**
     * The constant END_OF_SECTOR.
     */
    public static final int END_OF_SECTOR = 0xffffffff;
    /**
     * The constant FREE_SECTOR.
     */
    public static final int FREE_SECTOR = 0x00000000;
    /**
     * The constant FAT_ITEM_SIZE.
     */
    public static int FAT_ITEM_SIZE = 10240;

    /**
     * Gets first free sector.
     *
     * @return the first free sector
     */
    public int getFirstFreeSector() {
        for (int i = 0; i < FAT_ITEM_SIZE; i++) {
            if (FAT[i] == FREE_SECTOR)
                return i;
        }
        return -1;
    }

    /**
     * Gets next free sector.
     *
     * @param index the index
     * @return the next free sector
     */
    public int getNextFreeSector(int index) {
        index++;
        for (int i = index; i < FAT_ITEM_SIZE; i++) {
            if (FAT[i] == FREE_SECTOR)
                return i;
        }
        return -1;
    }

    /**
     * Gets free sector total num.
     *
     * @return the free sector total num
     */
    public int getFreeSectorTotalNum() {
        int cnt = 0;
        for (int i = 0; i < FAT_ITEM_SIZE; i++) {
            if (FAT[i] == FREE_SECTOR)
                cnt++;
        }
        return cnt;
    }
}
