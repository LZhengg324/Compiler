package FileProcess;

import Lexical.Node;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class FileProcessor {
    private FileReader fileReader;
    private FileWriter fileWriter;
    private final String fileName = "testfile.txt";
    private final String outputName = "output.txt";
    private final String source;

    public FileProcessor() throws IOException {
        this.fileReader = new FileReader(fileName);
        this.fileWriter = new FileWriter(outputName);
        source = generateSource();
    }

    private String generateSource() {
        StringBuilder sb = new StringBuilder();
        String line;

        try(Scanner scanner = new Scanner(fileReader)) {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    public String getSource() {
        return source;
    }

    public void generateOutput(LinkedList<Node> nodes) throws IOException {
        for (Node node : nodes) {
            fileWriter.append(node.getType().toString());
            fileWriter.append(" ");
            fileWriter.append(node.getContent());
            //fileWriter.append(" ");
            //fileWriter.append(String.valueOf(node.getLineNum()));
            //fileWriter.append(" ");
            //fileWriter.append(String.valueOf(node.getNum()));
            fileWriter.append("\n");
        }
        fileWriter.flush();
        fileWriter.close();
    }
}
