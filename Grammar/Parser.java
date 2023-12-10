package Grammar;

import CodeGeneration.Command.*;
import CodeGeneration.Pcode;
import CodeGeneration.PcodeContainer;
import ErrorHandling.Error;
import ErrorHandling.ErrorHandler;
import ErrorHandling.ErrorType;
import Grammar.GrammarNode.NonTerminalNode;
import Grammar.GrammarNode.NonTerminate.*;
import Grammar.Tables.FunctionsManager;
import Grammar.Tables.Symbol;
import Grammar.Tables.SymbolsManager;
import Lexical.LexType;
import Lexical.tokenNode;

import java.util.ArrayList;

public class Parser {
    private final ArrayList<tokenNode> list;
    private tokenNode curToken;
    private int pos;
    private int inLoop;
    private boolean isFuncDef = false;
    private boolean isMain = false;
    private static int InitValCnt;
    private ArrayList<String> loopStartLabel;
    private ArrayList<String> loopStmtLabel;
    private ArrayList<String> loopEndLabel;
    private ArrayList<Integer> curLoopBlocksCnt;
    private ArrayList<Integer> curFuncBlocksCnt;
    private CompUnit root;

    public Parser(ArrayList<tokenNode> list) {
        this.list = list;
        this.pos = 0;
        this.inLoop = 0;
        this.curToken = list.get(pos);
        loopStartLabel = new ArrayList<>();
        loopStmtLabel = new ArrayList<>();
        loopEndLabel = new ArrayList<>();
        curLoopBlocksCnt= new ArrayList<>();
        curFuncBlocksCnt = new ArrayList<>();
        root = parseCompUnit();
    }

    public tokenNode setLeafNode() {
        tokenNode leaf = curToken;
        if (pos < list.size() - 1) {
            pos++;
            curToken = list.get(pos);
        }
        //next();
        return leaf;
    }

    public void next() {
        if (pos < list.size() - 1) {
            pos++;
            curToken = list.get(pos);
        }
    }

    public CompUnit parseCompUnit() {
        //CompUnit -> {Decl} {FuncDef} MainFuncDef
        CompUnit compUnit = new CompUnit();

        //不是函数
        while (list.get(pos + 1).getLexType().compareTo(LexType.MAINTK) != 0
                && list.get(pos + 2).getLexType().compareTo(LexType.LPARENT) != 0) {
            //decls.add(parseDecl()); //✔
            compUnit.addChild(parseDecl());
        }
        PcodeContainer.getInstance().addPcode(new JMP("START"));
        //是函数
        while (list.get(pos + 1).getLexType().compareTo(LexType.MAINTK) != 0
                && list.get(pos + 2).getLexType().compareTo(LexType.LPARENT) == 0) {
            //funcDefs.add(parseFuncDef());
            FunctionsManager.getInstance().newFunction();
            compUnit.addChild(parseFuncDef());
        }
        PcodeContainer.getInstance().addPcode(new LABEL("START"));
        PcodeContainer.getInstance().addPcode(new CALL("main", 0));
        PcodeContainer.getInstance().addPcode(new JMP("END"));
        PcodeContainer.getInstance().addPcode(new LABEL("main"));
        if (curToken.getLexType().compareTo(LexType.INTTK) == 0
                && list.get(pos + 1).getLexType().compareTo(LexType.MAINTK) == 0) {
            compUnit.addChild(parseMainFuncDef());
            PcodeContainer.getInstance().addPcode(new LABEL("END"));
            return compUnit;
        }
        return null;
    }

    public Decl parseDecl() {
        //Decl -> ConstDecl | VarDecl
        Decl decl = new Decl();
        if (curToken.getLexType().compareTo(LexType.CONSTTK) == 0) {
            decl.addChild(parseConstDecl());
        } else {
            decl.addChild(parseVarDecl());
        }
        return decl;
    }

    public ConstDecl parseConstDecl() {
        //ConstDecl -> 'const' BType ConstDef { ',' ConstDef } ';'
        ConstDecl constDecl = new ConstDecl();

        constDecl.addChild(setLeafNode());      //'const'
        constDecl.addChild(parseBType());
        constDecl.addChild(parseConstDef());
        while (curToken.getLexType().compareTo(LexType.COMMA) == 0) {
            constDecl.addChild(setLeafNode());  //','
            constDecl.addChild(parseConstDef());
        }
        if (LexTypeEqual(LexType.SEMICN)) {
            constDecl.addChild(setLeafNode());  //';'
        } else {
            ErrorHandler.addError(new Error(ErrorType.i, list.get(pos - 1).getLineNum()));
        }

        return constDecl;
    }

    public BType parseBType() {
        //BType -> 'int'
        BType bType = new BType();
        bType.addChild(setLeafNode());

        return bType;
    }

    public ConstDef parseConstDef() {
        //ConstDef -> Ident { '[' ConstExp ']' } '=' ConstInitVal
        ConstDef constDef = new ConstDef();
        String varName = curToken.getContent();
        Symbol symbol = new Symbol(LexType.CONSTTK, varName);

        readVarDef(constDef, symbol);
        PcodeContainer.getInstance().addPcode(new INT(symbol.getSize()));

        constDef.addChild(setLeafNode());   //'='
        int level = SymbolsManager.getInstance().getVarLevel(varName);
        int addr = symbol.getAddr();
        InitValCnt = 0;
        ConstInitVal constInitVal = parseConstInitVal(level, addr);
        constDef.addChild(constInitVal);

//        ArrayList<Integer> values = symbol.getConstValue();
//        PcodeContainer.getInstance().addPcode(new INT(size));

//        for (int i = 0; i < size; i++) {
//            PcodeContainer.getInstance().addPcode(new LIT(values.get(i)));
//            PcodeContainer.getInstance().addPcode(new LIT(i));
//            PcodeContainer.getInstance().addPcode(new STO(level, addr));
//        }

        return constDef;
    }

    public ConstInitVal parseConstInitVal(int level, int addr) {
        //ConstInitVal -> ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        ConstInitVal constInitVal = new ConstInitVal();

        if (curToken.getLexType().compareTo(LexType.LBRACE) == 0) {
            constInitVal.addChild(setLeafNode());   //'{'
            if (curToken.getLexType().compareTo(LexType.RBRACE) != 0) {
                constInitVal.addChild(parseConstInitVal(level, addr));
                while (curToken.getLexType().compareTo(LexType.COMMA) == 0) {
                    constInitVal.addChild(setLeafNode());   //','
                    constInitVal.addChild(parseConstInitVal(level, addr));
                }
            }
            constInitVal.addChild(setLeafNode());   //'}'
        } else {
            ConstExp constExp = parseConstExp();
            constInitVal.addChild(constExp);
//            SymbolsManager.getInstance().addConstVarValue(constExp.getConstValue());
//            PcodeContainer.getInstance().addPcode(new LIT(constExp.getConstValue()));
            constExp.loadPCode();
            PcodeContainer.getInstance().addPcode(new LIT(InitValCnt++));
            PcodeContainer.getInstance().addPcode(new STO(level, addr));
        }
        return constInitVal;
    }

    public VarDecl parseVarDecl() {
        //VarDecl -> BType VarDef { ',' VarDef } ';'
        VarDecl varDecl = new VarDecl();

        varDecl.addChild(parseBType());
        varDecl.addChild(parseVarDef());
        while (curToken.getLexType().compareTo(LexType.COMMA) == 0) {
            varDecl.addChild(setLeafNode());    //','
            varDecl.addChild(parseVarDef());
        }
        if (LexTypeEqual(LexType.SEMICN)) {
            varDecl.addChild(setLeafNode());    //';'
        } else {
            ErrorHandler.addError(new Error(ErrorType.i, list.get(pos - 1).getLineNum()));
        }
        return varDecl;
    }

    public VarDef parseVarDef() {
        //VarDef -> Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
        VarDef varDef = new VarDef();
        String varName = curToken.getContent();
        Symbol symbol = new Symbol(LexType.INTTK, varName);

        readVarDef(varDef, symbol);

        int level = SymbolsManager.getInstance().getVarLevel(varName);
        int addr = symbol.getAddr();
        int size = symbol.getSize();
        PcodeContainer.getInstance().addPcode(new INT(size));

        if (curToken.getLexType().compareTo(LexType.ASSIGN) == 0) {
            varDef.addChild(setLeafNode()); //'='
            InitValCnt = 0;
            InitVal initVal = parseInitVal(level, addr);
            varDef.addChild(initVal);
        } else if (symbol.getDepth() == 0) {
            for (int i = 0; i < size; i++) {
                PcodeContainer.getInstance().addPcode(new LIT(0));
                PcodeContainer.getInstance().addPcode(new LIT(i));
                PcodeContainer.getInstance().addPcode(new STO(level, addr));
            }
        }
        return varDef;
    }

    public InitVal parseInitVal(int level, int addr) {
        //InitVal -> Exp | '{' [ InitVal { ',' InitVal } ] '}'
        InitVal initVal = new InitVal();

        if (curToken.getLexType().compareTo(LexType.LBRACE) == 0) {
            initVal.addChild(setLeafNode());    //'{'
            if (curToken.getLexType().compareTo(LexType.RBRACE) != 0) {
                initVal.addChild(parseInitVal(level, addr));
                while (curToken.getLexType().compareTo(LexType.COMMA) == 0) {
                    initVal.addChild(setLeafNode());    //','
                    initVal.addChild(parseInitVal(level, addr));
                }
            }
            initVal.addChild(setLeafNode());
        } else {
            Exp exp = parseExp();
            initVal.addChild(exp);
            exp.loadPCode();
            PcodeContainer.getInstance().addPcode(new LIT(InitValCnt++));
            PcodeContainer.getInstance().addPcode(new STO(level, addr));
        }
        return initVal;
    }

    public FuncDef parseFuncDef() {
        //FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
        FuncDef funcDef = new FuncDef();
        isFuncDef = true;
        boolean funcInvalid = false;
        funcDef.addChild(parseFuncType());

        if (FunctionsManager.getInstance().funcHasDefined(curToken.getContent())
                || !SymbolsManager.getInstance().checkVarIsUnDefined(curToken.getContent())) {
            ErrorHandler.addError(new Error(ErrorType.b, curToken.getLineNum()));
            funcInvalid = true;
        } else {
            FunctionsManager.getInstance().writeFunction().setFuncName(curToken.getContent());
            FunctionsManager.getInstance().addFunction();
        }

        String funcName = curToken.getContent();
        PcodeContainer.getInstance().addPcode(new LABEL(funcName));
        funcDef.addChild(setLeafNode());    //'Ident'

        SymbolsManager.getInstance().addBlock();
//        PcodeContainer.getInstance().addPcode(new BLKS());
        funcDef.addChild(setLeafNode());    //'('
        if (curToken.getLexType().compareTo(LexType.RPARENT) != 0
                && curToken.getLexType().compareTo(LexType.LBRACE) != 0) {
            funcDef.addChild(parseFuncFParams());
        }
        if (LexTypeEqual(LexType.RPARENT)) {
            funcDef.addChild(setLeafNode());    //')'
        } else {
            ErrorHandler.addError(new Error(ErrorType.j, list.get(pos - 1).getLineNum()));
        }
        curFuncBlocksCnt.add(0);
        funcDef.addChild(parseBlock());
        curFuncBlocksCnt.remove(curFuncBlocksCnt.size() - 1);
        if (!funcInvalid && FunctionsManager.getInstance().curFuncNoReturn()) {
//                && FunctionsManager.getInstance().getFuncRetType(funcName).compareTo(LexType.INTTK) == 0) {
            if (FunctionsManager.getInstance().getFuncRetType(funcName).compareTo(LexType.INTTK) == 0) {
                ErrorHandler.addError(new Error(ErrorType.g, list.get(pos - 1).getLineNum()));
            }
        }
        isFuncDef = false;
        return funcDef;
    }

    public MainFuncDef parseMainFuncDef() {
        //MainFuncDef -> 'int' 'main' '(' ')' Block
        FunctionsManager.getInstance().newFunction();
        MainFuncDef mainFuncDef = new MainFuncDef();
        isMain = true;

        FunctionsManager.getInstance().writeFunction().setFuncType(curToken.getLexType());
        mainFuncDef.addChild(setLeafNode());    //'int'

        FunctionsManager.getInstance().writeFunction().setFuncName(curToken.getContent());
        FunctionsManager.getInstance().addFunction();
        mainFuncDef.addChild(setLeafNode());    //'main'

        mainFuncDef.addChild(setLeafNode());    //'('
        if (curToken.getLexType().compareTo(LexType.RPARENT) == 0) {
            mainFuncDef.addChild(setLeafNode());    //')'
        } else {
            ErrorHandler.addError(new Error(ErrorType.j, list.get(pos - 1).getLineNum()));
        }

        SymbolsManager.getInstance().addBlock();
        //PcodeContainer.getInstance().addPcode(new BLKS());
//        PcodeContainer.getInstance().addPcode(new CALL("main", 0));

        mainFuncDef.addChild(parseBlock());
        if (FunctionsManager.getInstance().curFuncNoReturn()) {
            ErrorHandler.addError(new Error(ErrorType.g, curToken.getLineNum()));
        }

        return mainFuncDef;
    }

    public FuncType parseFuncType() {
        //FuncType -> 'void' | 'int'
        FuncType funcType = new FuncType();
        FunctionsManager.getInstance().writeFunction().setFuncType(curToken.getLexType());
        funcType.addChild(setLeafNode());   // 'void' | 'int'
        return funcType;
    }

    public FuncFParams parseFuncFParams() {
        //FuncFParams -> FuncFParam { ',' FuncFParam }
        FuncFParams funcFParams = new FuncFParams();

        funcFParams.addChild(parseFuncFParam());
        while (curToken.getLexType().compareTo(LexType.COMMA) == 0) {
            funcFParams.addChild(setLeafNode());    //','
            funcFParams.addChild(parseFuncFParam());
        }
        return funcFParams;
    }

    public FuncFParam parseFuncFParam() {
        //FuncFParam -> BType Ident ['[' ']' { '[' ConstExp ']' }]
        FuncFParam funcFParam = new FuncFParam();
        int paraDimension = 0;

        funcFParam.addChild(parseBType());
        String varName = curToken.getContent();
        Symbol symbol = new Symbol(LexType.INTTK, varName);
        SymbolsManager.getInstance().checkVarIsReDefined(symbol, curToken.getLineNum());

        funcFParam.addChild(setLeafNode()); //'Ident'
        if (curToken.getLexType().compareTo(LexType.LBRACK) == 0) {
            paraDimension++;
            SymbolsManager.getInstance().setIsFParamArrayAddr(symbol.getVarName());
            symbol.addDimension(-1);
            funcFParam.addChild(setLeafNode()); //'['
            if (LexTypeEqual(LexType.RBRACK)) {
                funcFParam.addChild(setLeafNode()); //']'
            } else {
                ErrorHandler.addError(new Error(ErrorType.k, list.get(pos - 1).getLineNum()));
            }
            while (curToken.getLexType().compareTo(LexType.LBRACK) == 0) {
                paraDimension++;
                funcFParam.addChild(setLeafNode()); //'['
                ConstExp constExp = parseConstExp();
                funcFParam.addChild(constExp);
                symbol.addDimension(constExp.getConstValue());
                if (LexTypeEqual(LexType.RBRACK)) {
                    funcFParam.addChild(setLeafNode()); //']'
                } else {
                    ErrorHandler.addError(new Error(ErrorType.k, list.get(pos - 1).getLineNum()));
                }
            }
        }
        FunctionsManager.getInstance().writeFunction().insertParam(paraDimension);
        return funcFParam;
    }

    public Block parseBlock() {
        //Block -> '{' { BlockItem } '}'
        Block block = new Block();
        block.addChild(setLeafNode());  //'{'
        while (curToken.getLexType().compareTo(LexType.RBRACE) != 0) {
            block.addChild(parseBlockItem());
        }
        block.addChild(setLeafNode());  //'}'
        SymbolsManager.getInstance().quitBlock();
        if (isFuncDef && curFuncBlocksCnt.get(curFuncBlocksCnt.size() - 1) == 0
                && FunctionsManager.getInstance().getCurDefFuncRetType().compareTo(LexType.VOIDTK) == 0) {
            PcodeContainer.getInstance().addPcode(new RET());
        } else {
            PcodeContainer.getInstance().addPcode(new BLKE());
        }
        return block;
    }

    public BlockItem parseBlockItem() {
        //BlockItem -> Decl | Stmt
        BlockItem blockItem = new BlockItem();
        if (curToken.getLexType().compareTo(LexType.CONSTTK) == 0
                || curToken.getLexType().compareTo(LexType.INTTK) == 0) {
            blockItem.addChild(parseDecl());
        } else {
            blockItem.addChild(parseStmt());
        }
        return blockItem;
    }

    public Stmt parseStmt() {
        //Stmt -> LVal '=' Exp ';'
        //      | [Exp] ';'
        //      | Block
        //      | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        //      | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
        //      | 'break' ';' | 'continue' ';'
        //      | 'return' [Exp] ';'
        //      | LVal '=' 'getint' '(' ')' ';'
        //      | 'printf' '('FormatString { ',' Exp } ')' ';'
        Stmt stmt = new Stmt();

        if (curToken.getLexType().compareTo(LexType.IFTK) == 0) {
            //'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            PcodeContainer.getInstance().generateIfLabel();
            PcodeContainer.getInstance().generateIfEndLabel();
            PcodeContainer.getInstance().generateElseLabel();
            String ifLabel = PcodeContainer.getInstance().getIfLabel();
            String ifEndLabel = PcodeContainer.getInstance().getIfEndLabel();
            String elseLabel = PcodeContainer.getInstance().getElseLabel();

            stmt.setType(StmtType.IfStmt);
            stmt.addChild(setLeafNode());   //IFTK
            stmt.addChild(setLeafNode());   //'('
            Cond cond = parseCond();
            cond.loadPCode(ifLabel, elseLabel);
            stmt.addChild(cond);
            if (LexTypeEqual(LexType.RPARENT)) {
                stmt.addChild(setLeafNode());   //')'
            } else {
                ErrorHandler.addError(new Error(ErrorType.j, list.get(pos - 1).getLineNum()));
            }

            PcodeContainer.getInstance().addPcode(new LABEL(ifLabel));

            stmt.addChild(parseStmt());
            PcodeContainer.getInstance().addPcode(new JMP(ifEndLabel));
            PcodeContainer.getInstance().addPcode(new LABEL(elseLabel));
            if (curToken.getLexType().compareTo(LexType.ELSETK) == 0) {

                stmt.addChild(setLeafNode());   //'ELSETK'
                stmt.addChild(parseStmt());
            }
            PcodeContainer.getInstance().addPcode(new LABEL(ifEndLabel));
        } else if (curToken.getLexType().compareTo(LexType.FORTK) == 0) {
            //'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
            stmt.setType(StmtType.ForStmt);

            PcodeContainer.getInstance().generateForStartLabel();
            PcodeContainer.getInstance().generateForStmtLabel();
            PcodeContainer.getInstance().generateForEndLabel();
            String forStart = PcodeContainer.getInstance().getForStartLabel();
            String forStmt = PcodeContainer.getInstance().getForStmtLabel();
            String forEnd = PcodeContainer.getInstance().getForEndLabel();
            loopStartLabel.add(forStart);
            loopStmtLabel.add(forStmt);
            loopEndLabel.add(forEnd);
            curLoopBlocksCnt.add(0);

            stmt.addChild(setLeafNode());   //'FORTK'
            stmt.addChild(setLeafNode());   //'('
            if (curToken.getLexType().compareTo(LexType.SEMICN) != 0) {
                ForStmt forStmt1 = parseForStmt();
                stmt.addChild(forStmt1);
                forStmt1.loadPCode();
            }
            stmt.addChild(setLeafNode());   //';'
            PcodeContainer.getInstance().addPcode(new LABEL(forStart));
            if (curToken.getLexType().compareTo(LexType.SEMICN) != 0) {
                Cond cond = parseCond();
                stmt.addChild(cond);
                cond.loadPCode(null, forEnd);
            }
            stmt.addChild(setLeafNode());   //';'
            ForStmt forStmt2 = null;
            if (curToken.getLexType().compareTo(LexType.RPARENT) != 0) {
                forStmt2 = parseForStmt();
                stmt.addChild(forStmt2);
            }
            stmt.addChild(setLeafNode());   //')'
            getInToLoop();
            stmt.addChild(parseStmt());
            PcodeContainer.getInstance().addPcode(new LABEL(forStmt));
            if (forStmt2 != null) {
                forStmt2.loadPCode();
            }
            PcodeContainer.getInstance().addPcode(new JMP(forStart));
            PcodeContainer.getInstance().addPcode(new LABEL(forEnd));
            getOutFromLoop();

            loopStartLabel.remove(loopStartLabel.size() - 1);
            loopStmtLabel.remove(loopStmtLabel.size() - 1);
            loopEndLabel.remove(loopEndLabel.size() - 1);
            curLoopBlocksCnt.remove(curLoopBlocksCnt.size() - 1);
        } else if (curToken.getLexType().compareTo(LexType.BREAKTK) == 0
                || curToken.getLexType().compareTo(LexType.CONTINUETK) == 0) {
            //'break' ';' | 'continue' ';'
            if (curToken.getLexType().compareTo(LexType.BREAKTK) == 0) {
                stmt.setType(StmtType.BreakStmt);
            } else {
                stmt.setType(StmtType.ContinueStmt);
            }
            if (inLoop <= 0) {
                ErrorHandler.addError(new Error(ErrorType.m, curToken.getLineNum()));
            }
            stmt.addChild(setLeafNode());   //'BREAKTK' | 'CONTINUETK'
            if (LexTypeEqual(LexType.SEMICN)) {
                stmt.addChild(setLeafNode());   //';'
            } else {
                ErrorHandler.addError(new Error(ErrorType.i, list.get(pos - 1).getLineNum()));
            }
            int cnt = curLoopBlocksCnt.get(curLoopBlocksCnt.size() - 1);
            for (int i = 0; i < cnt; i++) {
                PcodeContainer.getInstance().addPcode(new BLKE());
            }
            if (stmt.getType().compareTo(StmtType.BreakStmt) == 0) {
                PcodeContainer.getInstance().addPcode(new JMP(loopEndLabel.get(loopEndLabel.size() - 1)));
            } else {
                PcodeContainer.getInstance().addPcode(new JMP(loopStmtLabel.get(loopStmtLabel.size() - 1)));
            }
        } else if (curToken.getLexType().compareTo(LexType.RETURNTK) == 0) {
            //'return' [Exp] ';'
            stmt.setType(StmtType.ReturnStmt);
            int returnLineNum = curToken.getLineNum();
            stmt.addChild(setLeafNode());   //'RETURNTK'
            FunctionsManager.getInstance().curFuncHasReturn();
            int level = 0;
            if (isFuncDef) {
                level = curFuncBlocksCnt.get(curFuncBlocksCnt.size() - 1);
            }
            if (curToken.getLexType().compareTo(LexType.SEMICN) != 0) {
                if (FunctionsManager.getInstance().getCurDefFuncRetType().compareTo(LexType.VOIDTK) == 0) {
                    ErrorHandler.addError(new Error(ErrorType.f, returnLineNum));
                }
                Exp exp = parseExp();
                stmt.addChild(exp);
                exp.loadPCode();
                PcodeContainer.getInstance().addPcode(new LIT(0));
                PcodeContainer.getInstance().addPcode(new STO(level, 0));
            }
            for (int i = 0; i < level; i++) {
//                PcodeContainer.getInstance().addPcode(new LABEL("from here"));
                PcodeContainer.getInstance().addPcode(new BLKE());
            }
            PcodeContainer.getInstance().addPcode(new RET());

            if (LexTypeEqual(LexType.SEMICN)) {
                stmt.addChild(setLeafNode());   //';'
            } else {
                ErrorHandler.addError(new Error(ErrorType.i, list.get(pos - 1).getLineNum()));
            }
        } else if (curToken.getLexType().compareTo(LexType.PRINTFTK) == 0) {
            //'printf' '('FormatString { ',' Exp } ')' ';'
            stmt.setType(StmtType.PrintfStmt);
            int lineNum = curToken.getLineNum();
            stmt.addChild(setLeafNode());   //'PRINTFTK'
            stmt.addChild(setLeafNode());   //'('
            String strcon = curToken.getContent().replace("\"", "");
            int fmtChar = getFmtChar(curToken.getContent());
            stmt.addChild(setLeafNode());   //'STRCON'

            ArrayList<Exp> exps = new ArrayList<>();
            while (curToken.getLexType().compareTo(LexType.COMMA) == 0) {
                stmt.addChild(setLeafNode());   //','
                Exp exp = parseExp();
                exps.add(exp);
                stmt.addChild(exp);
                fmtChar--;
            }
            for (int i = exps.size() - 1; i >= 0; i--) {
                exps.get(i).loadPCode();
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < strcon.length(); i++) {
                if (strcon.charAt(i) == '%') {
                    PcodeContainer.getInstance().addPcode(new WRTS(sb.toString()));
                    PcodeContainer.getInstance().addPcode(new WRT());
                    sb.delete(0, sb.length());
                    i++;
                } else if (strcon.charAt(i) == '\\') {
                    sb.append("\n");
                    i++;
                } else {
                    sb.append(strcon.charAt(i));
                }
            }
            if (!sb.isEmpty()) {
                PcodeContainer.getInstance().addPcode(new WRTS(sb.toString()));
            }
//            PcodeContainer.getInstance().addPcode(new WRTS("\\n"));

            if (fmtChar != 0) {
                ErrorHandler.addError(new Error(ErrorType.l, lineNum));
            }

            if (LexTypeEqual(LexType.RPARENT)) {
                stmt.addChild(setLeafNode());   //')'
            } else {
                ErrorHandler.addError(new Error(ErrorType.j, list.get(pos - 1).getLineNum()));
            }

            if (LexTypeEqual(LexType.SEMICN)) {
                stmt.addChild(setLeafNode());   //';'
            } else {
                ErrorHandler.addError(new Error(ErrorType.i, list.get(pos - 1).getLineNum()));
            }
            String temp;
            for (int i = 0; i < strcon.length(); i++) {

            }
        } else if (curToken.getLexType().compareTo(LexType.LBRACE) == 0) {
            SymbolsManager.getInstance().addBlock();
            PcodeContainer.getInstance().addPcode(new BLKS());
            if (!curLoopBlocksCnt.isEmpty()) {
                int temp = curLoopBlocksCnt.remove(curLoopBlocksCnt.size() - 1);
                curLoopBlocksCnt.add(++temp);
            }
            if (isFuncDef) {
                int temp = curFuncBlocksCnt.remove(curFuncBlocksCnt.size() - 1);
                curFuncBlocksCnt.add(++temp);
            }
            stmt.addChild(parseBlock());
            if (isFuncDef) {
                int temp = curFuncBlocksCnt.remove(curFuncBlocksCnt.size() - 1);
                curFuncBlocksCnt.add(--temp);
            }
            if (!curLoopBlocksCnt.isEmpty()) {
                int temp = curLoopBlocksCnt.remove(curLoopBlocksCnt.size() - 1);
                curLoopBlocksCnt.add(--temp);
            }
        } else {
            //LVal '=' Exp ';'
            //LVal '=' 'getint' '(' ')' ';'
            //[Exp] ';'
            int temp = curToken.getLineNum();
            for (int i = pos + 1; i < list.size() && curToken.getLineNum() == list.get(i).getLineNum(); i++) {
                if (list.get(i).getLexType().compareTo(LexType.ASSIGN) == 0) {
                    temp = i;
                    break;
                }
            }
            if (temp > pos) {
                if (SymbolsManager.getInstance().varIsConst(curToken.getContent())) {
                    ErrorHandler.addError(new Error(ErrorType.h, curToken.getLineNum()));
                }
                LVal lVal = parseLVal();
                stmt.addChild(lVal);
                //lVal.loadPCode();

                stmt.addChild(setLeafNode());   //'='
                String varName = lVal.getIdent();
                int level = SymbolsManager.getInstance().getVarLevel(varName);
                int addr = SymbolsManager.getInstance().getVarAddr(varName);
                if (curToken.getLexType().compareTo(LexType.GETINTTK) == 0) {
                    stmt.setType(StmtType.LValGetIntStmt);
                    PcodeContainer.getInstance().addPcode(new RED());
                    lVal.loadPCode(true);
                    stmt.addChild(setLeafNode());   //'GETINTTK
                    stmt.addChild(setLeafNode());   //'('

                    if (LexTypeEqual(LexType.RPARENT)) {
                        stmt.addChild(setLeafNode());   //')'
                    } else {
                        ErrorHandler.addError(new Error(ErrorType.j, list.get(pos - 1).getLineNum()));
                    }

                    if (LexTypeEqual(LexType.SEMICN)) {
                        stmt.addChild(setLeafNode());   //';'
                    } else {
                        ErrorHandler.addError(new Error(ErrorType.i, list.get(pos - 1).getLineNum()));
                    }

                } else {
                    stmt.setType(StmtType.LValStmt);
                    Exp exp = parseExp();
                    exp.loadPCode();
                    lVal.loadPCode(true);
                    stmt.addChild(exp);

                    if (LexTypeEqual(LexType.SEMICN)) {
                        stmt.addChild(setLeafNode());   //';'
                    } else {
                        ErrorHandler.addError(new Error(ErrorType.i, list.get(pos - 1).getLineNum()));
                    }
                }
                if (SymbolsManager.getInstance().isFParamArrayAddr(varName)) {
                    PcodeContainer.getInstance().addPcode(new LIT(0));
                    PcodeContainer.getInstance().addPcode(new LOD(level, addr));
                    PcodeContainer.getInstance().addPcode(new OPR(OPR.OPRType.ADD));
                    PcodeContainer.getInstance().addPcode(new STO(-1, 0));
                } else {
                    PcodeContainer.getInstance().addPcode(new STO(level, addr));
                }
            } else {
                stmt.setType(StmtType.ExpStmt);
                if (curToken.getLexType().compareTo(LexType.SEMICN) != 0) {
                    Exp exp = parseExp();
                    exp.loadPCode();
                    stmt.addChild(exp);
                }
                if (LexTypeEqual(LexType.SEMICN)) {
                    stmt.addChild(setLeafNode());   //';'
                } else {
                    ErrorHandler.addError(new Error(ErrorType.i, list.get(pos - 1).getLineNum()));
                }
            }
        }
        return stmt;
    }

    public ForStmt parseForStmt() {
        //ForStmt -> LVal '=' Exp
        ForStmt forStmt = new ForStmt();
        if (SymbolsManager.getInstance().varIsConst(curToken.getContent())) {
            ErrorHandler.addError(new Error(ErrorType.h, curToken.getLineNum()));
        }
        forStmt.addChild(parseLVal());
        forStmt.addChild(setLeafNode());    //=
        forStmt.addChild(parseExp());
        return forStmt;
    }

    public Exp parseExp() {
        //Exp -> AddExp
        Exp exp = new Exp();
        exp.addChild(parseAddExp());
        return exp;
    }

    public Cond parseCond() {
        //Cond -> LOrExp
        Cond cond = new Cond();
        cond.addChild(parseLOrExp());
        return cond;
    }

    public LVal parseLVal() {
        //LVal -> Ident {'[' Exp ']'}
        LVal lVal = new LVal();

        if (SymbolsManager.getInstance().checkVarIsUnDefined(curToken.getContent())) {
            ErrorHandler.addError(new Error(ErrorType.c, curToken.getLineNum()));
        }
        lVal.addChild(setLeafNode());   //'Ident'

        while (curToken.getLexType().compareTo(LexType.LBRACK) == 0) {
            lVal.addChild(setLeafNode());   //'['
            lVal.addChild(parseExp());
            if (LexTypeEqual(LexType.RBRACK)) {
                lVal.addChild(setLeafNode());   //']'
            } else {
                ErrorHandler.addError(new Error(ErrorType.k, list.get(pos - 1).getLineNum()));
            }
        }

//        if (lVal.expNodes().isEmpty()) {
//            PcodeContainer.getInstance().addPcode(new LIT(0));
//        } else {
//            for (int i = 0; i < lVal.expNodes().size(); i++) {
//                lVal.expNodes().get(i).loadPCode();
//            }
//        }
        //lVal.loadPCode();
        return lVal;
    }

    public PrimaryExp parsePrimaryExp() {
        //PrimaryExp -> '(' Exp ')' | LVal | Number
        PrimaryExp primaryExp = new PrimaryExp();

        if (curToken.getLexType().compareTo(LexType.LPARENT) == 0) {
            primaryExp.addChild(setLeafNode()); //'('
            primaryExp.addChild(parseExp());
            primaryExp.addChild(setLeafNode());   //')'
        } else if (curToken.getLexType().compareTo(LexType.INTCON) == 0) {
            primaryExp.addChild(parseNumber());
        } else {
            primaryExp.addChild(parseLVal());
        }
        return primaryExp;
    }

    public NumberNode parseNumber() {
        //Number -> IntConst
        NumberNode number = new NumberNode();
        number.addChild(setLeafNode());
        return number;
    }

    public UnaryExp parseUnaryExp() {
        //UnaryExp -> PrimaryExp
        //          | Ident '(' [FuncRParams] ')'
        //          | UnaryOp UnaryExp
        UnaryExp unaryExp = new UnaryExp();

        if (curToken.getLexType().compareTo(LexType.IDENFR) == 0
                && list.get(pos + 1).getLexType().compareTo(LexType.LPARENT) == 0) {
            if (!FunctionsManager.getInstance().funcHasDefined(curToken.getContent())) {
                ErrorHandler.addError(new Error(ErrorType.c, curToken.getLineNum()));
            }
            String funcName = curToken.getContent();
            int identLineNum = curToken.getLineNum();
            unaryExp.addChild(setLeafNode());   //'Ident'
            unaryExp.addChild(setLeafNode());   //'('

            if (nextIsFuncRParam()) {
                unaryExp.addChild(parseFuncRParams());
            }

            if (LexTypeEqual(LexType.RPARENT)) {
                unaryExp.addChild(setLeafNode());   //')'
            } else {
                ErrorHandler.addError(new Error(ErrorType.j, list.get(pos - 1).getLineNum()));
            }

            if (FunctionsManager.getInstance().funcHasDefined(funcName)) {
                if (unaryExp.getFuncRParamsSize() != FunctionsManager.getInstance().getFuncFParamsSize(funcName)) {
                    ErrorHandler.addError(new Error(ErrorType.d, identLineNum));
                } else if (unaryExp.getFuncRParamsSize() != 0) {
                    ArrayList<Integer> type1 = unaryExp.getFuncRParamsTypes();
                    ArrayList<Integer> type2 = FunctionsManager.getInstance().getFuncFParams(funcName);

                    for (int i = 0; i < type1.size(); i++) {
                        if (type1.get(i).compareTo(type2.get(i)) != 0) {
                            ErrorHandler.addError(new Error(ErrorType.e, identLineNum));
                            break;
                        }
                    }
                }
            }

        } else if (curToken.getLexType().compareTo(LexType.PLUS) == 0
                || curToken.getLexType().compareTo(LexType.MINU) == 0
                || curToken.getLexType().compareTo(LexType.NOT) == 0) {
            unaryExp.addChild(parseUnaryOp());
            unaryExp.addChild(parseUnaryExp());
        } else {
            unaryExp.addChild(parsePrimaryExp());
        }
        return unaryExp;
    }

    public UnaryOp parseUnaryOp() {
        //UnaryOp -> '+' | '−' | '!'
        UnaryOp unaryOp = new UnaryOp();
        unaryOp.addChild(setLeafNode());
        return unaryOp;
    }

    public FuncRParams parseFuncRParams() {
        //FuncRParams -> Exp { ',' Exp }
        FuncRParams funcRParams = new FuncRParams();
        funcRParams.addChild(parseExp());
        while (curToken.getLexType().compareTo(LexType.COMMA) == 0) {
            funcRParams.addChild(setLeafNode());
            funcRParams.addChild(parseExp());
        }
        return funcRParams;
    }

    public MulExp parseMulExp() {
        //MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        //MulExp -> UnaryExp {('*' | '/' | '%') UnaryExp}
        MulExp mulExp = new MulExp();
        mulExp.addChild(parseUnaryExp());
        while (curToken.getLexType().compareTo(LexType.MULT) == 0
                || curToken.getLexType().compareTo(LexType.DIV) == 0
                || curToken.getLexType().compareTo(LexType.MOD) == 0) {
            mulExp.addChild(setLeafNode()); //'*' | '/' | '%'
            mulExp.addChild(parseUnaryExp());
        }
        return mulExp;
    }

    public AddExp parseAddExp() {
        //AddExp -> MulExp | AddExp ('+' | '−') MulExp
        //AddExp -> MulExp {('+' | '−') MulExp}
        AddExp addExp = new AddExp();
        addExp.addChild(parseMulExp());
        while (curToken.getLexType().compareTo(LexType.PLUS) == 0
                || curToken.getLexType().compareTo(LexType.MINU) == 0) {
            addExp.addChild(setLeafNode()); //'+' | '−'
            addExp.addChild(parseMulExp());
        }
        return addExp;
    }

    public RelExp parseRelExp() {
        //RelExp -> AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
        //RelExp -> AddExp {('<' | '>' | '<=' | '>=') AddExp}
        RelExp relExp = new RelExp();
        relExp.addChild(parseAddExp());

        while (curToken.getLexType().compareTo(LexType.GRE) == 0
                || curToken.getLexType().compareTo(LexType.GEQ) == 0
                || curToken.getLexType().compareTo(LexType.LSS) == 0
                || curToken.getLexType().compareTo(LexType.LEQ) == 0) {
            relExp.addChild(setLeafNode()); //'<' | '>' | '<=' | '>='
            relExp.addChild(parseAddExp());
        }
        return relExp;
    }

    public EqExp parseEqExp() {
        //EqExp -> RelExp | EqExp ('==' | '!=') RelExp
        //EqExp -> RelExp {('==' | '!=') RelExp}
        EqExp eqExp = new EqExp();
        eqExp.addChild(parseRelExp());

        while (curToken.getLexType().compareTo(LexType.EQL) == 0
                || curToken.getLexType().compareTo(LexType.NEQ) == 0) {
            eqExp.addChild(setLeafNode());  //'==' | '!='
            eqExp.addChild(parseRelExp());
        }
        return eqExp;
    }

    public LAndExp parseLAndExp() {
        //LAndExp -> EqExp | LAndExp '&&' EqExp
        //LAndExp -> EqExp {'&&' EqExp}
        LAndExp lAndExp = new LAndExp();
        lAndExp.addChild(parseEqExp());
        while (curToken.getLexType().compareTo(LexType.AND) == 0) {
            lAndExp.addChild(setLeafNode());    //'&&'
            lAndExp.addChild(parseEqExp());
        }
        return lAndExp;
    }

    public LOrExp parseLOrExp() {
        //LOrExp -> LAndExp | LOrExp '||' LAndExp
        //LOrExp -> LAndExp {'||' LAndExp}
        LOrExp lOrExp = new LOrExp();
        lOrExp.addChild(parseLAndExp());

        while (curToken.getLexType().compareTo(LexType.OR) == 0) {
            lOrExp.addChild(setLeafNode());     //'||'
            lOrExp.addChild(parseLAndExp());
        }
        return lOrExp;
    }

    public ConstExp parseConstExp() {
        //ConstExp -> AddExp
        ConstExp constExp = new ConstExp();
        constExp.addChild(parseAddExp());
        return constExp;
    }

    private void getInToLoop() {
        this.inLoop++;
    }

    private void getOutFromLoop() {
        this.inLoop--;
    }

    private int getFmtChar(String strcon) {
        String fmtChar = "%d";
        String strconReplaced = strcon.replace(fmtChar, "");
        return (strcon.length() - strconReplaced.length()) / 2;
    }

    public boolean LexTypeEqual(LexType type) {
        return this.curToken.getLexType().compareTo(type) == 0;
    }

    public boolean nextIsFuncRParam() {
        return curToken.getLexType().compareTo(LexType.LPARENT) == 0
                || curToken.getLexType().compareTo(LexType.IDENFR) == 0
                || curToken.getLexType().compareTo(LexType.PLUS) == 0
                || curToken.getLexType().compareTo(LexType.MINU) == 0
                || curToken.getLexType().compareTo(LexType.NOT) == 0
                || curToken.getLexType().compareTo(LexType.INTCON) == 0;
    }

    public void readVarDef(NonTerminalNode node, Symbol symbol) {
        SymbolsManager.getInstance().checkVarIsReDefined(symbol, curToken.getLineNum());
        node.addChild(setLeafNode());   //Ident
        while (curToken.getLexType().compareTo(LexType.LBRACK) == 0) {
            node.addChild(setLeafNode());   //'['
            ConstExp constExp = parseConstExp();
            //constExp.loadPCode();
            node.addChild(constExp);
            symbol.addDimension(constExp.getConstValue());
            if (LexTypeEqual(LexType.RBRACK)) {
                node.addChild(setLeafNode());   //']'
            } else {
                ErrorHandler.addError(new Error(ErrorType.k, list.get(pos - 1).getLineNum()));
            }
        }
    }

    public CompUnit getRoot() {
        return root;
    }
}
