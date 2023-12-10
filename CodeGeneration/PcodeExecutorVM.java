package CodeGeneration;

import CodeGeneration.Command.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PcodeExecutorVM {
    private static PcodeExecutorVM instance = new PcodeExecutorVM();
    private ArrayList<Pcode> pcodes;
    private int stack[];
    private int sp;
    private int pc;
    private int mp;
    private HashMap<String, Integer> labelsRecorder;
    private Scanner scanner;
    private final StringBuilder result = new StringBuilder();

    private PcodeExecutorVM() {
        this.stack = new int[1000000];
        stack[0] = 0;
        stack[1] = -1;
        stack[2] = 0;
        stack[3] = 0;
        this.sp = 3;
        this.mp = 0;
        this.pc = 0;
        this.pcodes = PcodeContainer.getInstance().getCodes();
        this.scanner = new Scanner(System.in);
        this.labelsRecorder = PcodeContainer.getInstance().getLabelsRecorder();
    }

    public static PcodeExecutorVM getInstance() {
        return instance;
    }

    public void startExecute() {
        while(pc < pcodes.size()) {
//            System.out.println(pcodes.get(pc).getCommand() + " pc : " + pc);
//            System.out.println("pc : " + pc + "  mp : " + mp);
            if (sp >= stack.length - 1) {
                int[] arrayNew = new int[stack.length + 10000];
                System.arraycopy(stack, 0, arrayNew, 0, stack.length);
                stack = arrayNew;
            }
            run(pcodes.get(pc));
        }
    }

    private void run(Pcode pcode) {
        if (pcode instanceof BLKS) {
            stack[sp + 3] = mp;
            mp = sp + 1;
            sp = sp + 4;
            pc++;
        } else if (pcode instanceof BLKE) {
            sp = mp - 1;
            mp = stack[mp + 2];
            pc++;
        } else if (pcode instanceof CALL) {
            for (int i = 0; i < ((CALL)pcode).getParanum(); i++) {
                stack[sp + 4 - i] = stack[sp - i];
            }
            sp = sp - ((CALL)pcode).getParanum();

            stack[sp + 2] = pc + 1;
            stack[sp + 3] = 0;
            stack[sp + 4] = mp;

            mp = sp + 1;
            sp = sp + 4 + ((CALL)pcode).getParanum();
//            if (((CALL)pcode).getLabel().compareTo("main") != 0) {
                pc = labelsRecorder.get(((CALL) pcode).getLabel());
//            } else {
//                pc++;
//            }
        } else if (pcode instanceof RET) {
            pc = stack[mp + 1];
            sp = mp - 1;
            mp = stack[mp + 3];
        } else if (pcode instanceof LOD) {
            int offset = stack[sp];
            sp--;
            int curmp = mp;
            for (int i = 0; i < ((LOD) pcode).getLevel(); i++) {
                curmp = stack[curmp + 2];
            }
            if (((LOD) pcode).getLevel() == -1) {
                curmp = 0;
            }
            int value = stack[curmp + ((LOD)pcode).getAddr() + offset];
            sp++;
            stack[sp] = value;
            pc++;
        } else if (pcode instanceof STO) {
            int offset = stack[sp--];
            int value = stack[sp--];
            int curmp = mp;
            for (int i = 0; i < ((STO)pcode).getLevel(); i++) {
                curmp = stack[curmp + 2];
            }
            if (((STO) pcode).getLevel() == -1) {
                curmp = 0;
            }
            stack[curmp + ((STO)pcode).getAddr() + offset] = value;
            pc++;
        } else if (pcode instanceof JPT) {
            if (stack[sp] != 0) {
                pc = labelsRecorder.get(((JPT)pcode).getLabel());
            } else {
                pc++;
            }
            sp--;
        } else if (pcode instanceof JPF) {
            if (stack[sp] == 0) {
                pc = labelsRecorder.get(((JPF)pcode).getLabel());
            } else {
                pc++;
            }
            sp--;
        } else if (pcode instanceof JMP) {
            pc = labelsRecorder.get(((JMP)pcode).getLabel());
        } else if (pcode instanceof LIT) {
            stack[++sp] = ((LIT)pcode).getImm();
            pc++;
        } else if (pcode instanceof INT) {
            sp = sp + ((INT)pcode).getImm();
            pc++;
        } else if (pcode instanceof LEA) {
            int curmp = mp;
            for (int i = 0; i < ((LEA) pcode).getLevel(); i++) {
                curmp = stack[curmp + 2];
            }
            int value = curmp + ((LEA)pcode).getAddr();
            stack[++sp] = value;
            pc++;
        } else if (pcode instanceof RED) {
            int value = scanner.nextInt();
            sp++;
            stack[sp] = value;
            pc++;
        } else if (pcode instanceof WRT) {
            result.append(stack[sp]);
            sp--;
            pc++;
        } else if (pcode instanceof WRTS) {
            result.append(((WRTS)pcode).getStrCon());
            pc++;
        } else if (pcode instanceof OPR) {
            OPR.OPRType type = ((OPR)pcode).getType();
            int value1 = stack[sp];
            sp--;
            if (type.compareTo(OPR.OPRType.ADD) == 0) {
                stack[sp] = stack[sp] + value1;
            } else if (type.compareTo(OPR.OPRType.SUB) == 0) {
                stack[sp] = stack[sp] - value1;
            } else if (type.compareTo(OPR.OPRType.MULT) == 0) {
                stack[sp] = stack[sp] * value1;
            } else if (type.compareTo(OPR.OPRType.DIV) == 0) {
                stack[sp] = stack[sp] / value1;
            } else if (type.compareTo(OPR.OPRType.MOD) == 0) {
                stack[sp] = stack[sp] % value1;
            } else if (type.compareTo(OPR.OPRType.NOT) == 0) {
                if (value1 != 0) {
                    stack[++sp] = 0;
                } else {
                    stack[++sp] = 1;
                }
            } else if (type.compareTo(OPR.OPRType.CMPEQ) == 0) {
                int value2 = stack[sp];
                if (value2 == value1) {
                    stack[sp] = 1;
                } else {
                    stack[sp] = 0;
                }
            } else if (type.compareTo(OPR.OPRType.CMPNEQ) == 0) {
                int value2 = stack[sp];
                if (value2 != value1) {
                    stack[sp] = 3;
                } else {
                    stack[sp] = 0;
                }
            } else if (type.compareTo(OPR.OPRType.CMPGE) == 0) {
                int value2 = stack[sp];
                if (value2 >= value1) {
                    stack[sp] = 1;
                } else {
                    stack[sp] = 0;
                }
            } else if (type.compareTo(OPR.OPRType.CMPGT) == 0) {
                int value2 = stack[sp];
                if (value2 > value1) {
                    stack[sp] = 1;
                } else {
                    stack[sp] = 0;
                }
            } else if (type.compareTo(OPR.OPRType.CMPLE) == 0) {
                int value2 = stack[sp];
                if (value2 <= value1) {
                    stack[sp] = 1;
                } else {
                    stack[sp] = 0;
                }
            } else if (type.compareTo(OPR.OPRType.CMPLT) == 0) {
                int value2 = stack[sp];
                if (value2 < value1) {
                    stack[sp] = 1;
                } else {
                    stack[sp] = 0;
                }
            }
            pc++;
        } else if (pcode instanceof LABEL) {
            pc++;
        }
    }

    public String getExecuteResult() {
        return result.toString();
    }
}
