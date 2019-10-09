import java.io.File;
import java.util.ArrayList;

class FileData implements java.io.Serializable {
    private boolean isDirectory = false;
    private String fullPath = null;
    private String filename = null;

    FileData (File file) {
        isDirectory = file.isDirectory();
        fullPath = file.getAbsolutePath();
        filename = file.getName();
    }

    public String getFilename () {
        return filename;
    }

    public boolean isDirectory () {
        return isDirectory;
    }
}