import sun.applet.Main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * The type File manager.
 */
public class FileManager {
    /**
     * The constant fileManager.
     */
    public static FileManager fileManager = null;
    /**
     * The Virtual ram disk.
     */
    public VirtualRAMDisk virtualRAMDisk;
    /**
     * The File directory.
     */
    public FileDirectory fileDirectory;
    /**
     * The File allocation table.
     */
    public FileAllocationTable fileAllocationTable;

    private FileManager() {
    }

    private FileManager(VirtualRAMDisk virtualRAMDisk) {
        this.virtualRAMDisk = virtualRAMDisk;
        fileDirectory = virtualRAMDisk.fileDirectory;
        fileAllocationTable = virtualRAMDisk.fileAllocationTable;
    }

    /**
     * Create file manager file manager.
     *
     * @param virtualRAMDisk the virtual ram disk
     * @return the file manager
     */
    public static FileManager createFileManager(VirtualRAMDisk virtualRAMDisk) {
        return fileManager = new FileManager(virtualRAMDisk);
    }

    /**
     * Gets file manager.
     *
     * @return the file manager
     */
    public static FileManager getFileManager() {
        if (null == fileManager) {
            fileManager = new FileManager();
        }

        return fileManager;
    }

    /**
     * Create file file directory item.
     *
     * @param fileName the file name
     * @param type     the type
     * @param path     the path
     * @return the file directory item
     */
    public FileDirectoryItem createFile(String fileName, FileDirectory.FILE_TYPE type, String path) {
        // &#x904d;&#x5386;&#x5f53;&#x524d;&#x76ee;&#x5f55;&#xff0c;&#x770b;&#x662f;&#x5426;&#x6709;&#x91cd;&#x540d;&#x7684;&#x6587;&#x4ef6;
        for (FileDirectoryItem item : fileDirectory.fileDirectoryItems) {
            if (item.name.equals(fileName) && (item.fullPath.equals(path))) {
                fileName += "-\u526f\u672c";
            }
        }
        // &#x6ca1;&#x6709;&#x91cd;&#x540d;&#x6587;&#x4ef6;&#xff0c;&#x5c1d;&#x8bd5;&#x521b;&#x5efa;
        // &#x627e;&#x5230;&#x7b2c;&#x4e00;&#x4e2a;&#x7a7a;&#x95f2;&#x7684;FAT&#x9879;
        int i = fileAllocationTable.getFirstFreeSector();
        // &#x5982;&#x679c;&#x7a7a;&#x95f4;&#x8db3;&#x591f;&#xff0c;&#x521b;&#x5efa;&#x6210;&#x529f;
        if (i != -1) {
            FileDirectoryItem item = new FileDirectoryItem();
            item.name = fileName;
            item.fullPath = path;
            item.fileType = type;
            item.fatStartIndex = i;

            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            item.lastModifiedTime = formatter.format(date);
            item.sizeInByte = 0;

            fileAllocationTable.FAT[i] = FileAllocationTable.END_OF_SECTOR;
            fileDirectory.fileDirectoryItems.add(item);
            virtualRAMDisk.getSectors()[i].setData("");

            return item;
        }
        // &#x5426;&#x5219;&#x521b;&#x5efa;&#x5931;&#x8d25;
        return null;
    }

    /**
     * Paste file file directory item.
     *
     * @param srcItem the src item
     * @param path    the path
     * @return the file directory item
     */
    public FileDirectoryItem pasteFile(FileDirectoryItem srcItem, String path) {

        if (srcItem.fileType.equals(FileDirectory.FILE_TYPE.FILE)) {
            FileDirectoryItem newFile = createFile(srcItem.name, srcItem.fileType, path);
            if (newFile == null)
                return newFile;
            StringBuilder content = getFileContent(srcItem);
            int res = saveContentToFile(newFile, content.toString());
            if (res == -1) {
                deleteFile(newFile.name, path);
                return null;
            }
            return newFile;
        } else {
            ArrayList<FileDirectoryItem> items = getFilesOfPath(srcItem.fullPath + "/" + srcItem.name);
            FileDirectoryItem newFile = createFile(srcItem.name, srcItem.fileType, path);
            if (newFile == null)
                return null;

            for (FileDirectoryItem item : items) {
                    pasteFile(item, newFile.fullPath + "/" + newFile.name);
            }
            return newFile;
        }
    }

    /**
     * Rename file string.
     *
     * @param oldFileName   the old file name
     * @param newFileName   the new file name
     * @param currentFolder the current folder
     * @return the string
     */
    public String renameFile(String oldFileName, String newFileName, String currentFolder) {
        // &#x904d;&#x5386;&#x5f53;&#x524d;&#x76ee;&#x5f55;&#xff0c;&#x770b;&#x662f;&#x5426;&#x6709;&#x91cd;&#x540d;&#x7684;&#x6587;&#x4ef6;
        for (FileDirectoryItem item : fileDirectory.fileDirectoryItems) {
            if (item.name.equals(newFileName)) {
                newFileName += "-\u526f\u672c";
            }
        }
        // &#x83b7;&#x53d6;&#x6587;&#x4ef6;&#x76ee;&#x5f55;&#x8868;&#x9879;
        FileDirectoryItem item = FileManager.getFileManager().getFileDirectoryItem(oldFileName, currentFolder);
        item.name = newFileName;
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        item.lastModifiedTime = formatter.format(date);
        // &#x5982;&#x679c;&#x662f;&#x6587;&#x4ef6;&#xff0c;&#x4fee;&#x6539;&#x7ed3;&#x675f;
        // &#x5982;&#x679c;&#x662f;&#x6587;&#x4ef6;&#x5939;&#xff0c;&#x9012;&#x5f52;&#x4fee;&#x6539;
        if (item.fileType == FileDirectory.FILE_TYPE.DIRECTORY) {
            String oldDirPath = currentFolder + "/" + oldFileName;
            String newDirPath = currentFolder + "/" + newFileName;
            renameSubDirFiles(oldDirPath, newDirPath);
        }
        return newFileName;
    }

    private void renameSubDirFiles(String oldDirPath, String newDirPath) {
        for (FileDirectoryItem item : fileDirectory.fileDirectoryItems) {
            if (item.fullPath.equals(oldDirPath)) {
                item.fullPath = newDirPath;
                if (item.fileType == FileDirectory.FILE_TYPE.DIRECTORY) {
                    // &#x9012;&#x5f52;&#x91cd;&#x547d;&#x540d;
                    renameSubDirFiles(oldDirPath + "/" + item.name, newDirPath + "/" + item.name);
                }
            }
        }
    }

    /**
     * Delete file.
     *
     * @param fileName      the file name
     * @param currentFolder the current folder
     */
    public void deleteFile(String fileName, String currentFolder) {
        // &#x83b7;&#x53d6;&#x6587;&#x4ef6;&#x76ee;&#x5f55;&#x8868;&#x9879;
        FileDirectoryItem item = FileManager.getFileManager().getFileDirectoryItem(fileName, currentFolder);
        // &#x9012;&#x5f52;&#x5220;&#x9664;&#x5b8c;&#x6210;
        if (item == null)
            return;

        // &#x66f4;&#x6539;&#x5176;FAT&#x8868;
        int pre = item.fatStartIndex;
        int next = pre;
        while (fileAllocationTable.FAT[next] != FileAllocationTable.END_OF_SECTOR) {
            next = fileAllocationTable.FAT[next];
            fileAllocationTable.FAT[pre] = FileAllocationTable.FREE_SECTOR;
            pre = next;
        }
        fileAllocationTable.FAT[next] = FileAllocationTable.FREE_SECTOR;
        // &#x4ece;&#x76ee;&#x5f55;&#x6587;&#x4ef6;&#x4e2d;&#x5220;&#x9664;&#x76f8;&#x5e94;&#x9879;
        for (int i = 0; i < fileDirectory.fileDirectoryItems.size(); i++) {
            FileDirectoryItem temp = fileDirectory.fileDirectoryItems.get(i);
            if (temp.name.equals(fileName) && temp.fullPath.equals(currentFolder)) {
                fileDirectory.fileDirectoryItems.remove(i);
                break;
            }
        }
        // 1.&#x5982;&#x679c;&#x662f;&#x6587;&#x4ef6;
        if (item.fileType == FileDirectory.FILE_TYPE.FILE) {
            // &#x5220;&#x9664;&#x5b8c;&#x6bd5;&#xff0c;&#x8fd4;&#x56de;
            return;
        } else {
            // 2.&#x5982;&#x679c;&#x662f;&#x6587;&#x4ef6;&#x5939;
            currentFolder += "/" + fileName;
            // &#x9012;&#x5f52;&#x5220;&#x9664;&#x6240;&#x6709;&#x6587;&#x4ef6;
            for (int i = 0; i < fileDirectory.fileDirectoryItems.size(); i++) {
                if (fileDirectory.fileDirectoryItems.get(i).fullPath.equals(currentFolder)) {
                    deleteFile(fileDirectory.fileDirectoryItems.get(i).name, currentFolder);
                    i--;
                }
            }
        }
    }

    /**
     * Open file string.
     *
     * @param fileName      the file name
     * @param currentFolder the current folder
     * @return the string
     */
    public String openFile(String fileName, String currentFolder) {
        // &#x83b7;&#x53d6;&#x6587;&#x4ef6;&#x76ee;&#x5f55;&#x8868;&#x9879;
        FileDirectoryItem item = FileManager.getFileManager().getFileDirectoryItem(fileName, currentFolder);
        // 1. &#x5982;&#x679c;&#x662f;&#x6587;&#x4ef6;
        if (item.fileType == FileDirectory.FILE_TYPE.FILE) {
            // &#x627e;&#x5230;&#x6587;&#x4ef6;&#x5b58;&#x50a8;&#x7684;&#x6247;&#x533a;&#xff0c;&#x5e76;&#x5bf9;&#x6247;&#x533a;&#x5185;&#x7684;&#x6570;&#x636e;&#x8fdb;&#x884c;&#x83b7;&#x53d6;&#x3001;&#x62fc;&#x63a5;
            StringBuilder content = getFileContent(item);
            // &#x6253;&#x5f00;&#x6587;&#x4ef6;&#x7f16;&#x8f91;&#x5668;&#xff0c;&#x663e;&#x793a;&#x62fc;&#x63a5;&#x597d;&#x7684;&#x6570;&#x636e;
            FileEditor fileEditor = new FileEditor(fileName);
            fileEditor.setVisible(true);
            fileEditor.setTextContent(content.toString());
            fileEditor.setCurrentFileDirectoryItem(item);
        }
        // 2. &#x5982;&#x679c;&#x662f;&#x5b50;&#x76ee;&#x5f55;
        else {
            currentFolder += "/" + item.name;

        }

        return currentFolder;
    }

    /**
     * Gets file content.
     *
     * @param item the item
     * @return the file content
     */
    public StringBuilder getFileContent(FileDirectoryItem item) {
        StringBuilder content = new StringBuilder();
        // &#x627e;&#x5230;&#x6587;&#x4ef6;&#x5b58;&#x50a8;&#x7684;&#x6247;&#x533a;&#xff0c;&#x5e76;&#x5bf9;&#x6247;&#x533a;&#x5185;&#x7684;&#x6570;&#x636e;&#x8fdb;&#x884c;&#x83b7;&#x53d6;&#x3001;&#x62fc;&#x63a5;
        int fatIndex = item.fatStartIndex;
        Sector[] sectors = virtualRAMDisk.getSectors();
        content.append(sectors[fatIndex].getData());
        while (fileAllocationTable.FAT[fatIndex] != FileAllocationTable.END_OF_SECTOR) {
            fatIndex = fileAllocationTable.FAT[fatIndex];
            content.append(sectors[fatIndex].getData());
        }

        return content;
    }


    /**
     * Gets file directory item.
     *
     * @param fileName the file name
     * @param path     the path
     * @return the file directory item
     */
    public FileDirectoryItem getFileDirectoryItem(String fileName, String path) {
        FileDirectoryItem i = null;
        for (FileDirectoryItem item : fileDirectory.fileDirectoryItems) {
            if (item.name.equals(fileName) && item.fullPath.equals(path)) {
                i = item;
                break;
            }
        }
        return i;
    }

    /**
     * Gets files of path.
     *
     * @param path the path
     * @return the files of path
     */
    public ArrayList<FileDirectoryItem> getFilesOfPath(String path) {
        return fileDirectory.fileDirectoryItems.stream().
                filter(item -> item.fullPath.equals(path)).
                collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Save content to file int.
     *
     * @param item    the item
     * @param content the content
     * @return the int
     */
    public int saveContentToFile(FileDirectoryItem item, String content) {
        if (item == null || content == null)
            return -1;

        int fatIndex;
        // &#x5982;&#x679c;&#x5b57;&#x7b26;&#x4e32;&#x4e3a;&#x7a7a;&#xff0c;&#x5219;&#x8986;&#x76d6;&#x76f8;&#x5e94;&#x6247;&#x533a;&#xff0c;&#x5e76;&#x4e14;&#x5c06;&#x6240;&#x6709;&#x5360;&#x7528;&#x6247;&#x533a;&#x8bbe;&#x7f6e;&#x4e3a;&#x7a7a;
        if (content.equals("")) {
            deleteItemSectors(item);
            item.fatStartIndex = fileAllocationTable.getFirstFreeSector();
            fileAllocationTable.FAT[item.fatStartIndex] = FileAllocationTable.END_OF_SECTOR;
            virtualRAMDisk.getSectors()[item.fatStartIndex].setData("");
            return 0;
        }
        // &#x5426;&#x5219;&#xff0c;&#x5c06;&#x6240;&#x6709;&#x5360;&#x7528;&#x6247;&#x533a;&#x8bbe;&#x7f6e;&#x4e3a;&#x7a7a;&#xff0c;&#x4e4b;&#x540e;&#x518d;&#x5199;&#x5165;
        else {
            // &#x7a7a;&#x95f4;&#x4e0d;&#x8db3;
            if (fileAllocationTable.getFreeSectorTotalNum() < Math.ceil(content.length() * 1.0 / 512))
                return -1;
            deleteItemSectors(item);
            // &#x6bcf;&#x4e2a;&#x6247;&#x533a;&#x5199;&#x5165;512&#x5b57;&#x8282;&#xff0c;&#x5373;&#x957f;&#x5ea6;&#x4e3a;512&#x7684;string
            fatIndex = -1;
            for (int i = 0; i < content.length(); i += 511) {
                int j = Math.min(i + 511, content.length());
                String temp = content.substring(i, j);
                // &#x627e;&#x5230;&#x7a7a;&#x95f2;&#x8868;
                fatIndex = fileAllocationTable.getNextFreeSector(fatIndex);
                if (i == 1) {
                    item.fatStartIndex = fatIndex;
                }
                if (fatIndex == -1) {
                    // &#x5b58;&#x50a8;&#x7a7a;&#x95f4;&#x4e0d;&#x8db3;&#xff01;
                    return -1;
                }

                // &#x5199;&#x5165;&#x6247;&#x533a;
                virtualRAMDisk.getSectors()[fatIndex].setData(temp);
                // &#x5982;&#x679c;&#x5199;&#x5b8c;&#x4e86;
                if (i + 511 > content.length()) {
                    fileAllocationTable.FAT[fatIndex] = FileAllocationTable.END_OF_SECTOR;
                    item.sizeInByte = content.length();

                    Calendar calendar = Calendar.getInstance();
                    Date date = calendar.getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    item.lastModifiedTime = formatter.format(date);
                    return 0;
                } else {
                    fileAllocationTable.FAT[fatIndex] = fileAllocationTable.getNextFreeSector(fatIndex);
                }
            }
        }

        return -1;
    }

    /**
     * Delete item sectors.
     *
     * @param item the item
     */
    public void deleteItemSectors(FileDirectoryItem item) {
        if (item == null)
            return;

        int pre = item.fatStartIndex;
        int next = pre;
        while (fileAllocationTable.FAT[next] != FileAllocationTable.END_OF_SECTOR) {
            next = fileAllocationTable.FAT[next];
            fileAllocationTable.FAT[pre] = FileAllocationTable.FREE_SECTOR;
            pre = next;
        }
        fileAllocationTable.FAT[next] = FileAllocationTable.FREE_SECTOR;
    }

    /**
     * Format disk.
     */
    public void formatDisk() {
        for (int i = 0; i < fileAllocationTable.FAT.length; i++)
            fileAllocationTable.FAT[i] = FileAllocationTable.FREE_SECTOR;
        fileDirectory.fileDirectoryItems.clear();
    }

    /**
     * Cal file size int.
     *
     * @param item the item
     * @return the int
     */
    public int calFileSize(FileDirectoryItem item) {
        if (item.fileType == FileDirectory.FILE_TYPE.FILE)
            return item.sizeInByte;

        ArrayList<FileDirectoryItem> items = getFilesOfPath(item.fullPath + "/" + item.name);
        if (item.fullPath.equals("/"))
            items = getFilesOfPath("root");
        int count = 0;
        for (FileDirectoryItem directoryItem : items) {
            count += calFileSize(directoryItem);
        }
        return count;
    }

    /**
     * Gets parent.
     *
     * @param item the item
     * @return the parent
     */
    public FileDirectoryItem getParent(FileDirectoryItem item) {
        String[] parents = item.fullPath.split("/");
        if (parents.length == 1)
            return getFileDirectoryItem("root", "/");
        if (parents.length > 1) {
            String s1 = item.fullPath.substring(0, item.fullPath.lastIndexOf("/"));
            return getFileDirectoryItem(parents[parents.length - 1], s1);
        }
        return null;
    }
}
