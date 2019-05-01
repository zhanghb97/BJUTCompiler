package ttcn3;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import ttcn3.t3parserParser.FunctionFormalParContext;
import ttcn3.t3parserParser.SingleConstDefContext;
import ttcn3.t3parserParser.SingleVarInstanceContext;
import ttcn3.t3parserParser.StructFieldDefContext;

public class DefPhase extends t3parserBaseListener {
	ParseTreeProperty<Scope> scopes = new ParseTreeProperty<Scope>();
    GlobalScope globals;
    Scope currentScope; // define symbols in this scope
    
    void saveScope(ParserRuleContext ctx, Scope s) { scopes.put(ctx, s); }
    
    //定义变量
    void defineVar(String typeCtx, Token nameToken) {
        Symbol.Type type = ttcn3.getType(typeCtx);
        VariableSymbol var = new VariableSymbol(nameToken.getText(), type);
        currentScope.define(var); // Define symbol in current scope
    }
    
    //定义常量
    void defineConst(String typeCtx, Token nameToken) {
        Symbol.Type type = ttcn3.getType(typeCtx);
        ConstSymbol constSymbol = new ConstSymbol(nameToken.getText(), type);
        currentScope.define(constSymbol); // Define symbol in current scope
    }
    
    //定义函数参数列表
    void defineArguments(String typeCtx, Token nameToken) {
    	Symbol.Type type = ttcn3.getType(typeCtx);
    	ConstSymbol constSymbol = new ConstSymbol(nameToken.getText(), type);
        currentScope.define(constSymbol); // Define symbol in current scope
    }
    
    // 进入module时，初始化全局作用域
    @Override
    public void enterTtcn3module(t3parserParser.Ttcn3moduleContext ctx) {
        globals = new GlobalScope(null);
        currentScope = globals;
    }
    
    // 进入常量定义时，把常量的类型和名字绑定存入符号表
    @Override 
    public void enterConstDef(t3parserParser.ConstDefContext ctx) { 
    	SingleConstDefContext s = ctx.constList().singleConstDef().get(0);
    	// 判断是何种常量（预定义、用户定义）
    	if(ctx.type().children.contains(ctx.type().predefinedType())) {
    		defineConst(ctx.type().predefinedType().getText(), s.IDENTIFIER().getSymbol());
    	}
    	else if(ctx.type().children.contains(ctx.type().referencedType())) {
    		defineConst(ctx.type().referencedType().extendedIdentifier().IDENTIFIER().get(0).getText(), s.IDENTIFIER().getSymbol());
    	}
    	
    }
    
    // 进入函数定义
    @Override 
    public void enterFunctionDef(t3parserParser.FunctionDefContext ctx) {
    	String name = ctx.IDENTIFIER().getText();
    	String typeTokenType = ctx.returnType().type().predefinedType().getText();
    	Symbol.Type type = ttcn3.getType(typeTokenType);
    	//新建一个指向外围作用域的作用域，这样就完成了入栈操作
    	FunctionSymbol functionSymbol = new FunctionSymbol(name, type, currentScope);
    	currentScope.define(functionSymbol);
    	saveScope(ctx, functionSymbol);
    	currentScope = functionSymbol;
    }
    
    //函数参数定义
    @Override 
    public void enterFunctionFormalParList(t3parserParser.FunctionFormalParListContext ctx) { 
    	//取参数列表
    	List<FunctionFormalParContext> par = ctx.functionFormalPar();
    	//在函数作用域中分别定义参数
    	for(int i = 0; i < par.size(); i++) {
    		String name = par.get(i).formalValuePar().IDENTIFIER().getText();
    		String typeTokenType = par.get(i).formalValuePar().type().predefinedType().getText();
    		Symbol.Type type = ttcn3.getType(typeTokenType);
    		FuncArgsSymbol funcArgsSymbol = new FuncArgsSymbol(name, type);
    		currentScope.define(funcArgsSymbol);
    	}
    }
    
    // Record 定义 
    @Override 
    public void enterRecordDef(t3parserParser.RecordDefContext ctx) { 
    	String name = ctx.structDefBody().IDENTIFIER().getText();
    	RecordSymbol recordSymbol = new RecordSymbol(name, currentScope);
    	currentScope.define(recordSymbol);
    	saveScope(ctx, recordSymbol);
    	currentScope = recordSymbol;
    }
    
    @Override 
    public void enterStructDefBody(t3parserParser.StructDefBodyContext ctx) { 
    	// 取record field列表
    	List<StructFieldDefContext> field = ctx.structFieldDef();
    	for(int i = 0; i < field.size(); i++) {
    		String name = field.get(i).IDENTIFIER().getText();
    		String typeTokenType = field.get(i).type().predefinedType().getText();
    		Symbol.Type type = ttcn3.getType(typeTokenType);
    		RecordFieldSymbol recordFieldSymbol = new RecordFieldSymbol(name,type);
    		currentScope.define(recordFieldSymbol);
    	}
    }
    
    @Override 
    public void exitRecordDef(t3parserParser.RecordDefContext ctx) {
    	currentScope = currentScope.getEnclosingScope(); //出栈
    }
    
    // 进入变量定义
    @Override 
    public void enterVarInstance(t3parserParser.VarInstanceContext ctx) { 
    	SingleVarInstanceContext s = ctx.varList().singleVarInstance().get(0);
    	defineVar(ctx.type().predefinedType().getText(), s.IDENTIFIER().getSymbol());
    }
    
    @Override 
    public void enterTimerInstance(t3parserParser.TimerInstanceContext ctx) { 
    	System.out.println(ctx.varList().singleVarInstance().get(0).IDENTIFIER().getSymbol());
    	defineVar(ctx.TIMER().getText(), ctx.varList().singleVarInstance().get(0).IDENTIFIER().getSymbol());
	}
    
    @Override 
    public void enterVarList(t3parserParser.VarListContext ctx) { 

    }
    
    @Override
    public void exitFunctionDef(t3parserParser.FunctionDefContext ctx) {
    	currentScope = currentScope.getEnclosingScope(); //出栈
    }
	
    @Override
    public void exitTtcn3module(t3parserParser.Ttcn3moduleContext ctx) {
        System.out.println(globals);
    }
    
}
