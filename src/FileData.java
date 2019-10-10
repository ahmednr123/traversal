import java.io.File;

class FileData implements java.io.Serializable {
    private boolean isDirectory = false;
    private String fullPath = null;
    private String filename = null;

    enum State { IS_DIRECTORY, FILENAME };

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
                    }
                    break;
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
    }

    public String getFilename () {
        return filename;
    }

    public boolean isDirectory () {
        return isDirectory;
    }

    public String toString () {
        String str = "";

        str += isDirectory + "|";
        str += filename;

        return str;
    }
}