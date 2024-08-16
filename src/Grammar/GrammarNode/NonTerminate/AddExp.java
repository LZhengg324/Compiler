package Grammar.GrammarNode.NonTerminate;

import CodeGeneration.Command.OPR;
import CodeGeneration.PcodeContainer;
import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Lexical.LexType;
import Lexical.tokenNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class AddExp extends NonTerminalNode {
    //AddExp -> MulExp | AddExp ('+' | '−') MulExp
    //AddExp -> MulExp {('+' | '−') MulExp}
    public AddExp() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        iterator.next().print(fileWriter);
        fileWriter.append("<AddExp>\n");
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
            iterator.next().print(fileWriter);
            fileWriter.append("<AddExp>\n");
        }
    }

    public Integer getParamType() {
        for (ASTNode node : getChildNodes()) {
            if (node instanceof MulExp) {
                return ((MulExp)node).getParamType();
            }
        }
        return -1;
    }

    public int getConstValue() {
        int ret = 0;
        int temp = 0;
        int operation = -1;
        for (ASTNode node : super.getChildNodes()) {
            if (node instanceof MulExp) {
                temp = ((MulExp)node).getConstValue();
                if (operation != -1) {
                    if (operation == 0) {
                        ret += temp;
                    } else {
                        ret -= temp;
                    }
                } else {
                    ret = temp;
                }
            } else if (node instanceof tokenNode) {
                if (((tokenNode)node).getLexType().compareTo(LexType.PLUS) == 0) {
                    operation = 0;
                } else if (((tokenNode)node).getLexType().compareTo(LexType.MINU) == 0) {
                    operation = 1;
                }
            }
        }
        return  ret;
    }

    public void loadPCode() {
        if (super.getChildNodes().size() == 1) {
            ((MulExp)super.getChildNodes().get(0)).loadPCode();
        } else {
            ((MulExp)super.getChildNodes().get(0)).loadPCode();
            for (int i = 1; i < super.getChildNodes().size(); i = i + 2) {
                ((MulExp)super.getChildNodes().get(i + 1)).loadPCode();
                LexType op = ((tokenNode) super.getChildNodes().get(i)).getLexType();
                if (op.compareTo(LexType.PLUS) == 0) {
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.ADD));
                } else if (op.compareTo(LexType.MINU) == 0) {
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.SUB));
                }
            }
//            for (ASTNode node : super.getChildNodes()) {
//                if (node instanceof MulExp) {
//                    ((MulExp) node).loadPCode();
//                } else if (node instanceof tokenNode) {
//                    if (((tokenNode) node).getLexType().compareTo(LexType.PLUS) == 0) {
//                        PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.ADD));
//                    } else if (((tokenNode) node).getLexType().compareTo(LexType.MINU) == 0) {
//                        PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.SUB));
//                    }
//                }
//            }
        }
    }
}
