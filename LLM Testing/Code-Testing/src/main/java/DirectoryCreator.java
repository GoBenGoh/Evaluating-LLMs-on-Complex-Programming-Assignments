import java.io.File;

public class DirectoryCreator {
    public static void createDirectory(String directory) {
        File folder = new File(directory);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("Folder created: " + directory);
            } else {
                System.out.println("Failed to create folder: " + directory);
            }
        }
    }
}
