import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hugansen on 2016/6/6.
 */
public class FileDirectory implements Serializable {
    /**
     * The enum File type.
     */
    // 文件类型(文件或文件夹)
    public enum FILE_TYPE {
        /**
         * File file type.
         */
        FILE,
        /**
         * Directory file type.
         */
        DIRECTORY
    }

    /**
     * The File directory items.
     */
    // 目录文件
    public ArrayList<FileDirectoryItem> fileDirectoryItems = new ArrayList<>();
}
