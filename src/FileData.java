import java.io.File;

class FileData implements java.io.Serializable {
    private boolean isDirectory = false;
    private String fullPath = null;
    private String filename = null;

    private long size = -1;

    enum State { IS_DIRECTORY, FILENAME, SIZE };

    FileData (String codedString, String fullPath) {
        if (codedString.equals("null")) {
            return;
        }

        int index = 0;
        char ch;
        String buffer = "";

        State state = State.IS_DIRECTORY;

        while (index < codedString.length()) {
            ch = codedString.charAt(index);

            if (ch != '|')
                buffer += ch;

            switch (state) {

                case IS_DIRECTORY:
                    if (ch == '|') {
                        isDirectory = buffer.equals("true");
                        buffer = "";
                        state = State.FILENAME;
                    }
                    break;

                case FILENAME:
                    if (ch == '|') {
                        filename = buffer;
                        buffer = "";
                        state = State.SIZE;
                    }
                    break;

                case SIZE:
                    if (ch == '|') {
                        size = Long.parseLong(buffer);
                        buffer = "";
                    }
            }

            if (fullPath.charAt(fullPath.length() - 1) == '\\') {
                this.fullPath = fullPath + filename;
            } else {
                this.fullPath = fullPath + '\\' + filename;
            }

            index++;
        }
    }

    FileData (File file) {
        isDirectory = file.isDirectory();
        fullPath = file.getAbsolutePath();
        filename = file.getName();
        if (!isDirectory)
            size = file.length();
    }

    public String getFilename () {
        return filename;
    }

    public long getSize () {
        return size;
    }

    public void setSize (long size) {
        this.size = size;
    }

    public boolean isDirectory () {
        return isDirectory;
    }

    public String getFullPath () {
        return fullPath;
    }

    public String toString () {
        String str = "";

        str += isDirectory + "|";
        str += filename + "|";
        str += size;

        return str;
    }
}