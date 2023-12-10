package Grammar.GrammarNode.NonTerminate;

import CodeGeneration.Command.JMP;
import CodeGeneration.Command.JPT;
import CodeGeneration.Command.LABEL;
import CodeGeneration.Pcode;
import CodeGeneration.PcodeContainer;
import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class LOrExp extends NonTerminalNode {
    //LOrExp -> LAndExp | LOrExp '||' LAndExp
    public LOrExp() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        iterator.next().print(fileWriter);
        fileWriter.append("<LOrExp>\n");
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
            iterator.next().print(fileWriter);
            fileWriter.append("<LOrExp>\n");
        }
    }

    public void loadPCode(String trueLabel, String falseLabel) {
        ArrayList<ASTNode> childNodes = super.getChildNodes();
        String orEndLabel = PcodeContainer.getInstance().getOrLabel();
        if (childNodes.size() == 1) {
            ((LAndExp) childNodes.get(0)).loadPCode(trueLabel, falseLabel);
        } else {
            for (int i = 0; i < childNodes.size(); i = i + 2) {
                if (trueLabel != null) {
                    ((LAndExp) childNodes.get(i)).loadPCode(trueLabel, null);
                } else {
                    ((LAndExp) childNodes.get(i)).loadPCode(orEndLabel, null);
                }
            }
            if (falseLabel != null) {
                PcodeContainer.getInstance().addPcode(new JMP(falseLabel));
            }
            if (trueLabel == null) {
                PcodeContainer.getInstance().addPcode(new LABEL(orEndLabel));
            }
        }
    }
}
