package ttcn3;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import ttcn3.t3parserParser.SingleVarInstanceContext;

public class DefPhase extends t3parserBaseListener {
	ParseTreeProperty<Scope> scopes = new ParseTreeProperty<Scope>();
    GlobalScope globals;
    Scope currentScope; // define symbols in this scope
    
    void saveScope(ParserRuleContext ctx, Scope s) { scopes.put(ctx, s); }
    void defineVar(String typeCtx, Token nameToken) {
        Symbol.Type type = ttcn3.getType(typeCtx);
        VariableSymbol var = new VariableSymbol(nameToken.getText(), type);
        currentScope.define(var); // Define symbol in current scope
    }
    
    @Override
    public void enterTtcn3module(t3parserParser.Ttcn3moduleContext ctx) {
        globals = new GlobalScope(null);
        currentScope = globals;
    }
    
    @Override 
    public void enterFunctionDef(t3parserParser.FunctionDefContext ctx) {
    	String name = ctx.IDENTIFIER().getText();
//    	int typeTokenType = ctx.returnType().start.getType();
//    	Symbol.Type type = ttcn3.getType(typeTokenType);
    	
    	//新建一个指向外围作用域的作用域，这样就完成了入栈操作
    	FunctionSymbol function = new FunctionSymbol(name, currentScope);
//    	FunctionSymbol function = new FunctionSymbol(name, type, currentScope);
    	currentScope.define(function);
    	saveScope(ctx, function);
    	currentScope = function;
    	
    }
    
    @Override 
    public void enterVarInstance(t3parserParser.VarInstanceContext ctx) { 
    	SingleVarInstanceContext s = ctx.varList().singleVarInstance().get(0);
    	defineVar(ctx.type().predefinedType().getText(), s.IDENTIFIER().getSymbol());
    }
    
    @Override 
    public void enterTimerInstance(t3parserParser.TimerInstanceContext ctx) { 
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
