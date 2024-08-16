package Grammar.GrammarNode.NonTerminate;

import CodeGeneration.Command.LEA;
import CodeGeneration.Command.LIT;
import CodeGeneration.Command.LOD;
import CodeGeneration.Command.OPR;
import CodeGeneration.Pcode;
import CodeGeneration.PcodeContainer;
import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Grammar.Tables.Symbol;
import Grammar.Tables.SymbolsManager;
import Lexical.tokenNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class LVal extends NonTerminalNode {
    //LVal -> Ident {'[' Exp ']'}
    public LVal() {
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
        fileWriter.append("<LVal>\n");
    }

    public ArrayList<Exp> expNodes() {
        ArrayList<Exp> exps = new ArrayList<>();
        for (ASTNode node : getChildNodes()) {
            if (node instanceof Exp) {
                exps.add((Exp)node);
            }
        }
        return exps;
    }

    public Integer getParamType() {
        ArrayList<ASTNode> child = getChildNodes();
        String name = getIdent();
        if (child.size() == 1) {
            if (SymbolsManager.getInstance().getDimension(name) != null) {
                return SymbolsManager.getInstance().getDimension(name).size();
            }
        } else {
            if (SymbolsManager.getInstance().getDimension(name).size() == 1) {
                if (expNodes().size() == 1) {
                    return 0;
                } else {
                    return 1;
                }
            } else if (SymbolsManager.getInstance().getDimension(name).size() == 2) {
                if (expNodes().size() == 2) {
                    return 0;
                } else if (expNodes().size() == 1) {
                    return 1;
                } else if (expNodes().isEmpty()) {
                    return 2;
                }
            }
        }
        return -1;
    }

    public int getConstValue() {
        int ret = 0;
        ArrayList<Integer> expCount = new ArrayList<>();
        String varName = ((tokenNode)super.getChildNodes().get(0)).getContent();
        for (ASTNode node : super.getChildNodes()) {
            if (node instanceof Exp) {
                expCount.add(((Exp)node).getConstValue());
            }
        }
        if (expCount.isEmpty()) {
            return SymbolsManager.getInstance().getConstValue(varName, -1, -1);
        } else if (expCount.size() == 1) {
            return SymbolsManager.getInstance().getConstValue(varName, expCount.get(0), -1);
        } else if (expCount.size() == 2) {
            return SymbolsManager.getInstance().getConstValue(varName, expCount.get(0), expCount.get(1));
        }
        return ret;
    }

    public void loadPCode(boolean isLeft) {
        String lValName = ((tokenNode)super.getChildNodes().get(0)).getContent();
        ArrayList<Integer> dimensionLens = SymbolsManager.getInstance().getDimension(lValName);

        if (expNodes().isEmpty()) {
            PcodeContainer.getInstance().addPcode(new LIT(0));
        } else {
            for (int i = 0; i < expNodes().size(); i++) {
                expNodes().get(i).loadPCode();
                if (i == 0 && dimensionLens.size() == 2) {
                    PcodeContainer.getInstance().addPcode(new LIT(dimensionLens.get(1)));
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.MULT));
                } else if (i == 1) {
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.ADD));
                }
            }
        }

        if (!isLeft) {
            if (getParamType() == 0) {
                int level = SymbolsManager.getInstance().getVarLevel(lValName);
                int addr = SymbolsManager.getInstance().getVarAddr(lValName);
                if (SymbolsManager.getInstance().isFParamArrayAddr(lValName)) {
                    //形参数组
                    PcodeContainer.getInstance().addPcode(new LIT(0));
                    PcodeContainer.getInstance().addPcode(new LOD(level, addr));
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.ADD));
                    PcodeContainer.getInstance().addPcode(new LOD(-1, 0));
                } else {
                    PcodeContainer.getInstance().addPcode(new LOD(level, addr));
                }
            } else {
                int level = SymbolsManager.getInstance().getVarLevel(lValName);
                int addr = SymbolsManager.getInstance().getVarAddr(lValName);
                if (!SymbolsManager.getInstance().isFParamArrayAddr(lValName)) {
                    PcodeContainer.getInstance().addPcode(new LEA(level, addr));
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.ADD));
                } else {
                    PcodeContainer.getInstance().addPcode(new LIT(0));
                    PcodeContainer.getInstance().addPcode(new LOD(level, addr));
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.ADD));
                }
            }
        }
    }
}
