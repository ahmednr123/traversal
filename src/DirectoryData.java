import java.io.File;
import java.util.ArrayList;

public class DirectoryData implements java.io.Serializable {
    private String fullPath = null;
    private String directoryName = null;

    private ArrayList<FileData> fileList = new ArrayList<>();

    DirectoryData (File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    fileList.add(new FileData(subFile));
                }
            }
        } else {
            throw new RuntimeException("Storing file in DirectoryData");
        }

        fullPath = file.getAbsolutePath();
        directoryName = file.getName();
    }

    public ArrayList<FileData> getFileList () {
        return fileList;
    }
}
