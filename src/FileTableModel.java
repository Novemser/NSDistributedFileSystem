import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Novemser on 6/5/2016.
 */
public class FileTableModel extends AbstractTableModel {
    private ArrayList<FileDirectoryItem> fileDirectoryItems;

    private String[] columns = {
            "",
            "\u6587\u4ef6\u540d",
            "\u8def\u5f84",
            "\u4fee\u6539\u65e5\u671f",
            "\u5927\u5c0f"
    };

    /**
     * Instantiates a new File table model.
     */
    FileTableModel() {
        String currentPath = MainUI.currentFolder;
        System.out.println("CP:"+currentPath);
        fileDirectoryItems = FileManager.getFileManager().getFilesOfPath(currentPath);
    }

    @Override
    public int getRowCount() {
        return fileDirectoryItems.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FileDirectoryItem item = fileDirectoryItems.get(rowIndex);
        switch (columnIndex) {
            case 0:
                if (item.fileType == FileDirectory.FILE_TYPE.FILE) {
                    return new ImageIcon(FileTableModel.class.getResource("/res/file.png"), "file");
                } else {
                    return new ImageIcon(FileTableModel.class.getResource("/res/folder.png"), "Folder");
                }
            case 1:
                return item.name;
            case 2:
                return item.fullPath;
            case 3:
                return item.lastModifiedTime;
            case 4:
                return FileManager.getFileManager().calFileSize(item) + " Bytes";
            default:
                System.out.println("Invalid col id");
        }
        return "\u6570\u636e\u9519\u8bef";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return ImageIcon.class;
            case 1:
            case 2:
            case 3:
            case 4:
                return String.class;
        }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        FileDirectoryItem item = fileDirectoryItems.get(rowIndex);
        switch (columnIndex) {
            case 0:
                break;
            case 1:
                item.name = (String) aValue;
                break;
            case 2:
                item.fullPath = (String) aValue;
                break;
            case 3:
                item.lastModifiedTime = (String) aValue;
                break;
            case 4:
                item.sizeInByte = (int) aValue;
                break;
            default:
                System.out.println("Invalid col id");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Sets file directory items.
     *
     * @param items the items
     */
    public void setFileDirectoryItems(ArrayList<FileDirectoryItem> items) {
        fileDirectoryItems = items;
        fireTableDataChanged();
    }

}
