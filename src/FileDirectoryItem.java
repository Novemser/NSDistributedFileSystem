import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Novemser on 6/5/2016.
 */
public class FileDirectoryItem implements Serializable {

    /**
     * The Name.
     */
    public String name;
    /**
     * The Full path.
     */
    public String fullPath;
    /**
     * The Last modified time.
     */
    public String lastModifiedTime;

    /**
     * The Size in byte.
     */
    public int sizeInByte;
    /**
     * The Fat start index.
     */
    public int fatStartIndex;

    /**
     * The File type.
     */
    public FileDirectory.FILE_TYPE fileType;

    /**
     * Instantiates a new File directory item.
     */
    FileDirectoryItem() {

    }

    @Override
    public String toString() {
        return name;
    }
}
