package Lexical;

public class Node {
    private String content;
    private LexType type;
    private int lineNum;
    private Integer num;

    public Node(String content, LexType type, int lineNum) {
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
}
