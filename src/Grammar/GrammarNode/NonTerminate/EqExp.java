package Grammar.GrammarNode.NonTerminate;

import CodeGeneration.Command.OPR;
import CodeGeneration.PcodeContainer;
import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Lexical.LexType;
import Lexical.tokenNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class EqExp extends NonTerminalNode {
    //EqExp -> RelExp | EqExp ('==' | '!=') RelExp
    public EqExp() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        iterator.next().print(fileWriter);
        fileWriter.append("<EqExp>\n");
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
            iterator.next().print(fileWriter);
            fileWriter.append("<EqExp>\n");
        }
    }

    public void loadPCode() {
        ArrayList<ASTNode> childNodes = super.getChildNodes();
        ((RelExp)childNodes.get(0)).loadPCode();

        if (childNodes.size() > 1) {
            for (int i = 1; i < childNodes.size(); i = i + 2) {
                tokenNode opr = (tokenNode)childNodes.get(i);
                ((RelExp) childNodes.get(i + 1)).loadPCode();
                if (opr.getLexType().compareTo(LexType.EQL) == 0) {
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.CMPEQ));
                } else {
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.CMPNEQ));
                }

            }
        }
    }
}
