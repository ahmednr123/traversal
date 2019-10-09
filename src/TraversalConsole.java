import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Scanner;

public class TraversalConsole {

    private TraversalConsole () {}

    private static HashMap<String, DirectoryData> hashmap = null;
    public static String path = null;//"C:\\Users";

    public static void run () {
        if (path == null) {
            throw new RuntimeException("Traversal Path not set");
        }

        String filename = "./dir-" + getHashCode(path) + ".hashmap";
        File hashmapFile = new File(filename);

        if (!hashmapFile.exists()) {
            DirectoryTraversal.path = path;
            DirectoryTraversal.saveTo = filename;
            DirectoryTraversal.run();
        } else {
            try {
                FileInputStream fileIn = new FileInputStream(filename);
                long startTime = System.currentTimeMillis();
                ObjectInputStream in = new ObjectInputStream(fileIn);
                hashmap = (HashMap<String, DirectoryData>) in.readObject();
                System.out.println("Time taken to load the file: " + (double)(System.currentTimeMillis() - startTime)/1000 + " secs");
                in.close();
                fileIn.close();
            } catch (IOException e) {
                System.out.println("HashMap File Error!");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("HashMap Class Error!");
                e.printStackTrace();
            }

            console(path);
        }
    }

    private static String getHashCode (String str) {
        int code = str.hashCode();

        if (code < 0) {
            code *= -1;
            return "x" + code;
        }

        return (code + "");
    }

    private static void console (String path) {
        if (path != null) {
            System.out.println("Directory: " + path);
            System.out.println("Inside: ");
            DirectoryData root = hashmap.get(path);
            for (FileData subFile : root.getFileList()) {
                if (subFile.isDirectory()) {
                    System.out.print("[d] ");
                } else {
                    System.out.print("[-] ");
                }
                System.out.print(subFile.getFilename() + "\n");
            }
        }

        System.out.print("\n>  ");
        Scanner in = new Scanner(System.in);
        String next_dir = in.nextLine();
        String next_path = path;

        if (path.charAt(path.length() - 1) != '\\') {
            next_path += "\\" + next_dir;
        } else {
            next_path += next_dir;
        }


        if (next_dir.equals("exit")) {
            System.exit(0);
        }

        if (hashmap.containsKey(next_path)) {
            console(next_path);
        } else {
            System.out.println("Wrong Input or [" + next_dir + "] is a file");
            console(null);
        }
    }

}
