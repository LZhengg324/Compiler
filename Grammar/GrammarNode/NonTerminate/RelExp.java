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

public class RelExp extends NonTerminalNode {
    //RelExp -> AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    public RelExp() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        iterator.next().print(fileWriter);
        fileWriter.append("<RelExp>\n");
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
            iterator.next().print(fileWriter);
            fileWriter.append("<RelExp>\n");
        }
    }

    public void loadPCode() {
        ArrayList<ASTNode> childNodes = super.getChildNodes();
        ((AddExp)childNodes.get(0)).loadPCode();

        if (childNodes.size() > 1) {
            for (int i = 1; i < childNodes.size(); i = i + 2) {
                tokenNode opr = (tokenNode)childNodes.get(i);
                ((AddExp) childNodes.get(i + 1)).loadPCode();
                if (opr.getLexType().compareTo(LexType.GEQ) == 0) {
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.CMPGE));
                } else if (opr.getLexType().compareTo(LexType.GRE) == 0) {
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.CMPGT));
                } else if (opr.getLexType().compareTo(LexType.LEQ) == 0) {
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.CMPLE));
                } else {
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.CMPLT));
                }

            }
        }
    }
}
