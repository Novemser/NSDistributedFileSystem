import java.io.Serializable;

/**
 * Created with OSAssignment3
 * User:Novemser
 * Date:6/5/2016
 * Time:22:16
 */
public class VirtualRAMDisk implements Serializable {
    // &#x786c;&#x76d8;&#x4e00;&#x5171;10240&#x5757;&#xff0c;&#x5408;&#x8ba1;5MB
    private Sector[] sectors;
    private int totSize;
    /**
     * The File allocation table.
     */
    public FileAllocationTable fileAllocationTable;
    /**
     * The File directory.
     */
    public FileDirectory fileDirectory;

    /**
     * Instantiates a new Virtual ram disk.
     */
    public VirtualRAMDisk() {
        sectors = new Sector[10240];
        totSize = 5 * 1024 * 1024;
        for (int i = 0; i < 10240; i++) {
            sectors[i] = new Sector();
        }
        fileAllocationTable = new FileAllocationTable();
        fileDirectory = new FileDirectory();
    }

    /**
     * Get sectors sector [ ].
     *
     * @return the sector [ ]
     */
    public Sector[] getSectors() {
        return sectors;
    }

    /**
     * Gets tot size.
     *
     * @return the tot size
     */
    public int getTotSize() {
        return totSize;
    }

}
