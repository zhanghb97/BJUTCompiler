package ttcn3;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import ttcn3.t3parserParser.SingleConstDefContext;
import ttcn3.t3parserParser.SingleVarInstanceContext;

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
    
    //进入module时，初始化全局作用域
    @Override
    public void enterTtcn3module(t3parserParser.Ttcn3moduleContext ctx) {
        globals = new GlobalScope(null);
        currentScope = globals;
    }
    
    //进入常量定义时，把常量的类型和名字绑定存入符号表
    @Override public void enterConstDef(t3parserParser.ConstDefContext ctx) { 
    	SingleConstDefContext s = ctx.constList().singleConstDef().get(0);
    	defineConst(ctx.type().predefinedType().getText(), s.IDENTIFIER().getSymbol());
    }
    
    //进入函数定义
    @Override 
    public void enterFunctionDef(t3parserParser.FunctionDefContext ctx) {
    	String name = ctx.IDENTIFIER().getText();
    	String typeTokenType = ctx.FUNCTION().getText();
    	Symbol.Type type = ttcn3.getType(typeTokenType);
    	//新建一个指向外围作用域的作用域，这样就完成了入栈操作
    	FunctionSymbol functionSymbol = new FunctionSymbol(name, type, currentScope);
    	currentScope.define(functionSymbol);
    	saveScope(ctx, functionSymbol);
    	currentScope = functionSymbol;
    }
    
    @Override 
    public void enterVarInstance(t3parserParser.VarInstanceContext ctx) { 
    	SingleVarInstanceContext s = ctx.varList().singleVarInstance().get(0);
    	defineVar(ctx.type().predefinedType().getText(), s.IDENTIFIER().getSymbol());
    	System.out.println(currentScope);
//    	System.out.println(currentScope.resolve(s.IDENTIFIER().toString()));
    }
    
    @Override 
    public void enterTimerInstance(t3parserParser.TimerInstanceContext ctx) { 
    	System.out.println(ctx.varList().singleVarInstance().get(0).IDENTIFIER().getSymbol());
    	defineVar(ctx.TIMER().getText(), ctx.varList().singleVarInstance().get(0).IDENTIFIER().getSymbol());
//    	System.out.println("currentScope:"+currentScope);
//    	System.out.println(currentScope.resolve(ctx.varList().singleVarInstance().get(0).IDENTIFIER().toString()));
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
