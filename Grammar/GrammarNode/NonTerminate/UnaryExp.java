package Grammar.GrammarNode.NonTerminate;

import CodeGeneration.Command.CALL;
import CodeGeneration.Command.INT;
import CodeGeneration.Command.LIT;
import CodeGeneration.Command.OPR;
import CodeGeneration.PcodeContainer;
import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Grammar.Tables.FunctionsManager;
import Lexical.LexType;
import Lexical.tokenNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class UnaryExp extends NonTerminalNode {
    //UnaryExp -> PrimaryExp
    //          | Ident '(' [FuncRParams] ')'
    //          | UnaryOp UnaryExp
    public UnaryExp() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public int getFuncRParamsSize() {
        for (ASTNode node : getChildNodes()) {
            if (node instanceof FuncRParams) {
                return ((FuncRParams)node).getParamSize();
            }
        }
        return 0;
    }

    public ArrayList<Integer> getFuncRParamsTypes() {
        for (ASTNode node : getChildNodes()) {
            if (node instanceof FuncRParams) {
                return ((FuncRParams)node).getParamsType();
            }
        }
        return null;
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
        }
        fileWriter.append("<UnaryExp>\n");
    }

    public Integer getParamType() {
        ArrayList<ASTNode> child = getChildNodes();
        for (int i = 0; i < child.size(); i++) {
            if (child.get(i) instanceof PrimaryExp) {
                return ((PrimaryExp)child.get(i)).getParamType();
            } else if (child.get(i) instanceof tokenNode) {
                String funcName = ((tokenNode) child.get(i)).getContent();
                if (FunctionsManager.getInstance().funcHasDefined(funcName)) {
                    if (FunctionsManager.getInstance().getFuncRetType(funcName).compareTo(LexType.VOIDTK) == 0) {
                        return -1;
                    } else if (FunctionsManager.getInstance().getFuncRetType(funcName).compareTo(LexType.INTTK) == 0) {
                        return 0;
                    }
                }
            } else if (child.get(i) instanceof UnaryOp) {
                return ((UnaryExp)child.get(i + 1)).getParamType();
            }
        }
        return -1;
    }

    public int getConstValue() {
        for (ASTNode node : super.getChildNodes()) {
            if (node instanceof PrimaryExp) {
                return ((PrimaryExp)node).getConstValue();
            } else if (node instanceof UnaryOp) {
                if (((UnaryOp)node).getOpType().compareTo(LexType.PLUS) == 0) {
                    return (((UnaryExp) super.getChildNodes().get(1)).getConstValue());
                } else if (((UnaryOp)node).getOpType().compareTo(LexType.MINU) == 0) {
                    return -(((UnaryExp) super.getChildNodes().get(1)).getConstValue());
                } else if (((UnaryOp)node).getOpType().compareTo(LexType.NOT) == 0) {
                    int num = ((UnaryExp) super.getChildNodes().get(1)).getConstValue();
                    if (num > 0) {
                        num = 0;
                    } else {
                        num = 1;
                    }
                    return num;
                }
            }
        }
        return 0;
    }

    public void loadPCode() {
        ArrayList<ASTNode> childNodes = super.getChildNodes();
        ASTNode firstNode = childNodes.get(0);
        if (firstNode instanceof PrimaryExp) {
            ((PrimaryExp)firstNode).loadPCode();
        } else if (firstNode instanceof UnaryOp) {
            if (((UnaryOp)firstNode).getOpType().compareTo(LexType.PLUS) == 0) {
                PcodeContainer.getInstance().addPcode(new LIT(0));
                ((UnaryExp) super.getChildNodes().get(1)).loadPCode();
                PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.ADD));
            } else if (((UnaryOp)firstNode).getOpType().compareTo(LexType.MINU) == 0) {
                PcodeContainer.getInstance().addPcode(new LIT(0));
                ((UnaryExp) super.getChildNodes().get(1)).loadPCode();
                PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.SUB));
            } else if (((UnaryOp)firstNode).getOpType().compareTo(LexType.NOT) == 0) {
                ((UnaryExp) super.getChildNodes().get(1)).loadPCode();
                PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.NOT));
            }
        } else if (firstNode instanceof tokenNode) {
            String funcName = ((tokenNode)firstNode).getContent();
            if (childNodes.size() == 3) {
                PcodeContainer.getInstance().addPcode(new CALL(funcName, 0));
            } else {
                int funcRParamsCnt = ((FuncRParams)childNodes.get(2)).getParamSize();
                ((FuncRParams)childNodes.get(2)).loadPCode();
                PcodeContainer.getInstance().addPcode(new CALL(funcName, funcRParamsCnt));
            }
            if (FunctionsManager.getInstance().getFuncRetType(funcName).compareTo(LexType.INTTK) == 0) {
                PcodeContainer.getInstance().addPcode(new INT(1));
            }
        }
    }
}
