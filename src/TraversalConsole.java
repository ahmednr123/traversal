import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Usage:
 * TraversalConsole.path = "[valid path]";   <======   Set path first
 * TraversalConsole.run();
 *
 * Working:
 * The application requires two runs for a new path
 * First run traverses through the path and saves the file and directory info in a .hashmap file
 * Second run loads the .hashmap file data and runs the console
 *
 * The application will automatically look for the file with the path data
 *
 * [ONE TIME USE ONLY, PER PROGRAM RUN]
 */
public class TraversalConsole {

    private TraversalConsole () {}

    private static HashMap<String, DirectoryData> hashmap = new HashMap<>();
    public static String path = null;

    public static int times = 0;

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
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                long startTime = System.currentTimeMillis();

                // Local "line" variable
                {
                    String line = reader.readLine();
                    DirectoryData dir = new DirectoryData(line);
                    hashmap.put(dir.getFullPath(), dir);
                }

                // Load data in background
                //(new Thread(() -> {
                    try {
                        while (reader.ready()) {
                            String line = reader.readLine();
                            DirectoryData dir = new DirectoryData(line);
                            hashmap.put(dir.getFullPath(), dir);
                        }
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                //})).start();

                GetDirectorySize.run(path, hashmap);

                System.out.println("Time taken to load the file: " + (double)(System.currentTimeMillis() - startTime)/1000 + " secs");
            } catch (IOException e) {
                System.out.println("HashMap File Error!");
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

    // BAD CODE
    // NEEDS A REWRITE
    private static void console (String path) {
        boolean ERROR = false;
        while (true) {
            if (!ERROR) {
                System.out.println("Directory: " + path);

                System.out.println("====================================");
                long startTime = System.currentTimeMillis();
                DirectoryData root = hashmap.get(path);
                System.out.println("Directories in hashmap: " + hashmap.size());
                System.out.println("Time taken to get data: " + (System.currentTimeMillis() - startTime) + "ms");
                System.out.println("====================================");

                System.out.println("Inside: ");

                if (root.getFileList().size() == 0) {
                    System.out.println("(Empty Directory)");
                }

                String leftAlignFormat = "|%-1s | %-100s | %-10s |%n";

                System.out.format("+---+------------------------------------------------------------------------------------------------------+------------+%n");
                System.out.format("|   | Name                                                                                                 | Size       |%n");
                System.out.format("+---+------------------------------------------------------------------------------------------------------+------------+%n");

                for (FileData subFile : root.getFileList()) {
                    if (subFile.isDirectory()) {
                        System.out.format(leftAlignFormat, " d", subFile.getFilename(),
                                size_string(hashmap.get(subFile.getFullPath()).getSize()));
                    } else {
                        System.out.format(leftAlignFormat, " -", subFile.getFilename(),
                                size_string(subFile.getSize()));
                    }
                }
                System.out.format("+---+------------------------------------------------------------------------------------------------------+------------+%n");
            }

            ERROR = false;

            System.out.print("\n>  ");
            Scanner in = new Scanner(System.in);
            String next_dir = in.nextLine();
            String next_path = path;

            if (next_dir.equals("..")) {
                String[] path_arr = next_path.split("\\\\");
                next_path = String.join("\\", Arrays.copyOfRange(path_arr, 0, path_arr.length-1));

                if (path.charAt(path.length()-2) == ':') {
                    System.out.println("Directory: " + path);
                    System.out.println("Already in root");
                    //path = TraversalConsole.path;
                    ERROR = true;
                    continue;
                }

                if (next_path.charAt(next_path.length()-1) == ':')
                    next_path += '\\';

                if (!hashmap.containsKey(next_path)) {
                    System.out.println("Directory: " + path + "\\");
                    System.out.println("Already in root");
                    ERROR = true;
                    continue;
                } else {
                    path = next_path;
                    continue;
                }
            }

            if (path.charAt(path.length() - 1) != '\\') {
                next_path += "\\" + next_dir;
            } else {
                next_path += next_dir;
            }

            if (next_dir.equals("exit")) {
                break;
            }

            if (hashmap.containsKey(next_path)) {
                path = next_path;
                continue;
            } else {
                System.out.println("Wrong Input or [" + next_dir + "] is a file");
                ERROR = true;
                continue;
            }
        }
    }

    private static String size_string (long size) {
        String size_str = "";
        String[] sizes = {"B","KB","MB","GB","TB"};
        int index = 0;
        float f_size = (float)size;

        while ((f_size/1024) >= 1) {
            f_size = f_size/1024;
            index++;
        }

        size_str = String.format("%.2f %s", f_size, sizes[index]);

        return size_str;
    }

}
