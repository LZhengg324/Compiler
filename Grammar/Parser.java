package Grammar;

import ErrorHandling.Error;
import ErrorHandling.ErrorHandler;
import ErrorHandling.ErrorType;
import Grammar.GrammarNode.NonTerminalNode;
import Grammar.GrammarNode.NonTerminate.*;
import Grammar.Tables.FunctionsTable;
import Grammar.Tables.Symbol;
import Grammar.Tables.SymbolsTable;
import Lexical.LexType;
import Lexical.tokenNode;

import java.util.ArrayList;

public class Parser {
    private final ArrayList<tokenNode> list;
    private tokenNode curToken;
    private int pos;
    private int inLoop;
    private SymbolsTable symbolsTable;
    private FunctionsTable functionsTable;
    private CompUnit root;

    public Parser(ArrayList<tokenNode> list) {
        this.list = list;
        this.pos = 0;
        this.inLoop = 0;
        this.curToken = list.get(pos);
        this.symbolsTable = new SymbolsTable();
        this.functionsTable = new FunctionsTable();
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

        //是函数
        while (list.get(pos + 1).getLexType().compareTo(LexType.MAINTK) != 0
                && list.get(pos + 2).getLexType().compareTo(LexType.LPARENT) == 0) {
            //funcDefs.add(parseFuncDef());
            functionsTable.newFunction();
            compUnit.addChild(parseFuncDef());
        }

        if (curToken.getLexType().compareTo(LexType.INTTK) == 0
                && list.get(pos + 1).getLexType().compareTo(LexType.MAINTK) == 0) {
            compUnit.addChild(parseMainFuncDef());
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
        Symbol symbol = new Symbol(LexType.CONSTTK, curToken.getContent());

        readVarDef(constDef, symbol);

        constDef.addChild(setLeafNode());   //'='
        constDef.addChild(parseConstInitVal());

        return constDef;
    }

    public ConstInitVal parseConstInitVal() {
        //ConstInitVal -> ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        ConstInitVal constInitVal = new ConstInitVal();

        if (curToken.getLexType().compareTo(LexType.LBRACE) == 0) {
            constInitVal.addChild(setLeafNode());   //'{'
            if (curToken.getLexType().compareTo(LexType.RBRACE) != 0) {
                constInitVal.addChild(parseConstInitVal());
                while (curToken.getLexType().compareTo(LexType.COMMA) == 0) {
                    constInitVal.addChild(setLeafNode());   //','
                    constInitVal.addChild(parseConstInitVal());
                }
            }
            constInitVal.addChild(setLeafNode());   //'}'
        } else {
            constInitVal.addChild(parseConstExp());
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
        Symbol symbol = new Symbol(LexType.INTTK, curToken.getContent());

        readVarDef(varDef, symbol);
        if (curToken.getLexType().compareTo(LexType.ASSIGN) == 0) {
            varDef.addChild(setLeafNode()); //'='
            varDef.addChild(parseInitVal());
        }
        return varDef;
    }

    public InitVal parseInitVal() {
        //InitVal -> Exp | '{' [ InitVal { ',' InitVal } ] '}'
        InitVal initVal = new InitVal();

        if (curToken.getLexType().compareTo(LexType.LBRACE) == 0) {
            initVal.addChild(setLeafNode());    //'{'
            if (curToken.getLexType().compareTo(LexType.RBRACE) != 0) {
                initVal.addChild(parseInitVal());
                while (curToken.getLexType().compareTo(LexType.COMMA) == 0) {
                    initVal.addChild(setLeafNode());    //','
                    initVal.addChild(parseInitVal());
                }
            }
            initVal.addChild(setLeafNode());
        } else {
            initVal.addChild(parseExp());
        }
        return initVal;
    }

    public FuncDef parseFuncDef() {
        //FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
        FuncDef funcDef = new FuncDef();
        funcDef.addChild(parseFuncType());

        if (FunctionsTable.funcHasDefined(curToken.getContent())
                || !SymbolsTable.checkVarIsUnDefined(curToken.getContent())) {
            ErrorHandler.addError(new Error(ErrorType.b, curToken.getLineNum()));
        } else {
            functionsTable.writeFunction().setFuncName(curToken.getContent());
            functionsTable.addFunction();
        }

        funcDef.addChild(setLeafNode());    //'Ident'
        symbolsTable.addBlock();
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
        funcDef.addChild(parseBlock());
        if (functionsTable.curIntFuncNoRet()) {
            ErrorHandler.addError(new Error(ErrorType.g, list.get(pos - 1).getLineNum()));
        }
        return funcDef;
    }

    public MainFuncDef parseMainFuncDef() {
        //MainFuncDef -> 'int' 'main' '(' ')' Block
        functionsTable.newFunction();
        MainFuncDef mainFuncDef = new MainFuncDef();

        functionsTable.writeFunction().setFuncType(curToken.getLexType());
        mainFuncDef.addChild(setLeafNode());    //'int'

        functionsTable.writeFunction().setFuncName(curToken.getContent());
        functionsTable.addFunction();
        mainFuncDef.addChild(setLeafNode());    //'main'

        mainFuncDef.addChild(setLeafNode());    //'('
        if (curToken.getLexType().compareTo(LexType.RPARENT) == 0) {
            mainFuncDef.addChild(setLeafNode());    //')'
        } else {
            ErrorHandler.addError(new Error(ErrorType.j, list.get(pos - 1).getLineNum()));
        }

        symbolsTable.addBlock();
        mainFuncDef.addChild(parseBlock());
        if (functionsTable.curIntFuncNoRet()) {
            ErrorHandler.addError(new Error(ErrorType.g, curToken.getLineNum()));
        }

        return mainFuncDef;
    }

    public FuncType parseFuncType() {
        //FuncType -> 'void' | 'int'
        FuncType funcType = new FuncType();
        functionsTable.writeFunction().setFuncType(curToken.getLexType());
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
        Symbol symbol = new Symbol();
        int paraDimension = 0;

        symbol.setVarType(LexType.INTTK);
        funcFParam.addChild(parseBType());

        symbol.setVarName(curToken.getContent());
        SymbolsTable.checkVarIsReDefined(symbol, curToken.getLineNum());
        funcFParam.addChild(setLeafNode()); //'Ident'

        if (curToken.getLexType().compareTo(LexType.LBRACK) == 0) {
            paraDimension++;
            symbol.addDimension();
            funcFParam.addChild(setLeafNode()); //'['
            if (LexTypeEqual(LexType.RBRACK)) {
                funcFParam.addChild(setLeafNode()); //']'
            } else {
                ErrorHandler.addError(new Error(ErrorType.k, list.get(pos - 1).getLineNum()));
            }
            while (curToken.getLexType().compareTo(LexType.LBRACK) == 0) {
                paraDimension++;
                symbol.addDimension();
                funcFParam.addChild(setLeafNode()); //'['
                funcFParam.addChild(parseConstExp());
                if (LexTypeEqual(LexType.RBRACK)) {
                    funcFParam.addChild(setLeafNode()); //']'
                } else {
                    ErrorHandler.addError(new Error(ErrorType.k, list.get(pos - 1).getLineNum()));
                }
            }
        }
        functionsTable.writeFunction().insertParam(paraDimension);
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
        symbolsTable.quitBlock();
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
            stmt.setType(StmtType.IfStmt);
            stmt.addChild(setLeafNode());   //IFTK
            stmt.addChild(setLeafNode());   //'('
            stmt.addChild(parseCond());
            if (LexTypeEqual(LexType.RPARENT)) {
                stmt.addChild(setLeafNode());   //')'
            } else {
                ErrorHandler.addError(new Error(ErrorType.j, list.get(pos - 1).getLineNum()));
            }
            stmt.addChild(parseStmt());
            if (curToken.getLexType().compareTo(LexType.ELSETK) == 0) {
                stmt.addChild(setLeafNode());   //'ELSETK'
                stmt.addChild(parseStmt());
            }
        } else if (curToken.getLexType().compareTo(LexType.FORTK) == 0) {
            //'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
            stmt.setType(StmtType.ForStmt);
            stmt.addChild(setLeafNode());   //'FORTK'
            stmt.addChild(setLeafNode());   //'('
            if (curToken.getLexType().compareTo(LexType.SEMICN) != 0) {
                stmt.addChild(parseForStmt());
            }
            stmt.addChild(setLeafNode());   //';'
            if (curToken.getLexType().compareTo(LexType.SEMICN) != 0) {
                stmt.addChild(parseCond());
            }
            stmt.addChild(setLeafNode());   //';'
            if (curToken.getLexType().compareTo(LexType.RPARENT) != 0) {
                stmt.addChild(parseForStmt());
            }
            stmt.addChild(setLeafNode());   //')'
            getInToLoop();
            stmt.addChild(parseStmt());
            getOutFromLoop();
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
        } else if (curToken.getLexType().compareTo(LexType.RETURNTK) == 0) {
            //'return' [Exp] ';'
            stmt.setType(StmtType.ReturnStmt);
            int returnLineNum = curToken.getLineNum();
            stmt.addChild(setLeafNode());   //'RETURNTK'
            functionsTable.curIntFuncRetMatched();
            if (curToken.getLexType().compareTo(LexType.SEMICN) != 0) {
                if (functionsTable.getCurDefFuncRetType().compareTo(LexType.VOIDTK) == 0) {
                    ErrorHandler.addError(new Error(ErrorType.f, returnLineNum));
                }
                stmt.addChild(parseExp());
            }
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
            int fmtChar = getFmtChar(curToken.getContent());
            stmt.addChild(setLeafNode());   //'STRCON'

            while (curToken.getLexType().compareTo(LexType.COMMA) == 0) {
                stmt.addChild(setLeafNode());   //','
                stmt.addChild(parseExp());
                fmtChar--;
            }

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
        } else if (curToken.getLexType().compareTo(LexType.LBRACE) == 0) {
            symbolsTable.addBlock();
            stmt.addChild(parseBlock());
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
                if (symbolsTable.varIsConst(curToken.getContent())) {
                    ErrorHandler.addError(new Error(ErrorType.h, curToken.getLineNum()));
                }
                stmt.addChild(parseLVal());
                stmt.addChild(setLeafNode());   //'='
                if (curToken.getLexType().compareTo(LexType.GETINTTK) == 0) {
                    stmt.setType(StmtType.LValGetIntStmt);
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
                    stmt.addChild(parseExp());
                    if (LexTypeEqual(LexType.SEMICN)) {
                        stmt.addChild(setLeafNode());   //';'
                    } else {
                        ErrorHandler.addError(new Error(ErrorType.i, list.get(pos - 1).getLineNum()));
                    }
                }
            } else {
                stmt.setType(StmtType.ExpStmt);
                if (curToken.getLexType().compareTo(LexType.SEMICN) != 0) {
                    stmt.addChild(parseExp());
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
        if (symbolsTable.varIsConst(curToken.getContent())) {
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

        if (SymbolsTable.checkVarIsUnDefined(curToken.getContent())) {
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
            if (!FunctionsTable.funcHasDefined(curToken.getContent())) {
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

            if (FunctionsTable.funcHasDefined(funcName)) {
                if (unaryExp.getFuncRParamsSize() != functionsTable.getFuncFParamsSize(funcName)) {
                    ErrorHandler.addError(new Error(ErrorType.d, identLineNum));
                } else if (unaryExp.getFuncRParamsSize() != 0) {
                    ArrayList<Integer> type1 = unaryExp.getFuncRParamsTypes();
                    ArrayList<Integer> type2 = functionsTable.getFuncFParams(funcName);

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
        if (curToken.getLexType().compareTo(LexType.LPARENT) == 0
                || curToken.getLexType().compareTo(LexType.IDENFR) == 0
                || curToken.getLexType().compareTo(LexType.PLUS) == 0
                || curToken.getLexType().compareTo(LexType.MINU) == 0
                || curToken.getLexType().compareTo(LexType.NOT) == 0
                || curToken.getLexType().compareTo(LexType.INTCON) == 0) {
            return true;
        }
        return false;
    }

    public void readVarDef(NonTerminalNode node, Symbol symbol) {

        SymbolsTable.checkVarIsReDefined(symbol, curToken.getLineNum());
        node.addChild(setLeafNode());   //Ident
        while (curToken.getLexType().compareTo(LexType.LBRACK) == 0) {
            symbol.addDimension();
            node.addChild(setLeafNode());   //'['
            node.addChild(parseConstExp());
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
