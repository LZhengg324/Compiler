package Grammar;

import Grammar.GrammarNode.NonTerminate.*;
import Lexical.LexType;
import Lexical.tokenNode;

import java.util.ArrayList;

public class Parser {
    private final ArrayList<tokenNode> list;
    private tokenNode curToken;
    private int pos;
    private CompUnit root;

    public Parser(ArrayList<tokenNode> list) {
        this.list = list;
        this.pos = 0;
        this.curToken = list.get(pos);
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
        while (list.get(pos + 1).getType().compareTo(LexType.MAINTK) != 0
                && list.get(pos + 2).getType().compareTo(LexType.LPARENT) != 0) {
            //decls.add(parseDecl()); //✔
            compUnit.addChild(parseDecl());
        }

        //是函数
        while (list.get(pos + 1).getType().compareTo(LexType.MAINTK) != 0
                && list.get(pos + 2).getType().compareTo(LexType.LPARENT) == 0) {
            //funcDefs.add(parseFuncDef());
            compUnit.addChild(parseFuncDef());
        }

        if (curToken.getType().compareTo(LexType.INTTK) == 0
                && list.get(pos + 1).getType().compareTo(LexType.MAINTK) == 0) {
            compUnit.addChild(parseMainFuncDef());
            return compUnit;
        } else {
            error();
        }
        return null;
    }

    public Decl parseDecl() {
        //Decl -> ConstDecl | VarDecl
        Decl decl = new Decl();
        if (curToken.getType().compareTo(LexType.CONSTTK) == 0) {
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
        while (curToken.getType().compareTo(LexType.COMMA) == 0) {
            constDecl.addChild(setLeafNode());  //','
            constDecl.addChild(parseConstDef());
        }
        constDecl.addChild(setLeafNode());  //';'

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

        constDef.addChild(setLeafNode());   //Ident
        while (curToken.getType().compareTo(LexType.LBRACK) == 0) {
            constDef.addChild(setLeafNode());   //'['
            constDef.addChild(parseConstExp());
            constDef.addChild(setLeafNode());   //']'
        }
        constDef.addChild(setLeafNode());   //'='
        constDef.addChild(parseConstInitVal());

        return constDef;
    }

    public ConstInitVal parseConstInitVal() {
        //ConstInitVal -> ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        ConstInitVal constInitVal = new ConstInitVal();

        if (curToken.getType().compareTo(LexType.LBRACE) == 0) {
            constInitVal.addChild(setLeafNode());   //'{'
            if (curToken.getType().compareTo(LexType.RBRACE) != 0) {
                constInitVal.addChild(parseConstInitVal());
                while (curToken.getType().compareTo(LexType.COMMA) == 0) {
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
        while (curToken.getType().compareTo(LexType.COMMA) == 0) {
            varDecl.addChild(setLeafNode());    //','
            varDecl.addChild(parseVarDef());
        }
        varDecl.addChild(setLeafNode());    //';'
        return varDecl;
    }

    public VarDef parseVarDef() {
        //VarDef -> Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
        VarDef varDef = new VarDef();

        varDef.addChild(setLeafNode()); //Ident
        if (curToken.getType().compareTo(LexType.LBRACK) == 0) {
            while (curToken.getType().compareTo(LexType.LBRACK) == 0) {
                varDef.addChild(setLeafNode()); //'['
                varDef.addChild(parseConstExp());
                varDef.addChild(setLeafNode()); //']'
            }
        }
        if (curToken.getType().compareTo(LexType.ASSIGN) == 0) {
            varDef.addChild(setLeafNode()); //'='
            varDef.addChild(parseInitVal());
        }
        return varDef;
    }

    public InitVal parseInitVal() {
        //InitVal -> Exp | '{' [ InitVal { ',' InitVal } ] '}'
        InitVal initVal = new InitVal();

        if (curToken.getType().compareTo(LexType.LBRACE) == 0) {
            initVal.addChild(setLeafNode());    //'{'
            if (curToken.getType().compareTo(LexType.RBRACE) != 0) {
                initVal.addChild(parseInitVal());
                while (curToken.getType().compareTo(LexType.COMMA) == 0) {
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
        funcDef.addChild(setLeafNode());    //'Ident'
        funcDef.addChild(setLeafNode());    //'('
        if (curToken.getType().compareTo(LexType.RPARENT) != 0) {
            funcDef.addChild(parseFuncFParams());
        }
        funcDef.addChild(setLeafNode());    //')'
        funcDef.addChild(parseBlock());
        return funcDef;
    }

    public MainFuncDef parseMainFuncDef() {
        //MainFuncDef -> 'int' 'main' '(' ')' Block
        MainFuncDef mainFuncDef = new MainFuncDef();
        mainFuncDef.addChild(setLeafNode());    //'int'
        mainFuncDef.addChild(setLeafNode());    //'main'
        mainFuncDef.addChild(setLeafNode());    //'('
        mainFuncDef.addChild(setLeafNode());    //')'
        mainFuncDef.addChild(parseBlock());
        return mainFuncDef;
    }

    public FuncType parseFuncType() {
        //FuncType -> 'void' | 'int'
        FuncType funcType = new FuncType();
        funcType.addChild(setLeafNode());   // 'void' | 'int'
        return funcType;
    }

    public FuncFParams parseFuncFParams() {
        //FuncFParams -> FuncFParam { ',' FuncFParam }
        FuncFParams funcFParams = new FuncFParams();

        funcFParams.addChild(parseFuncFParam());
        while (curToken.getType().compareTo(LexType.COMMA) == 0) {
            funcFParams.addChild(setLeafNode());    //','
            funcFParams.addChild(parseFuncFParam());
        }
        return funcFParams;
    }

    public FuncFParam parseFuncFParam() {
        //FuncFParam -> BType Ident ['[' ']' { '[' ConstExp ']' }]
        FuncFParam funcFParam = new FuncFParam();
        funcFParam.addChild(parseBType());
        funcFParam.addChild(setLeafNode()); //'Ident'

        if (curToken.getType().compareTo(LexType.LBRACK) == 0) {
            funcFParam.addChild(setLeafNode()); //'['
            funcFParam.addChild(setLeafNode()); //']'

            while (curToken.getType().compareTo(LexType.LBRACK) == 0) {
                funcFParam.addChild(setLeafNode()); //'['
                funcFParam.addChild(parseConstExp());
                funcFParam.addChild(setLeafNode()); //']'
            }
        }
        return funcFParam;
    }

    public Block parseBlock() {
        //Block -> '{' { BlockItem } '}'
        Block block = new Block();
        block.addChild(setLeafNode());  //'{'
        while (curToken.getType().compareTo(LexType.RBRACE) != 0) {
            block.addChild(parseBlockItem());
        }
        block.addChild(setLeafNode());  //'}'
        return block;
    }

    public BlockItem parseBlockItem() {
        //BlockItem -> Decl | Stmt
        BlockItem blockItem = new BlockItem();
        if (curToken.getType().compareTo(LexType.CONSTTK) == 0
                || curToken.getType().compareTo(LexType.INTTK) == 0) {
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

        if (curToken.getType().compareTo(LexType.IFTK) == 0) {
            //'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            stmt.setType(StmtType.IfStmt);
            stmt.addChild(setLeafNode());   //IFTK
            stmt.addChild(setLeafNode());   //'('
            stmt.addChild(parseCond());
            stmt.addChild(setLeafNode());   //')'
            stmt.addChild(parseStmt());
            if (curToken.getType().compareTo(LexType.ELSETK) == 0) {
                stmt.addChild(setLeafNode());   //'ELSETK'
                stmt.addChild(parseStmt());
            }
        } else if (curToken.getType().compareTo(LexType.FORTK) == 0) {
            //'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
            stmt.setType(StmtType.ForStmt);
            stmt.addChild(setLeafNode());   //'FORTK'
            stmt.addChild(setLeafNode());   //'('
            if (curToken.getType().compareTo(LexType.SEMICN) != 0) {
                stmt.addChild(parseForStmt());
            }
            stmt.addChild(setLeafNode());   //';'
            if (curToken.getType().compareTo(LexType.SEMICN) != 0) {
                stmt.addChild(parseCond());
            }
            stmt.addChild(setLeafNode());   //';'
            if (curToken.getType().compareTo(LexType.RPARENT) != 0) {
                stmt.addChild(parseForStmt());
            }
            stmt.addChild(setLeafNode());   //')'
            stmt.addChild(parseStmt());
        } else if (curToken.getType().compareTo(LexType.BREAKTK) == 0) {
            //'break' ';'
            stmt.setType(StmtType.BreakStmt);
            stmt.addChild(setLeafNode());   //'BREAKTK'
            stmt.addChild(setLeafNode());   //';'
        } else if (curToken.getType().compareTo(LexType.CONTINUETK) == 0) {
            //'continue' ';'
            stmt.setType(StmtType.ContinueStmt);
            stmt.addChild(setLeafNode());   //'CONTINUETK'
            stmt.addChild(setLeafNode());   //';'
        } else if (curToken.getType().compareTo(LexType.RETURNTK) == 0) {
            //'return' [Exp] ';'
            stmt.setType(StmtType.ReturnStmt);
            stmt.addChild(setLeafNode());   //'RETURNTK'
            if (curToken.getType().compareTo(LexType.SEMICN) != 0) {
                stmt.addChild(parseExp());
            }
            stmt.addChild(setLeafNode());   //';'
        } else if (curToken.getType().compareTo(LexType.PRINTFTK) == 0) {
            //'printf' '('FormatString { ',' Exp } ')' ';'
            stmt.setType(StmtType.PrintfStmt);
            stmt.addChild(setLeafNode());   //'PRINTFTK'
            stmt.addChild(setLeafNode());   //'('
            stmt.addChild(setLeafNode());   //'STRCON'
            while (curToken.getType().compareTo(LexType.COMMA) == 0) {
                stmt.addChild(setLeafNode());   //','
                stmt.addChild(parseExp());
            }
            stmt.addChild(setLeafNode());   //')'
            stmt.addChild(setLeafNode());   //';'
        } else if (curToken.getType().compareTo(LexType.LBRACE) == 0) {
            stmt.addChild(parseBlock());
        } else {
            //LVal '=' Exp ';'
            //LVal '=' 'getint' '(' ')' ';'
            //[Exp] ';'
            int temp = curToken.getLineNum();
            for (int i = pos + 1; i < list.size() && curToken.getLineNum() == list.get(i).getLineNum(); i++) {
                if (list.get(i).getType().compareTo(LexType.ASSIGN) == 0) {
                    temp = i;
                    break;
                }
            }
            if (temp > pos) {
                stmt.addChild(parseLVal());
                stmt.addChild(setLeafNode());   //'='
                if (curToken.getType().compareTo(LexType.GETINTTK) == 0) {
                    stmt.setType(StmtType.LValGetIntStmt);
                    stmt.addChild(setLeafNode());   //'GETINTTK
                    stmt.addChild(setLeafNode());   //'('
                    stmt.addChild(setLeafNode());   //')'
                    stmt.addChild(setLeafNode());   //';'
                } else {
                    stmt.setType(StmtType.LValStmt);
                    stmt.addChild(parseExp());
                    stmt.addChild(setLeafNode());   //';'
                }
            } else {
                stmt.setType(StmtType.ExpStmt);
                if (curToken.getType().compareTo(LexType.SEMICN) != 0) {
                    stmt.addChild(parseExp());
                }
                stmt.addChild(setLeafNode());   //';'
            }
        }
        return stmt;
    }

    public ForStmt parseForStmt() {
        //ForStmt -> LVal '=' Exp
        ForStmt forStmt = new ForStmt();
        forStmt.addChild(parseLVal());
        forStmt.addChild(setLeafNode());
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

        lVal.addChild(setLeafNode());   //'Ident'

        while (curToken.getType().compareTo(LexType.LBRACK) == 0) {
            lVal.addChild(setLeafNode());   //'['
            lVal.addChild(parseExp());
            lVal.addChild(setLeafNode());   //']'
        }
        return lVal;
    }

    public PrimaryExp parsePrimaryExp() {
        //PrimaryExp -> '(' Exp ')' | LVal | Number
        PrimaryExp primaryExp = new PrimaryExp();

        if (curToken.getType().compareTo(LexType.LPARENT) == 0) {
            primaryExp.addChild(setLeafNode()); //'('
            primaryExp.addChild(parseExp());
            primaryExp.addChild(setLeafNode()); //')'
        } else if (curToken.getType().compareTo(LexType.INTCON) == 0) {
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

        if (curToken.getType().compareTo(LexType.IDENFR) == 0
                && list.get(pos + 1).getType().compareTo(LexType.LPARENT) == 0) {
            unaryExp.addChild(setLeafNode());   //'Ident'
            unaryExp.addChild(setLeafNode());   //'('
            if (curToken.getType().compareTo(LexType.RPARENT) != 0) {
                unaryExp.addChild(parseFuncRParams());
            }
            unaryExp.addChild(setLeafNode());
        } else if (curToken.getType().compareTo(LexType.PLUS) == 0
                || curToken.getType().compareTo(LexType.MINU) == 0
                || curToken.getType().compareTo(LexType.NOT) == 0) {
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
        while (curToken.getType().compareTo(LexType.COMMA) == 0) {
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
        while (curToken.getType().compareTo(LexType.MULT) == 0
                || curToken.getType().compareTo(LexType.DIV) == 0
                || curToken.getType().compareTo(LexType.MOD) == 0) {
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
        while (curToken.getType().compareTo(LexType.PLUS) == 0
                || curToken.getType().compareTo(LexType.MINU) == 0) {
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

        if (curToken.getType().compareTo(LexType.GRE) == 0
                || curToken.getType().compareTo(LexType.GEQ) == 0
                || curToken.getType().compareTo(LexType.LSS) == 0
                || curToken.getType().compareTo(LexType.LEQ) == 0) {
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

        if (curToken.getType().compareTo(LexType.EQL) == 0
                || curToken.getType().compareTo(LexType.NEQ) == 0) {
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
        while (curToken.getType().compareTo(LexType.AND) == 0) {
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

        while (curToken.getType().compareTo(LexType.OR) == 0) {
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

    public void error() {

    }

    public CompUnit getRoot() {
        return root;
    }
}
