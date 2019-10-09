import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
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
    private static HashMap<String, DirectoryData> hashmap = new HashMap<>();

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
        addToHashmap(pathFile.getAbsolutePath(), new DirectoryData(pathFile));

        System.out.println("Traversing directory...");
        showFiles(files);
    }

    private static synchronized void increment () {
        activeThreads++;
    }

    private static synchronized void decrement () {
        activeThreads--;
    }

    private static synchronized void addToHashmap (String path, DirectoryData data) {
        hashmap.put(path, data);
    }

    private static synchronized void saveHashmapFile () {
        try {
            FileOutputStream fileOut = new FileOutputStream(saveTo);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(hashmap);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in " + saveTo);
            System.out.println("Restart application to view traverse directory");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showFiles(File[] files) {
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                increment();
                addToHashmap(file.getAbsolutePath(), new DirectoryData(file));
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
