package main;

import gui.MainGUI;
import model.DataModel;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class PhotoCheck extends SwingWorker<Void, Integer> {

    private MainGUI parent;

    private final double LIM_RATIO = 0.008;
    private final double LIMIT = 2;

    private final String[] imageExtensions = new String[]{"jpg", "jpeg", "png", "bmp", "gif", "tif"};
    private DataModel dm;

    private int current = 0, total = 0;

    public PhotoCheck(MainGUI parent) {
        this.parent = parent;
    }

    private int recursiveCountFiles(File cur) {
        if (cur.isFile())
            return 1;
        else {
            int count = 0;
            File[] listFile = cur.listFiles();
            if (listFile == null)
                return 0;
            for (File f : listFile) {
                count += recursiveCountFiles(f);
            }
            return count;
        }
    }

    private void processFile(File file) throws IOException {
        if (!Arrays.asList(imageExtensions).contains(file.getName().substring(file.getName().lastIndexOf(".") + 1))) {
            return;
        }

        Mat matrix = Imgcodecs.imread(file.getPath());

        boolean color = checkColor(matrix, matrix.width(), matrix.height());

        File destination = new File(dm.getGrayFolder().getPath() + File.separatorChar + file.getName());

        if (color) {
            destination = new File(dm.getColorFolder().getPath() + File.separatorChar + file.getName());
        }

        Files.move(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

        matrix.release();
    }

    private void recursiveTraversal(File cur) throws IOException {
        if (cur.isFile()) {
            processFile(cur);
            publish();
        } else {
            File[] listFile = cur.listFiles();
            if (listFile == null)
                return;
            for (File f : listFile) {
                if (f.getPath().equals(dm.getColorFolder().getPath()) || f.getPath().equals(dm.getGrayFolder().getPath()))
                    continue;
                recursiveTraversal(f);
            }
        }
    }

    @Override
    protected Void doInBackground() {
        try {
            parent.getProgress().setString("Counting files...");
            dm = parent.getDataModel();
            total = recursiveCountFiles(dm.getPhotoFolder());
            recursiveTraversal(dm.getPhotoFolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkColor(Mat br, int width, int height) {

        if (br.channels() == 1)
            return false;

        int color = 0;
        double r = 0, g = 0, b = 0;
        double[] p;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                p = br.get(j, i);
                if (br.channels() == 3) {
                    r = p[0];
                    g = p[1];
                    b = p[2];
                } else if (br.channels() == 4) {
                    r = p[1];
                    g = p[2];
                    b = p[3];
                }

                if ((Math.abs(r - g) > LIMIT) || (Math.abs(g - b) > LIMIT) || (Math.abs(r - b) > LIMIT)) {
                    color++;
                }
                if (((double) color / (width * height)) > LIM_RATIO) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void process(List<Integer> chunks) {
        current ++;
        parent.getProgress().setString("Moved " + current + " / " + total);
        parent.getProgress().setValue((int) ((float) current / total * 100.0));
    }

    @Override
    protected void done() {
        parent.getProgress().setString("Finished");
        parent.getProgress().setValue(100);
    }
}
