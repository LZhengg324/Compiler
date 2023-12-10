package Grammar.GrammarNode.NonTerminate;

import CodeGeneration.Command.LIT;
import CodeGeneration.PcodeContainer;
import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Lexical.tokenNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class NumberNode extends NonTerminalNode {
    //Number -> IntConst
    public NumberNode() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
        }
        fileWriter.append("<Number>\n");
    }

    public int getConstValue() {
        return ((tokenNode)super.getChildNodes().get(0)).getNum();
    }

    public void loadPCode() {
        int num = ((tokenNode)super.getChildNodes().get(0)).getNum();
        PcodeContainer.getInstance().addPcode(new LIT(num));
    }
}
