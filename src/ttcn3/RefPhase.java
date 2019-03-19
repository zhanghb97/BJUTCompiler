package ttcn3;

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
}
