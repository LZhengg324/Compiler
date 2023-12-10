package CodeGeneration;

import CodeGeneration.Command.LABEL;

import java.util.ArrayList;
import java.util.HashMap;

public class PcodeContainer {
    private static final PcodeContainer instance = new PcodeContainer();
    private final ArrayList<Pcode> codes;
    private int if_LabelCnt = 0;
    private int ifEnd_LabelCnt = 0;
    private int else_LabelCnt = 0;
    private int forStart_LabelCnt = 0;
    private int forStmt_LabelCnt = 0;
    private int forEnd_LabelCnt = 0;
    private int andEnd_LabelCnt = 0;
    private int orEnd_LabelCnt = 0;
    private final ArrayList<String> ifLabelStack = new ArrayList<>();
    private final ArrayList<String> ifEndLabelStack = new ArrayList<>();
    private final ArrayList<String> elseLabelStack = new ArrayList<>();
    private final ArrayList<String> forStartLabelStack = new ArrayList<>();
    private final ArrayList<String> forStmtLabelStack = new ArrayList<>();
    private final ArrayList<String> forEndLabelStack = new ArrayList<>();
    private final HashMap<String, Integer> labelsRecorder;

    private PcodeContainer() {
        this.codes = new ArrayList<>();
        this.labelsRecorder = new HashMap<>();
    }

    public static PcodeContainer getInstance() {
        return instance;
    }

    public void addPcode(Pcode pcode) {
        codes.add(pcode);

        if (pcode instanceof LABEL) {
            labelsRecorder.put(((LABEL)pcode).getLabel(), codes.indexOf(pcode));
//            System.out.println(labelsRecorder);
        }
    }

    public ArrayList<Pcode> getCodes() {
        return this.codes;
    }

    public HashMap<String, Integer> getLabelsRecorder() {
        return this.labelsRecorder;
    }

    public void generateIfLabel() {
        ifLabelStack.add(getIfLabelNo());
    }

    public String getIfLabel() {
        return ifLabelStack.remove(ifLabelStack.size() - 1);
    }

    public String getIfLabelNo() {
        String ret = "if_Label_" + if_LabelCnt;
        if_LabelCnt++;
        return ret;
    }

    public void generateIfEndLabel() {
        ifEndLabelStack.add(getIfEndLabelNo());
    }

    public String getIfEndLabel() {
        return ifEndLabelStack.remove(ifEndLabelStack.size() - 1);
    }

    public String getIfEndLabelNo() {
        String ret = "ifEnd_Label_" + ifEnd_LabelCnt;
        ifEnd_LabelCnt++;
        return ret;
    }

    public void generateElseLabel() {
        elseLabelStack.add(getElseLabelNo());
    }

    public String getElseLabelNo() {
        String ret = "else_Label_" + else_LabelCnt;
        else_LabelCnt++;
        return ret;
    }

    public String getElseLabel() {
        return elseLabelStack.remove(elseLabelStack.size() - 1);
    }

    public void generateForStartLabel() {
        forStartLabelStack.add(getForStartLabelNo());
    }

    public String getForStartLabelNo() {
        String ret = "forStart_Label_" + forStart_LabelCnt;
        forStart_LabelCnt++;
        return ret;
    }

    public String getForStmtLabel() {
        return forStmtLabelStack.get(forStmtLabelStack.size() - 1);
    }

    public void generateForStmtLabel() {
        forStmtLabelStack.add(getForStmtLabelNo());
    }

    public String getForStmtLabelNo() {
        String ret = "forStmt_Label_" + forStmt_LabelCnt;
        forStmt_LabelCnt++;
        return ret;
    }

    public String getForStartLabel() {
        return forStartLabelStack.get(forStartLabelStack.size() - 1);
    }

    public void generateForEndLabel() {
        forEndLabelStack.add(getForEndLabelNo());
    }

    public String getForEndLabelNo() {
        String ret = "forEnd_Label_" + forEnd_LabelCnt;
        forEnd_LabelCnt++;
        return ret;
    }

    public String getForEndLabel() {
        return forEndLabelStack.get(forEndLabelStack.size() - 1);
    }

    public String getAndLabel() {
        String ret = "andEnd_Label_" + andEnd_LabelCnt;
        andEnd_LabelCnt++;
        return ret;
    }

    public String getOrLabel() {
        String ret = "orEnd_Label_" + orEnd_LabelCnt;
        orEnd_LabelCnt++;
        return ret;
    }

    public void printPcode() {
        int i = 0;
        for (Pcode pcode : codes) {
            System.out.print(i++ + " ");
            pcode.printPcode();
        }
    }
}
