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

public class MulExp extends NonTerminalNode {
    //MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    public MulExp() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        iterator.next().print(fileWriter);
        fileWriter.append("<MulExp>\n");
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
            iterator.next().print(fileWriter);
            fileWriter.append("<MulExp>\n");
        }
    }

    public Integer getParamType() {
        for (ASTNode node : getChildNodes()) {
            if (node instanceof UnaryExp) {
                return ((UnaryExp)node).getParamType();
            }
        }
        return -1;
    }

    public int getConstValue() {
        int ret = 0;
        int temp = 0;
        int operation = -1; // 0 : *, 1 : /, 2 : %
        for (ASTNode node : super.getChildNodes()) {
            if (node instanceof UnaryExp) {
                temp = ((UnaryExp)node).getConstValue();
                if (operation != -1) {
                    if (operation == 0) {
                        ret = ret * temp;
                    } else if (operation == 1) {
                        ret = ret / temp;
                    } else {
                        ret = ret % temp;
                    }
                }  else {
                    ret = temp;
                }
            } else if (node instanceof tokenNode) {
                if (((tokenNode)node).getLexType().compareTo(LexType.MULT) == 0) {
                    operation = 0;
                } else if (((tokenNode)node).getLexType().compareTo(LexType.DIV) == 0) {
                    operation = 1;
                } else if (((tokenNode)node).getLexType().compareTo(LexType.MOD) == 0) {
                    operation = 2;
                }
            }
        }
        return ret;
    }

    public void loadPCode() {
        if (super.getChildNodes().size() == 1) {
            ((UnaryExp)super.getChildNodes().get(0)).loadPCode();
        } else {
            ((UnaryExp)super.getChildNodes().get(0)).loadPCode();
            for (int i = 1; i < super.getChildNodes().size(); i = i + 2) {
                ((UnaryExp)super.getChildNodes().get(i + 1)).loadPCode();
                LexType op = ((tokenNode)super.getChildNodes().get(i)).getLexType();
                if (op.compareTo(LexType.MULT) == 0) {
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.MULT));
                } else if (op.compareTo(LexType.DIV) == 0) {
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.DIV));
                } else if (op.compareTo(LexType.MOD) == 0) {
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.MOD));
                }
            }
        }
//        for (ASTNode node : super.getChildNodes()) {
//            if (node instanceof UnaryExp) {
//                ((UnaryExp)node).loadPCode();
//            } else if (node instanceof tokenNode){
//                if (((tokenNode)node).getLexType().compareTo(LexType.MULT) == 0) {
//                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.MULT));
//                } else if (((tokenNode)node).getLexType().compareTo(LexType.DIV) == 0) {
//                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.DIV));
//                } else if (((tokenNode)node).getLexType().compareTo(LexType.MOD) == 0) {
//                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.MOD));
//                }
//            }
//        }
    }
}
