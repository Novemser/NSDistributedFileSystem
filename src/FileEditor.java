import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by hugansen on 2016/6/6.
 */
public class FileEditor extends JFrame {
    private int width = 600;
    private int height = 600;
    private JTextArea textArea;
    private JFrame frame;
    private FileDirectoryItem currentFileDirectoryItem;

    /**
     * Instantiates a new File editor.
     *
     * @param name the name
     */
    public FileEditor(String name) {
        super("\u6587\u672c\u7f16\u8f91\u5668--" + name);
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        setSize(width, height);
        setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(saveAdapter);
        frame = this;
        frame.setLocationRelativeTo(null);
    }

    /**
     * Sets current file directory item.
     *
     * @param currentFileDirectoryItem the current file directory item
     */
    public void setCurrentFileDirectoryItem(FileDirectoryItem currentFileDirectoryItem) {
        this.currentFileDirectoryItem = currentFileDirectoryItem;
    }

    /**
     * Sets text content.
     *
     * @param content the content
     */
    public void setTextContent(String content) {
        textArea.setText(content);
    }

    private WindowAdapter saveAdapter = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            int option = JOptionPane.showConfirmDialog(frame, "\u4fdd\u5b58\u6587\u4ef6\u5417\uff1f");
            switch (option) {
                case 0:
                    int res = FileManager.getFileManager().saveContentToFile(currentFileDirectoryItem, textArea.getText());
                    if (res == -1) {
                        JOptionPane.showMessageDialog(frame, "\u5b58\u50a8\u7a7a\u95f4\u4e0d\u8db3\uff01");
                        break;
                    }
                case 1:
                    MainUI.updateFreeSpaceLabel();
                    frame.dispose();
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }
    };
}
