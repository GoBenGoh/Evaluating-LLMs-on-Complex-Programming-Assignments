import java.io.*;

public class JavaToText {
    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        String line = null;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            FileWriter fileWriter = new FileWriter(fileName+".txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedReader.close();
            bufferedWriter.close();
        } finally {
            System.out.println("Done");
        }
    }
}
