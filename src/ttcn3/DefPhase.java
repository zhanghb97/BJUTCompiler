package ttcn3;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class DefPhase extends t3parserBaseListener {
	ParseTreeProperty<Scope> scopes = new ParseTreeProperty<Scope>();
    GlobalScope globals;
    Scope currentScope; // define symbols in this scope
    
    void saveScope(ParserRuleContext ctx, Scope s) { scopes.put(ctx, s); }
    void defineVar(t3parserParser.TypeContext typeCtx, Token nameToken) {
        int typeTokenType = typeCtx.start.getType();
        Symbol.Type type = ttcn3.getType(typeTokenType);
        VariableSymbol var = new VariableSymbol(nameToken.getText(), type);
        currentScope.define(var); // Define symbol in current scope
    }
    
    public void enterTtcn3module(t3parserParser.Ttcn3moduleContext ctx) {
        globals = new GlobalScope(null);
        currentScope = globals;
    }
	
    public void exitTtcn3module(t3parserParser.Ttcn3moduleContext ctx) {
        System.out.println(globals);
    }
    
}
