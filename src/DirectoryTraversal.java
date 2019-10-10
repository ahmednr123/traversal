import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * THREAD UNSAFE SINGLETON
 */
public class DirectoryTraversal {
    public static String path = null;
    public static String saveTo = null;

    private static int activeThreads = 0;
    private static long startTime = 0;
    private static ArrayList<DirectoryData> hashmap = new ArrayList<>();

    private static ExecutorService executor = Executors.newFixedThreadPool(100);

    private DirectoryTraversal() {}

    public static void run () {
        if (path == null)
            throw new RuntimeException("Traverse path is not set!");

        if (saveTo == null)
            throw new RuntimeException("SaveTo path is not set!");

        startTime = System.currentTimeMillis();
        File pathFile = new File(path);

        if (!pathFile.exists() || !pathFile.isDirectory())
            throw new RuntimeException("The path doesn't exist or is not a directory");

        File[] files = pathFile.listFiles();
        addToHashmap(new DirectoryData(pathFile));

        System.out.println("Traversing directory...");
        showFiles(files);
    }

    private static synchronized void increment () {
        activeThreads++;
    }

    private static synchronized void decrement () {
        activeThreads--;
    }

    private static synchronized void addToHashmap (DirectoryData data) {
        hashmap.add(data);
    }

    private static synchronized void saveHashmapFile () {
        System.out.println("Saving File... (will take longer)\n");
        try {
            FileOutputStream fileOut = new FileOutputStream(saveTo);
            OutputStreamWriter out = new OutputStreamWriter(fileOut);
            for (DirectoryData data:hashmap) {
                out.write(data.toString()+"\n");
            }
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in " + saveTo);
            System.out.println("Restart application to traverse directory");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showFiles(File[] files) {
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                increment();
                addToHashmap(new DirectoryData(file));
                executor.execute(new Thread(() -> {
                    showFiles(file.listFiles());
                    if (activeThreads <= 1) {
                        System.out.println("Time taken: " + ((double) (System.currentTimeMillis()-startTime)/1000 ) + " secs");
                        executor.shutdown();
                        saveHashmapFile();
                    } else {
                        decrement();
                    }
                }));
            }
        }
    }
}
