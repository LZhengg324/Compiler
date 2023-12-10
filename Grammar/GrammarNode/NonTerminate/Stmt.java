package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class Stmt extends NonTerminalNode {
    //Stmt -> LVal '=' Exp ';'
    //      | [Exp] ';'
    //      | Block
    //      | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
    //      | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    //      | 'break' ';' | 'continue' ';'
    //      | 'return' [Exp] ';'
    //      | LVal '=' 'getint' '(' ')' ';'
    //      | 'printf' '('FormatString { ',' Exp } ')' ';'
    private StmtType type;

    public Stmt() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public StmtType getType() {
        return this.type;
    }

    public void setType(StmtType stmtType) {
        this.type = stmtType;
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
        }
        fileWriter.append("<Stmt>\n");
    }
}
