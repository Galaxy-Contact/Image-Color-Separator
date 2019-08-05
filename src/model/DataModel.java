package model;

import java.io.File;

public class DataModel {
    private File photoFolder, grayFolder, colorFolder;

    public File getGrayFolder() {
        return grayFolder;
    }

    public void setGrayFolder(File grayFolder) {
        this.grayFolder = grayFolder;
        grayFolder.mkdirs();
    }

    public File getColorFolder() {
        return colorFolder;
    }

    public void setColorFolder(File colorFolder) {
        this.colorFolder = colorFolder;
        colorFolder.mkdirs();
    }

    public File getPhotoFolder() {
        return photoFolder;
    }

    public void setPhotoFolder(File photoFolder) {
        this.photoFolder = photoFolder;
    }
}
