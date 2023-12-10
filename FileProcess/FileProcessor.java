package FileProcess;

import ErrorHandling.Error;
import ErrorHandling.ErrorHandler;
import Grammar.GrammarNode.NonTerminate.CompUnit;
import Lexical.tokenNode;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileProcessor {
    private final FileReader fileReader;
    private final FileWriter parserFileWriter;
    private final FileWriter errorsFileWriter;
    private final FileWriter pcodeResultFileWriter;
    private final String fileName = "testfile.txt";
    private final String outputName = "output.txt";
    private final String errorName = "error.txt";
    private final String resultName = "pcoderesult.txt";
    private final String source;

    public FileProcessor() throws IOException {
        this.fileReader = new FileReader(fileName);
        this.parserFileWriter = new FileWriter(outputName);
        this.errorsFileWriter = new FileWriter(errorName);
        this.pcodeResultFileWriter = new FileWriter(resultName);
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

    public FileWriter getParserFileWriter() {
        return parserFileWriter;
    }

    public void generateLexerOutput(ArrayList<tokenNode> nodes) throws IOException {
        for (tokenNode node : nodes) {
            parserFileWriter.append(node.getLexType().toString());
            parserFileWriter.append(" ");
            parserFileWriter.append(node.getContent());
            //fileWriter.append(" ");
            //fileWriter.append(String.valueOf(node.getLineNum()));
            //fileWriter.append(" ");
            //fileWriter.append(String.valueOf(node.getNum()));
            parserFileWriter.append("\n");
        }
        parserFileWriter.flush();
        parserFileWriter.close();
    }

    public void generateParserOutput(CompUnit root) throws IOException {
        root.print(parserFileWriter);
        parserFileWriter.flush();
        parserFileWriter.close();
    }

    public void generateErrorOutput() throws IOException {
        for (Error error : ErrorHandler.getList()) {
            errorsFileWriter.append(String.valueOf(error.getLineNum()));
            errorsFileWriter.append(" ");
            errorsFileWriter.append(error.getErrorType().toString());
            errorsFileWriter.append("\n");
        }
        errorsFileWriter.flush();
        errorsFileWriter.close();
    }

    public void generatePcodeResult(String executeResult) throws IOException {
        pcodeResultFileWriter.append(executeResult);
        pcodeResultFileWriter.flush();
        pcodeResultFileWriter.close();
    }
}
