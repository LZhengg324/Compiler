package Lexical;

import Grammar.GrammarNode.ASTNode;

import java.io.FileWriter;
import java.io.IOException;

public class tokenNode implements ASTNode {
    private String content;
    private LexType type;
    private int lineNum;
    private Integer num;

    public tokenNode(String content, LexType type, int lineNum) {
        this.content = content;
        this.type = type;
        this.lineNum = lineNum;
        if (type.compareTo(LexType.INTCON) == 0) {
            num = Integer.parseInt(content);
        } else {
            num = null;
        }
    }

    public String getContent() {
        return this.content;
    }

    public LexType getType() {
        return this.type;
    }

    public int getLineNum() {
        return this.lineNum;
    }

    public Integer getNum() {
        return this.num;
    }

    public void print(FileWriter fileWriter) throws IOException {
        fileWriter.append(type.toString());
        fileWriter.append(" ");
        fileWriter.append(content);
        fileWriter.append("\n");
    }
}
