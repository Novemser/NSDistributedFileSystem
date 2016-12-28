import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import utils.DataSaveLoadHelper;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;
import javax.swing.tree.*;

/**
 * The type Main ui.
 */
public class MainUI {

    private JFrame frame;
    private JTextField pathStringShow;
    private JTextField searchTextArea;
    private static JLabel labelRightBottom;
    private static JLabel labelItemsCount;
    /**
     * The Split pane.
     */
    public JSplitPane splitPane = new JSplitPane();
    private static JTable fileDispTable;
    private int selectedRowNumber;
    private JPopupMenu fileDispSpareAreaMenu;
    private JPopupMenu fileOrDirClickedPopMenu;
    private FileTableModel fileTableModel;
    private static VirtualRAMDisk virtualRAMDisk;
    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private JMenu menuItemFile;
    private TableRowSorter<FileTableModel> rowSorter;
    private FileDirectoryItem copyItem;

    private String filterText;
    private JMenuItem spacePaste;
    private String lastFolder;

    /**
     * The constant currentFolder.
     */
// 当前目录
    public static String currentFolder = "root";

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        // UI外观美化
        beautifyLookAndFeels();
        EventQueue.invokeLater(() -> {
            try {
                MainUI window = new MainUI();
                window.frame.setLocationRelativeTo(null);
                window.frame.setVisible(true);
                window.splitPane.setDividerLocation(0.2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Beautify look and feels.
     */
    public static void beautifyLookAndFeels() {
        try {
            BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike;
        UIManager.put("RootPane.setupButtonVisible", false);
        initGlobalFontSetting(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.PLAIN, 13));
    }

    /**
     * Init global font setting.
     *
     * @param fnt the fnt
     */
    public static void initGlobalFontSetting(Font fnt) {
        FontUIResource fontRes = new FontUIResource(fnt);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource)
                UIManager.put(key, fontRes);
        }
    }

    /**
     * Instantiates a new Main ui.
     */
    public MainUI() {
        initialize();
    }

    /**
     * Init virtual disk.
     */
    public void initVirtualDisk() {
        File file = new File("./VirtualDisk.dat");
        if (!file.exists()) {
            virtualRAMDisk = new VirtualRAMDisk();
            FileManager.createFileManager(virtualRAMDisk);
            // &#x521b;&#x5efa;root&#x6587;&#x4ef6;&#x5939;
            currentFolder = "/";
            FileManager.getFileManager().createFile("root", FileDirectory.FILE_TYPE.DIRECTORY, currentFolder);
            currentFolder = "root";
        } else {
            virtualRAMDisk = (VirtualRAMDisk) DataSaveLoadHelper.readObjectFromFile("VirtualDisk.dat");
            FileManager.createFileManager(virtualRAMDisk);
        }
    }

    private void initialize() {
        JPanel panelBg = new JPanel();
        initVirtualDisk();
        lastFolder = currentFolder;

        frame = new JFrame("\u6587\u4ef6\u7ba1\u7406\u7cfb\u7edf");
        frame.setBounds(100, 100, 1200, 781);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addKeyListener(keyAdapter);
        frame.addWindowListener(windowAdapter);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        menuItemFile = new JMenu("\u6587\u4ef6");
        menuBar.add(menuItemFile);

//        menuItemHelp = new JMenu("帮助");
//        menuBar.add(menuItemHelp);
//
//        menuItemAbout = new JMenu("关于");
//        menuBar.add(menuItemAbout);

        JButton btnBack = new JButton();
        btnBack.addActionListener(backToPreviousFolder);
        btnBack.setIcon(new ImageIcon(MainUI.class.getResource("/res/keyboard_return.png"), "keyboard_return"));
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setRolloverIcon(new ImageIcon(MainUI.class.getResource("/res/keyboard_return_ov.png"), "keyboard_return"));

        JButton btnForward = new JButton();
        btnForward.setIcon(new ImageIcon(MainUI.class.getResource("/res/keyboard_return_rev.png"), "keyboard_return"));
        btnForward.addActionListener(forwardFolder);
        btnForward.setBorderPainted(false);
        btnForward.setFocusPainted(false);
        btnForward.setContentAreaFilled(false);
        btnForward.setRolloverIcon(new ImageIcon(MainUI.class.getResource("/res/keyboard_return_rev_ov.png"), "keyboard_return"));
        initPopupMenu();

        pathStringShow = new JTextField();
        pathStringShow.setColumns(10);
        updatePathString();

        JButton btnSearch = new JButton(" ");
        btnSearch.setPreferredSize(new Dimension(20, 20));
        btnSearch.setIcon(new ImageIcon(MainUI.class.getResource("/res/search.png"), "search"));
        btnSearch.setBorderPainted(false);
        btnSearch.setFocusPainted(false);
        btnSearch.setContentAreaFilled(true);
        btnSearch.setContentAreaFilled(false);

        searchTextArea = new JTextField();
        searchTextArea.setColumns(10);
        Document document = searchTextArea.getDocument();
        document.addDocumentListener(documentListener);

        JPanel panel = new JPanel();

        // Main container
        GroupLayout groupLayout = new GroupLayout(panelBg);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                        .addComponent(splitPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 913, Short.MAX_VALUE)
                                        .addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 913, Short.MAX_VALUE)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addComponent(btnBack)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnForward)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addComponent(pathStringShow, GroupLayout.PREFERRED_SIZE, 587, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addComponent(btnSearch)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addComponent(searchTextArea, GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(btnBack)
                                        .addComponent(btnForward)
                                        .addComponent(pathStringShow, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnSearch)
                                        .addComponent(searchTextArea, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(splitPane, GroupLayout.PREFERRED_SIZE, 576, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                                .addContainerGap())
        );

        labelItemsCount = new JLabel("0 Items");
        labelRightBottom = new JLabel("Total:5120KB");
        labelRightBottom.setHorizontalAlignment(SwingConstants.RIGHT);
        updateFreeSpaceLabel();
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
                gl_panel.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_panel.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelItemsCount)
                                .addGap(759)
                                .addComponent(labelRightBottom, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                .addContainerGap())
        );
        gl_panel.setVerticalGroup(
                gl_panel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                                .addComponent(labelRightBottom, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                                .addComponent(labelItemsCount))
        );
        panel.setLayout(gl_panel);

        JScrollPane scrollPane = new JScrollPane();
        splitPane.setLeftComponent(scrollPane);

        // Item display table
        fileTableModel = new FileTableModel();
        fileDispTable = new JTable(fileTableModel);
        rowSorter = new TableRowSorter<>(fileTableModel);
        fileDispTable.setRowSorter(rowSorter);

        JScrollPane scrollPaneForFileDispTable = new JScrollPane(fileDispTable);
        fileDispTable.setFillsViewportHeight(true);
        fileDispTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileDispTable.setShowVerticalLines(false);
        fileDispTable.setAutoCreateRowSorter(false);
        fileDispTable.getRowSorter().toggleSortOrder(0);
        fileDispTable.getRowSorter().toggleSortOrder(0);
        splitPane.setRightComponent(scrollPaneForFileDispTable);
//        fileDispTable.setDefaultRenderer(Object.class, new ColorTableCellRenderer());

//        fileDispTable.getSelectionModel().addListSelectionListener(new RowListener());
        fileDispTable.addMouseListener(mouseAdapter);
        frame.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                updateFreeSpaceLabel();
            }
        });

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(FileManager.getFileManager().getFilesOfPath("/").get(0));
        treeModel = new DefaultTreeModel(root);
        initTreeModelView(root);

        fileTree = new JTree(treeModel);
        fileTree.setCellRenderer(new FileTreeCellRenderer());
        // Add tree Listener
        fileTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            showChildren(node);
            changeTableContent(node);
        });
        scrollPane.setViewportView(fileTree);

        panelBg.setLayout(groupLayout);
        frame.getContentPane().add(panelBg);
        resizeFileDispTable();
    }

    private RowFilter<FileTableModel, Integer> rowFilter = new RowFilter<FileTableModel, Integer>() {
        @Override
        public boolean include(Entry<? extends FileTableModel, ? extends Integer> entry) {
            // &#x6587;&#x4ef6;&#x540d;
            String fileName = entry.getStringValue(1);
            return fileName.contains(filterText);
        }
    };

    private DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            filterText = searchTextArea.getText();

            if (filterText.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(rowFilter);
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            filterText = searchTextArea.getText();

            if (filterText.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(rowFilter);
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    };

    private void changeTableContent(DefaultMutableTreeNode node) {
        FileDirectoryItem item = (FileDirectoryItem) node.getUserObject();
        currentFolder = item.fullPath;
        if (currentFolder.equals("/")) {
            fileTableModel.setFileDirectoryItems(FileManager.getFileManager().getFilesOfPath("root"));
            currentFolder = "root";
            updatePathString();
        } else {
            openFile(item.name);
        }
    }

    private void initTreeModelView(DefaultMutableTreeNode root) {
        ArrayList<FileDirectoryItem> items = FileManager.getFileManager().getFilesOfPath(currentFolder);
        for (FileDirectoryItem item : items) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
            root.add(node);
        }
    }

    /**
     * Update free space label.
     */
    public static void updateFreeSpaceLabel() {
        int cnt = 0;
        for (int i = 0; i < 10240; i++) {
            if (virtualRAMDisk.fileAllocationTable.FAT[i] == FileAllocationTable.FREE_SECTOR)
                cnt++;
        }
        labelRightBottom.setText("\u5df2\u7528:" + (5120 - cnt / 2) + "KB " + "\u7a7a\u95f2:" + String.valueOf(cnt / 2) + "KB " + "\u5171\u8ba1:5120KB");
        int itemCnt = FileManager.getFileManager().getFilesOfPath(currentFolder).size();
        labelItemsCount.setText(itemCnt + " \u9879");
    }

    private void updatePathString() {
        pathStringShow.setText("VirtualDisk:/" + currentFolder);
    }

    private void initPopupMenu() {
        JMenuItem spaceAddFile = new JMenuItem("\u65b0\u5efa\u6587\u4ef6");
        JMenuItem createFileItem = new JMenuItem("\u65b0\u5efa\u6587\u4ef6");

        spaceAddFile.addActionListener(createFileListener);
        createFileItem.addActionListener(createFileListener);
        spaceAddFile.setIcon(new ImageIcon(MainUI.class.getResource("/res/file.png"), "File"));
        createFileItem.setIcon(new ImageIcon(MainUI.class.getResource("/res/file.png"), "File"));

        JMenuItem spaceAddDir = new JMenuItem("\u65b0\u5efa\u6587\u4ef6\u5939");
        JMenuItem createDirItem = new JMenuItem("\u65b0\u5efa\u6587\u4ef6\u5939");
        spaceAddDir.setIcon(new ImageIcon(MainUI.class.getResource("/res/folder.png"), "Folder"));
        createDirItem.setIcon(new ImageIcon(MainUI.class.getResource("/res/folder.png"), "Folder"));
        spaceAddDir.addActionListener(createDirectoryListener);
        createDirItem.addActionListener(createDirectoryListener);

        spacePaste = new JMenuItem("\u7c98\u8d34");
        spacePaste.addActionListener(pasteListener);
        spacePaste.setEnabled(false);

        JMenuItem spaceAttr = new JMenuItem("\u5c5e\u6027");
        spaceAttr.addActionListener(openPropertyPanelListener);

        JMenuItem spaceFormat = new JMenuItem("\u683c\u5f0f\u5316");
        spaceFormat.addActionListener(formatDiskListener);

        fileDispSpareAreaMenu = new JPopupMenu();
        fileDispSpareAreaMenu.add(spaceAddFile);
        fileDispSpareAreaMenu.add(spaceAddDir);
        fileDispSpareAreaMenu.addSeparator();
        fileDispSpareAreaMenu.add(spacePaste);
        fileDispSpareAreaMenu.add(spaceAttr);
        fileDispSpareAreaMenu.add(spaceFormat);

        menuItemFile.add(createFileItem);
        menuItemFile.add(createDirItem);

        fileOrDirClickedPopMenu = new JPopupMenu();

        JMenuItem mntmOpen = new JMenuItem("\u6253\u5f00");
        mntmOpen.addActionListener(openFileListener);

        JMenuItem mntmDelete = new JMenuItem("\u5220\u9664");
        mntmDelete.addActionListener(deleteFileListener);

        JMenuItem mntmCopy = new JMenuItem("\u590d\u5236");
        mntmCopy.addActionListener(copyListener);

        JMenuItem mntmRename = new JMenuItem("\u91cd\u547d\u540d");
        mntmRename.addActionListener(renameFileListener);

        JMenuItem mntmAttr = new JMenuItem("\u5c5e\u6027");
        mntmAttr.addActionListener(openPropertyPanelListener);

        fileOrDirClickedPopMenu.add(mntmOpen);
        fileOrDirClickedPopMenu.addSeparator();
        fileOrDirClickedPopMenu.add(mntmCopy);
        fileOrDirClickedPopMenu.add(mntmRename);
        fileOrDirClickedPopMenu.add(mntmDelete);
        fileOrDirClickedPopMenu.addSeparator();
        fileOrDirClickedPopMenu.add(mntmAttr);
    }

    private WindowAdapter windowAdapter = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            // &#x5173;&#x95ed;&#x7a97;&#x53e3;&#xff0c;&#x5b58;&#x50a8;&#x786c;&#x76d8;
            DataSaveLoadHelper.writeObjectToFile(virtualRAMDisk, "VirtualDisk.dat");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
    };

    private MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            // &#x53cc;&#x51fb;&#x6253;&#x5f00;&#x6587;&#x4ef6;&#x3001;&#x76ee;&#x5f55;
            if (e.getButton() == MouseEvent.BUTTON1) {
                frame.requestFocus();
                if ((selectedRowNumber = fileDispTable.rowAtPoint(e.getPoint())) > -1 && e.getClickCount() == 2) {
//                    System.out.println("Row " + fileDispTable.rowAtPoint(e.getPoint()) + " Clicked twice.");
                    String fileName = (String) fileDispTable.getValueAt(selectedRowNumber, 1);
                    openFile(fileName);
                }
            }
            // &#x53f3;&#x952e;&#x8fdb;&#x884c;&#x64cd;&#x4f5c;
            else if (e.getButton() == MouseEvent.BUTTON3) {
                if ((selectedRowNumber = fileDispTable.rowAtPoint(e.getPoint())) > -1) {
                    fileOrDirClickedPopMenu.show(fileDispTable, e.getX(), e.getY());
                    System.out.println("Row selected:" + fileDispTable.rowAtPoint(e.getPoint()));
                } else {
                    fileDispSpareAreaMenu.show(fileDispTable, e.getX(), e.getY());
                    System.out.println("Spare space clicked");
                }
            }
        }
    };

    private KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                backToPreFolder();
            }
        }
    };

    private ActionListener createFileListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String fileName = JOptionPane.showInputDialog(frame, "\u8bf7\u8f93\u5165\u6587\u4ef6\u540d:");
            if (fileName == null || fileName.equals("")) {
                JOptionPane.showMessageDialog(frame, "\u6587\u4ef6\u540d\u4e0d\u80fd\u4e3a\u7a7a!", "\u6587\u4ef6\u540d\u4e3a\u7a7a", JOptionPane.ERROR_MESSAGE);
            } else {
                FileDirectoryItem newFile = FileManager.getFileManager().createFile(fileName, FileDirectory.FILE_TYPE.FILE, currentFolder);
                if (newFile == null) {
                    JOptionPane.showMessageDialog(frame, "\u78c1\u76d8\u7a7a\u95f4\u5df2\u6ee1\uff0c\u8bf7\u5c1d\u8bd5\u5220\u9664\u90e8\u5206\u6587\u4ef6\u540e\u518d\u8bd5\uff01", "\u7a7a\u95f4\u4e0d\u8db3", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                fileTableModel.setFileDirectoryItems(FileManager.getFileManager().getFilesOfPath(currentFolder));

                updateTreeNodes(newFile.name);

                updateFreeSpaceLabel();
            }
        }
    };

    private void updateTreeNodes(String fileName) {
        FileDirectoryItem newChild = FileManager.getFileManager().getFileDirectoryItem(fileName, currentFolder);
        FileDirectoryItem item = FileManager.getFileManager().getParent(newChild);
        TreePath path = findTreePath(item);
        DefaultMutableTreeNode parNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        treeModel.insertNodeInto(new DefaultMutableTreeNode(newChild), parNode, parNode.getChildCount());
        expandTree(fileTree);
    }

    private ActionListener createDirectoryListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String fileName = JOptionPane.showInputDialog(frame, "\u8bf7\u8f93\u5165\u6587\u5939\u4ef6\u540d:");
            if (fileName == null || fileName.equals("")) {
                JOptionPane.showMessageDialog(frame, "\u6587\u4ef6\u5939\u540d\u4e0d\u80fd\u4e3a\u7a7a!", "\u6587\u4ef6\u5939\u540d\u4e3a\u7a7a", JOptionPane.ERROR_MESSAGE);
            } else {
                FileDirectoryItem newFile = FileManager.getFileManager().createFile(fileName, FileDirectory.FILE_TYPE.DIRECTORY, currentFolder);
                if (newFile == null) {
                    JOptionPane.showMessageDialog(frame, "\u78c1\u76d8\u7a7a\u95f4\u5df2\u6ee1\uff0c\u8bf7\u5c1d\u8bd5\u5220\u9664\u90e8\u5206\u6587\u4ef6\u540e\u518d\u8bd5\uff01", "\u7a7a\u95f4\u4e0d\u8db3", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                fileTableModel.setFileDirectoryItems(FileManager.getFileManager().getFilesOfPath(currentFolder));

                updateTreeNodes(newFile.name);

                updateFreeSpaceLabel();
            }
        }
    };

    private ActionListener openFileListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
//            System.out.println("selectedRowNumber:" + selectedRowNumber);
            String fileName = (String) fileDispTable.getValueAt(selectedRowNumber, 1);
            openFile(fileName);
        }
    };

    private ActionListener renameFileListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String fileName = (String) fileDispTable.getValueAt(selectedRowNumber, 1);
            String newFileName = JOptionPane.showInputDialog(frame, "\u8bf7\u8f93\u5165\u6587\u4ef6\u540d:");
            if (newFileName == null || newFileName.equals("")) {
                JOptionPane.showMessageDialog(frame, "\u6587\u4ef6\u540d\u4e0d\u80fd\u4e3a\u7a7a\u54e6");
            } else {
                // &#x66f4;&#x65b0;&#x6587;&#x4ef6;&#x7cfb;&#x7edf;&#x4e2d;&#x7684;&#x6587;&#x4ef6;&#x540d;
                newFileName = FileManager.getFileManager().renameFile(fileName, newFileName, currentFolder);
                // &#x66f4;&#x65b0;&#x663e;&#x793a;&#x5217;&#x8868;&#x4e2d;&#x7684;&#x4fe1;&#x606f;
                fileDispTable.setValueAt(newFileName, selectedRowNumber, 1);
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                fileDispTable.setValueAt(formatter.format(date), selectedRowNumber, 3);
                fileTree.updateUI();
            }
        }
    };

    private ActionListener deleteFileListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String fileName = (String) fileDispTable.getValueAt(selectedRowNumber, 1);
            switch (JOptionPane.showConfirmDialog(frame, "\u4f60\u786e\u5b9a\u8981\u5220\u9664" + fileName + "\u5417?", "\u5220\u9664\uff1f", JOptionPane.YES_NO_OPTION)) {
                case 0:
                    FileDirectoryItem item = FileManager.getFileManager().getFileDirectoryItem(fileName, currentFolder);
                    TreePath path = findTreePath(item);
                    treeModel.removeNodeFromParent((MutableTreeNode) path.getLastPathComponent());

                    FileManager.getFileManager().deleteFile(fileName, currentFolder);
                    fileTableModel.setFileDirectoryItems(FileManager.getFileManager().getFilesOfPath(currentFolder));

                    break;
                case 1:
                    break;
            }
        }
    };

    private ActionListener formatDiskListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (JOptionPane.showConfirmDialog(frame, "\u4f60\u786e\u5b9a\u8981\u683c\u5f0f\u5316\u78c1\u76d8\u5417?", "\u683c\u5f0f\u5316\uff1f", JOptionPane.YES_NO_OPTION)) {
                case 0:
                    // &#x683c;&#x5f0f;&#x5316;
                    FileManager.getFileManager().formatDisk();
                    // &#x66f4;&#x65b0;UI
                    updateFreeSpaceLabel();
                    updatePathString();
                    // &#x66f4;&#x65b0;FileTableModel
                    fileTableModel.setFileDirectoryItems(FileManager.getFileManager().getFilesOfPath(currentFolder));
                    // &#x521b;&#x5efa;root&#x6587;&#x4ef6;&#x5939;
                    currentFolder = "/";
                    FileManager.getFileManager().createFile("root", FileDirectory.FILE_TYPE.DIRECTORY, currentFolder);
                    currentFolder = "root";
                    // &#x66f4;&#x65b0;FileTreeModel
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(FileManager.getFileManager().getFilesOfPath("/").get(0));
                    treeModel = new DefaultTreeModel(root);
                    fileTree.setModel(treeModel);
                    break;
                case 1:
                    break;
            }
        }
    };

    private ActionListener openPropertyPanelListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            FileDirectoryItem item;
            if (selectedRowNumber == -1) {
                int i = currentFolder.lastIndexOf("/");
                // &#x5982;&#x679c;&#x4e0d;&#x662f;root&#x6587;&#x4ef6;&#x5939;
                if (i != -1) {
                    // TODO:BUG..................
                    // TODO:DONE
                    item = FileManager.getFileManager().getFileDirectoryItem(currentFolder.substring(i + 1, currentFolder.length()), currentFolder.substring(0, i));
                } else {
                    item = FileManager.getFileManager().getFileDirectoryItem("root", "/");
                }
            } else {
                String fileName = (String) fileDispTable.getValueAt(selectedRowNumber, 1);
                item = FileManager.getFileManager().getFileDirectoryItem(fileName, currentFolder);
            }
            PropertyFrame propertyFrame = new PropertyFrame(item);
            propertyFrame.setVisible(true);
        }
    };

    private ActionListener backToPreviousFolder = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            backToPreFolder();
        }
    };

    private ActionListener forwardFolder = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            forwardFolder();
        }
    };

    private void forwardFolder() {
        currentFolder = lastFolder;
        fileTableModel.setFileDirectoryItems(FileManager.getFileManager().getFilesOfPath(currentFolder));
        updatePathString();
    }

    private ActionListener copyListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // &#x83b7;&#x53d6;&#x9009;&#x62e9;&#x7684;&#x6587;&#x4ef6;
            String fileName = (String) fileDispTable.getValueAt(selectedRowNumber, 1);
            copyItem = FileManager.getFileManager().getFileDirectoryItem(fileName, currentFolder);
            spacePaste.setEnabled(true);
        }
    };

    private ActionListener pasteListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // &#x5728;&#x5f53;&#x524d;&#x76ee;&#x5f55;&#x521b;&#x5efa;&#x590d;&#x5236;&#x7684;&#x6587;&#x4ef6;
            // &#x5c06;&#x6e90;&#x6587;&#x4ef6;&#x7684;&#x5185;&#x5bb9;&#x5199;&#x5165;&#x590d;&#x5236;&#x597d;&#x7684;&#x6587;&#x4ef6;
            FileDirectoryItem newFile = FileManager.getFileManager().pasteFile(copyItem, currentFolder);
            if (newFile == null) {
                JOptionPane.showMessageDialog(frame, "\u5b58\u50a8\u7a7a\u95f4\u4e0d\u8db3\uff01");
                return;
            }
            fileTableModel.setFileDirectoryItems(FileManager.getFileManager().getFilesOfPath(currentFolder));

            updateTreeNodes(newFile.name);

            updateFreeSpaceLabel();
        }
    };

    private void backToPreFolder() {
        lastFolder = currentFolder;
        int i = currentFolder.lastIndexOf("/");
        if (i != -1) {
            currentFolder = currentFolder.substring(0, i);
            fileTableModel.setFileDirectoryItems(FileManager.getFileManager().getFilesOfPath(currentFolder));
            updatePathString();
        }
    }

    private void openFile(String fileName) {
        String temp = currentFolder;
        currentFolder = FileManager.getFileManager().openFile(fileName, currentFolder);
        if (!temp.equals(currentFolder)) {

            FileDirectoryItem next = FileManager.getFileManager().getFileDirectoryItem(fileName, temp);
            TreePath path = findTreePath(next);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            showChildren(node);
            fileTree.updateUI();

            fileTableModel.setFileDirectoryItems(FileManager.getFileManager().getFilesOfPath(currentFolder));
            updatePathString();
        }
    }

    private void resizeFileDispTable() {
        TableColumn column;
        for (int i = 0; i < fileDispTable.getColumnCount(); i++) {
            column = fileDispTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setMinWidth(0);
                column.setPreferredWidth(5);
            } else if (i == 1)
                column.setPreferredWidth(150);
            else if (i == 2)
                column.setPreferredWidth(200);
            else if (i == 3)
                column.setPreferredWidth(65);
        }
    }

    private TreePath findTreePath(FileDirectoryItem find) {
        for (int ii = 0; ii < fileTree.getRowCount(); ii++) {
            TreePath treePath = fileTree.getPathForRow(ii);
            Object object = treePath.getLastPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
            FileDirectoryItem nodeFile = (FileDirectoryItem) node.getUserObject();

            if (nodeFile == find) {
                return treePath;
            }
        }
        // not found!
        return null;
    }

    private void showChildren(DefaultMutableTreeNode node) {
        fileTree.setEditable(false);
        if (node == null)
            return;
        FileDirectoryItem item = (FileDirectoryItem) node.getUserObject();
        if (item == null)
            return;
        if (item.fileType == FileDirectory.FILE_TYPE.DIRECTORY) {
            ArrayList<FileDirectoryItem> items = FileManager.getFileManager().getFilesOfPath(item.fullPath + "/" + item.name);
//            System.out.println("ShowChildren, isLeaf of " + item.name + " " + node.isLeaf() + " item:" + items.size());
            if (node.isLeaf()) {
                for (FileDirectoryItem child : items) {
                    node.add(new DefaultMutableTreeNode(child));
                }
            }
        }
        expandTree(fileTree);
    }

    /**
     * Expand tree.
     *
     * @param tree the tree
     */
    public static void expandTree(JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root), true);
    }


    private static void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }
}
