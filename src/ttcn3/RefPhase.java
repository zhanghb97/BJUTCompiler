package ttcn3;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class RefPhase extends t3parserBaseListener {
	ParseTreeProperty<Scope> scopes;
    GlobalScope globals;
    Scope currentScope; // resolve symbols starting in this scope
    
    public RefPhase(GlobalScope globals, ParseTreeProperty<Scope> scopes) {
        this.scopes = scopes;
        this.globals = globals;
    }
    
    @Override
    public void enterTtcn3module(t3parserParser.Ttcn3moduleContext ctx) {
        currentScope = globals;
    }
    
    @Override 
    public void enterFunctionDef(t3parserParser.FunctionDefContext ctx) {
    	currentScope = scopes.get(ctx);
    }
    
    @Override 
    public void exitFunctionDef(t3parserParser.FunctionDefContext ctx) {
    	currentScope = currentScope.getEnclosingScope();
    }
    
    @Override 
    public void enterFunctionInstance(t3parserParser.FunctionInstanceContext ctx) { 
    	String funcName = ctx.IDENTIFIER().toString();
    	Symbol meth = currentScope.resolve(funcName);
    	if ( meth==null ) {
            ttcn3.error((Token)ctx.IDENTIFIER(), "no such function: "+funcName);
        }
        if ( meth instanceof VariableSymbol ) {
        	ttcn3.error((Token)ctx.IDENTIFIER(), funcName+" is not a function");
        }
    }
    
    @Override
    public void enterVariableRef(t3parserParser.VariableRefContext ctx) {
    	String name = ctx.IDENTIFIER().getText();
    	Symbol var = currentScope.resolve(name);
    	if ( var==null ) {
            System.out.println("no such variable: "+name);
    		ttcn3.error((Token)ctx.IDENTIFIER(), "no such variable: "+name);
        }
        if ( var instanceof FunctionSymbol ) {
        	ttcn3.error((Token)ctx.IDENTIFIER(), name+" is not a variable");
        }
    }
    
}
