import java.util.HashMap;

public class GetDirectorySize {
    private GetDirectorySize() {}

    public static long run (String root, HashMap<String, DirectoryData> hashmap) {
        DirectoryData directoryData = hashmap.get(root);
        long size = 0;

        for (FileData file : directoryData.getFileList()) {
            if (file.isDirectory()) {
                size += run(file.getFullPath(), hashmap);
            } else {
                size += file.getSize();
            }
        }

        hashmap.get(root).setSize(size);

        return size;
    }
}
