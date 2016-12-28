import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Created by Novemser on 6/8/2016.
 */
public class PropertyFrame extends JFrame {
	private JPanel contentPane;
	private JTextField textField;
    private JFrame frame;

	/**
	 * Create the frame.
	 *
	 * @param item the item
	 */
	public PropertyFrame(FileDirectoryItem item) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 346, 407);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);

		JLabel imgIcon = new JLabel();

		textField = new JTextField();
		textField.setColumns(10);
        textField.setText(item.name);
		textField.setEditable(false);
        setLocationRelativeTo(null);

		JSeparator separator = new JSeparator();

		JLabel lblType = new JLabel("\u7C7B\u578B:");

		JLabel label = new JLabel("\u4F4D\u7F6E:");

		JLabel label_1 = new JLabel("\u5927\u5C0F:");

		JLabel label_2 = new JLabel("\u5360\u7528\u7A7A\u95F4:");

		JLabel lblNewLabel = new JLabel("\u4FEE\u6539\u65E5\u671F:");

		JSeparator separator_1 = new JSeparator();

		JLabel label_3 = new JLabel("\u8D77\u59CB\u5757:");

		JLabel occupySpaceText = new JLabel(String.valueOf((int) Math.ceil(FileManager.getFileManager().calFileSize(item) * 1.0 / 512) * 512) + " Byte");

		JLabel sizeText = new JLabel(String.valueOf(FileManager.getFileManager().calFileSize(item)) + " Byte");

		JLabel locationText = new JLabel(item.fullPath);

		JLabel typeText = new JLabel("type");
        switch (item.fileType) {
            case FILE:
                imgIcon.setIcon(new ImageIcon(FileTableModel.class.getResource("/res/file_big.png"), "file"));
                typeText.setText("\u6587\u672c\u6587\u4ef6");
                break;
            case DIRECTORY:
                imgIcon.setIcon(new ImageIcon(FileTableModel.class.getResource("/res/folder_big.png"), "Folder"));
                typeText.setText("\u6587\u4ef6\u5939");
                break;
        }

		JLabel modifyTimeText = new JLabel(item.lastModifiedTime);

		JLabel startBlockText = new JLabel(String.valueOf(item.fatStartIndex));

		JButton btnConfirm = new JButton("\u786e\u5b9a");

		JButton btnCancel = new JButton("\u53d6\u6d88");

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(20)
							.addComponent(imgIcon, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, 231, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(separator, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
								.addGroup(gl_panel.createSequentialGroup()
									.addGap(10)
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addComponent(label_2)
										.addComponent(label_1)
										.addComponent(label)
										.addComponent(lblType))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addComponent(typeText)
										.addComponent(locationText)
										.addComponent(sizeText)
										.addComponent(occupySpaceText)))))
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(separator_1, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
								.addGroup(gl_panel.createSequentialGroup()
									.addGap(10)
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addComponent(lblNewLabel)
										.addComponent(label_3))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addComponent(startBlockText)
										.addComponent(modifyTimeText))))))
					.addContainerGap())
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap(130, Short.MAX_VALUE)
					.addComponent(btnConfirm)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCancel)
					.addGap(16))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(33)
							.addComponent(imgIcon, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(49)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(18)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblType)
						.addComponent(typeText))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(locationText))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_1)
						.addComponent(sizeText))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_2)
						.addComponent(occupySpaceText))
					.addGap(18)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 3, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(modifyTimeText))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_3)
						.addComponent(startBlockText))
					.addPreferredGap(ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnConfirm)
						.addComponent(btnCancel))
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
        pack();
        frame = this;
        addWindowListener(closeAdapter);
        btnCancel.addActionListener(close);
        btnConfirm.addActionListener(close);
        
	}

    private ActionListener close = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    };

    private WindowAdapter closeAdapter = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            frame.dispose();
        }
    };
}
