package gui;

import main.PhotoCheck;
import model.DataModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainGUI extends JFrame {

    private JProgressBar progress = new JProgressBar();
    private JTextField tfFolderPath = new JTextField();
    private JTextField tfGray = new JTextField();
    private JTextField tfColor = new JTextField();
    private JButton btnTarget = new JButton("Browse");
    private JButton btnRun = new JButton("Run");
    private DataModel dm;
    private JFileChooser jfc = new JFileChooser(".");


    public MainGUI(DataModel dm) {

        this.dm = dm;

        initComponents();

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        pack();
        setLocation((int) ((Toolkit.getDefaultToolkit().getScreenSize().getWidth() - getWidth()) / 2),
                (int) ((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - getHeight()) / 2));
//        setResizable(false);
        setVisible(true);
    }

    private void initComponents() {
        JLabel lblFolder = new JLabel("Photos folder");
        JLabel lblGray = new JLabel("Greyscale folder");
        JLabel lblColor = new JLabel("Color folder");
        JPanel pnlInput = new JPanel(new GridLayout(4, 1));
        JPanel pnlTarget = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pnlGray = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pnlColor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pnlAction = new JPanel(new FlowLayout());


        lblColor.setPreferredSize(lblGray.getPreferredSize());
        lblFolder.setPreferredSize(lblGray.getPreferredSize());

        tfFolderPath.setPreferredSize(new Dimension(300, 25));
        tfGray.setPreferredSize(new Dimension(300, 25));
        tfColor.setPreferredSize(new Dimension(300, 25));

        pnlTarget.add(lblFolder);
        pnlTarget.add(tfFolderPath);
        pnlTarget.add(btnTarget);
        pnlGray.add(lblGray);
        pnlGray.add(tfGray);
        pnlColor.add(lblColor);
        pnlColor.add(tfColor);

        pnlInput.add(pnlTarget);
        pnlInput.add(pnlGray);
        pnlInput.add(pnlColor);

        pnlInput.add(pnlAction);

        pnlAction.add(btnRun);

        add(pnlInput, BorderLayout.CENTER);
        add(progress, BorderLayout.SOUTH);

        progress.setStringPainted(true);

        btnTarget.addActionListener(e -> {
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (jfc.showOpenDialog(this) == jfc.getApproveButtonMnemonic()) {
                if (jfc.getSelectedFile() == null)
                    return;
                tfFolderPath.setText(jfc.getSelectedFile().toString());
                tfGray.setText(jfc.getSelectedFile().toString() + File.separatorChar + "greyscale");
                tfColor.setText(jfc.getSelectedFile().toString() + File.separatorChar + "color");
            }
        });

        btnRun.addActionListener(e -> {
            if (tfFolderPath.getText().equals(""))
                return;
            dm.setPhotoFolder(new File(tfFolderPath.getText()));
            dm.setGrayFolder(new File(tfGray.getText()));
            dm.setColorFolder(new File(tfColor.getText()));
            btnRun.setEnabled(false);
            try {
                new PhotoCheck(this).execute();
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        });
    }

    public JProgressBar getProgress() {
        return progress;
    }

    public DataModel getDataModel() {
        return dm;
    }
}