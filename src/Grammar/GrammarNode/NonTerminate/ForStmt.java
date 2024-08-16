package Grammar.GrammarNode.NonTerminate;

import CodeGeneration.Command.LIT;
import CodeGeneration.Command.LOD;
import CodeGeneration.Command.OPR;
import CodeGeneration.Command.STO;
import CodeGeneration.PcodeContainer;
import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Grammar.Tables.SymbolsManager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ForStmt extends NonTerminalNode {
    //ForStmt -> LVal '=' Exp
    public ForStmt() {
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
        fileWriter.append("<ForStmt>\n");
    }

    public void loadPCode() {
        ArrayList<ASTNode> nodes = super.getChildNodes();
        LVal lVal = (LVal)nodes.get(0);
        Exp exp = (Exp)nodes.get(2);
        String varName = lVal.getIdent();
        int level = SymbolsManager.getInstance().getVarLevel(varName);
        int addr = SymbolsManager.getInstance().getVarAddr(varName);

        exp.loadPCode();
        lVal.loadPCode(true);
        PcodeContainer.getInstance().addPcode(new STO(level, addr));
    }
}
