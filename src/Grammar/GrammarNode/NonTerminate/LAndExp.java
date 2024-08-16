package Grammar.GrammarNode.NonTerminate;

import CodeGeneration.Command.JMP;
import CodeGeneration.Command.JPF;
import CodeGeneration.Command.LABEL;
import CodeGeneration.PcodeContainer;
import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class LAndExp extends NonTerminalNode {
    //LAndExp -> EqExp | LAndExp '&&' EqExp
    public LAndExp() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        iterator.next().print(fileWriter);
        fileWriter.append("<LAndExp>\n");
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
            iterator.next().print(fileWriter);
            fileWriter.append("<LAndExp>\n");
        }
    }

    public void loadPCode(String trueLabel, String falseLabel) {
        ArrayList<ASTNode> childNodes = super.getChildNodes();
        String andEndLabel = PcodeContainer.getInstance().getAndLabel();
        for (int i = 0; i < childNodes.size(); i = i + 2) {
            ((EqExp) childNodes.get(i)).loadPCode();
            if (falseLabel != null) {
                PcodeContainer.getInstance().addPcode(new JPF(falseLabel));
            } else {
                PcodeContainer.getInstance().addPcode(new JPF(andEndLabel));
            }
        }
        if (trueLabel != null) {
            PcodeContainer.getInstance().addPcode(new JMP(trueLabel));
        }
        if (falseLabel == null) {
            PcodeContainer.getInstance().addPcode(new LABEL(andEndLabel));
        }
    }
}
