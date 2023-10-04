package Lexical;

import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    private final String source;
    private int curPos;
    private StringBuilder token;
    private int lineNum;
    private ArrayList<Node> list;
    private final HashMap<String, LexType> reserveWords = new HashMap<String, LexType>() {{
            put("main", LexType.MAINTK);
            put("const", LexType.CONSTTK);
            put("int", LexType.INTTK);
            put("break", LexType.BREAKTK);
            put("continue", LexType.CONTINUETK);
            put("if", LexType.IFTK);
            put("else", LexType.ELSETK);
            put("for", LexType.FORTK);
            put("getint", LexType.GETINTTK);
            put("printf", LexType.PRINTFTK);
            put("return", LexType.RETURNTK);
            put("void", LexType.VOIDTK);
        }
    };

    private final HashMap<String, LexType> operations = new HashMap<String, LexType>() {{
            put("+", LexType.PLUS);
            put("-", LexType.MINU);
            put("*", LexType.MULT);
            put("%", LexType.MOD);
        }
    };

    private final HashMap<String, LexType> brackets = new HashMap<String, LexType>() {{
            put("{", LexType.LBRACE);
            put("}", LexType.RBRACE);
            put("[", LexType.LBRACK);
            put("]", LexType.RBRACK);
            put("(", LexType.LPARENT);
            put(")", LexType.RPARENT);
        }
    };

    public Lexer(String source) {
        this.source = source;
        this.curPos = 0;
        this.token = new StringBuilder();
        this.lineNum = 1;
        this.list = new ArrayList<>();
        generateLexical();
    }

    public Character getChar() {
        if (curPos < source.length()) {
            char c = source.charAt(curPos++);
            if (c == '\n') {
                lineNum++;
            }
            return c;
        }
        return null;
    }

    public void ungetChar() {   //超前扫描回退
        curPos--;
        if (source.charAt(curPos) == '\n') {
            lineNum--;
        }
    }

    public void generateLexical() {
        Character c;
        while (curPos < source.length()) {
            clearToken();
            c = getChar();
            if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                continue;
            } else if (Character.isDigit(c)) {
                do {
                    token.append(c);
                    c = getChar();
                } while (Character.isDigit(c));
                ungetChar();
                list.add(new Node(token.toString(), LexType.INTCON, lineNum));
            } else if (Character.isLetter(c) || c == '_') {
                do {
                    token.append(c);
                    c = getChar();
                } while (Character.isDigit(c) || Character.isLetter(c) || c == '_');
                ungetChar();
                if (isReserveWord(token.toString())) {
                    list.add(new Node(token.toString(), getReservedWordLexType(token.toString()), lineNum));
                } else {
                    list.add(new Node(token.toString(), LexType.IDENFR, lineNum));
                }
            } else if (isOperation(c.toString())) {
                list.add(new Node(c.toString(), getOperationsLexType(c.toString()), lineNum));
            } else if (isBracket(c.toString())) {
                list.add(new Node(c.toString(), getBracketLexType(c.toString()), lineNum));
            } else if (c == ';') {
                list.add(new Node(c.toString(), LexType.SEMICN, lineNum));
            } else if (c == ',') {
                list.add(new Node(c.toString(), LexType.COMMA, lineNum));
            } else if (isRelation(c)) {
                processRelation(c);
            } else if (c == '/') {
                processSlash(c);
            } else if (c == '\"') {
                processFormatString(c);
            }
        }
    }

    private void processRelation(char c) {
        token.append(c);
        if (c == '=') {
            Character next = getChar();
            if (next == '=') {
                token.append(next);
                list.add(new Node(token.toString(), LexType.EQL, lineNum));
            } else {
                ungetChar();
                list.add(new Node(token.toString(), LexType.ASSIGN, lineNum));
            }
        } else if (c == '>') {
            Character next = getChar();
            if (next == '=') {
                token.append(next);
                list.add(new Node(token.toString(), LexType.GEQ, lineNum));
            } else {
                ungetChar();
                list.add(new Node(token.toString(), LexType.GRE, lineNum));
            }
        } else if (c == '<') {
            Character next = getChar();
            if (next == '=') {
                token.append(next);
                list.add(new Node(token.toString(), LexType.LEQ, lineNum));
            } else {
                ungetChar();
                list.add(new Node(token.toString(), LexType.LSS, lineNum));
            }
        } else if (c == '!') {
            Character next = getChar();
            if (next == '=') {
                token.append(next);
                list.add(new Node(token.toString(), LexType.NEQ, lineNum));
            } else {
                ungetChar();
                list.add(new Node(token.toString(), LexType.NOT, lineNum));
            }
        } else if (c == '|') {
            Character next = getChar();
            token.append(next);
            list.add(new Node(token.toString(), LexType.OR, lineNum));
        } else if (c == '&') {
            Character next = getChar();
            token.append(next);
            list.add(new Node(token.toString(), LexType.AND, lineNum));
        }
    }

    private void processSlash(char c) {
        token.append(c);
        Character next = getChar();
        if (next == '/') {
            do {
                next = getChar();
            } while (next != '\n' && curPos < source.length());
        } else if (next == '*') {
            do {
                next = getChar();
                if (next == '*') {
                    next = getChar();
                    if (next == '/') {
                        break;
                    } else {
                        ungetChar();
                    }
                }
            } while (true);
        } else {
            ungetChar();
            list.add(new Node(token.toString(), LexType.DIV, lineNum));
        }
    }

    private void processFormatString(char c) {
        token.append(c);
        Character next;
        do {
            next = getChar();
            token.append(next);
        } while (next != '\"');
        list.add(new Node(token.toString(), LexType.STRCON, lineNum));
    }

    public void clearToken() {
        token.delete(0, token.length());
    }

    public boolean isReserveWord(String word) {
        for (String reserved : reserveWords.keySet()) {
            if (reserved.compareTo(word) == 0) {
                return true;
            }
        }
        return false;
    }

    public LexType getReservedWordLexType(String word) {
        for (String reserved : reserveWords.keySet()) {
            if (reserved.compareTo(word) == 0) {
                return reserveWords.get(reserved);
            }
        }
        return null;
    }

    public boolean isOperation(String word) {
        for (String operation : operations.keySet()) {
            if (operation.compareTo(word) == 0) {
                return true;
            }
        }
        return false;
    }

    public LexType getOperationsLexType(String word) {
        for (String operation : operations.keySet()) {
            if (operation.compareTo(word) == 0) {
                return operations.get(operation);
            }
        }
        return null;
    }

    public boolean isBracket(String word) {
        for (String bracket : brackets.keySet()) {
            if (bracket.compareTo(word) == 0) {
                return true;
            }
        }
        return false;
    }

    public LexType getBracketLexType(String word) {
        for (String bracket : brackets.keySet()) {
            if (bracket.compareTo(word) == 0) {
                return brackets.get(bracket);
            }
        }
        return null;
    }

    public boolean isRelation(char word) {
        return (word == '>' || word == '<' || word == '='
                || word == '&' || word == '|' || word == '!');
    }

    public ArrayList<Node> getList() {
        return this.list;
    }
}
