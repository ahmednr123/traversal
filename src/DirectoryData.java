import java.io.File;
import java.util.ArrayList;

public class DirectoryData implements java.io.Serializable {
    private String fullPath = null;
    private String directoryName = null;
    private long size = -1;

    private ArrayList<FileData> fileList = new ArrayList<>();
    enum State { FULL_PATH, DIRECTORY_NAME, SIZE, FILES };

    DirectoryData (String codedString) {
        int index = 0;
        char ch;
        String buffer = "";

        State state = State.FULL_PATH;

        while (index < codedString.length()) {
            ch = codedString.charAt(index);

            if (state == State.FILES || ch != '|')
                buffer += ch;

            switch (state) {
                case FULL_PATH:
                    if (ch == '|') {
                        if (codedString.charAt(index+1) != '|')
                            throw new RuntimeException("Parsing error");

                        fullPath = buffer;
                        buffer = "";
                        index++;
                        state = State.DIRECTORY_NAME;
                    }
                    break;

                case DIRECTORY_NAME:
                    if (ch == '|') {
                        if (codedString.charAt(index+1) != '|')
                            throw new RuntimeException("Parsing error");

                        directoryName = buffer;
                        buffer = "";
                        index++;
                        state = State.SIZE;
                    }
                    break;

                case SIZE:
                    if (ch == '|') {
                        if (codedString.charAt(index+1) != '|')
                            throw new RuntimeException("Parsing error");

                        size = Long.parseLong(buffer);
                        buffer = "";
                        index++;
                        state = State.FILES;
                    }
                    break;

                case FILES:
                    if (ch == '|' && codedString.charAt(index+1) == '|') {
                        FileData file = new FileData(buffer, this.fullPath);
                        fileList.add(file);
                        buffer = "";
                        index++;
                    }
                    break;
            }

            index++;
        }

    }

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

    public void setSize (long size) {
        this.size = size;
    }

    public long getSize () {
        return size;
    }

    public String getFullPath () {
        return fullPath;
    }

    public String toString () {
        String str = "";

        str += fullPath + "||";
        str += directoryName + "||";
        str += size + "||";

        for (FileData file : fileList)
            str += file.toString() + "||";

        if (fileList.size() == 0)
            str += "null";

        return str;
    }
}