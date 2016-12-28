
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Created by Novemser on 6/9/2016.
 */
public class FileTreeCellRenderer extends DefaultTreeCellRenderer {
    private ImageIcon fileIcon;
    private ImageIcon folderIcon;

    /**
     * Instantiates a new File tree cell renderer.
     */
    FileTreeCellRenderer() {
        fileIcon = new ImageIcon(FileTreeCellRenderer.class.getResource("/res/file.png"), "file");
        folderIcon = new ImageIcon(FileTableModel.class.getResource("/res/folder.png"), "Folder");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        FileDirectoryItem item = (FileDirectoryItem) node.getUserObject();
        switch (item.fileType) {
            case FILE:
                setIcon(fileIcon);
                break;
            case DIRECTORY:
                setIcon(folderIcon);
                break;
        }
        return this;
    }
}
