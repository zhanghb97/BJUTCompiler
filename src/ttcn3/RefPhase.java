package ttcn3;

import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

public class RefPhase extends t3parserBaseListener {
	ParseTreeProperty<Scope> scopes;
    GlobalScope globals;
    Scope currentScope; // resolve symbols starting in this scope
    
    // 用于暂存runs on的component symbol
    ComponentSymbol componentSymbol;
    // 用于暂存record field symbol
    RecordSymbol recordFieldSymbol;
    // 用于判断是否进入equalExpression
    int equalExpressionFlag = 0;
    
    public RefPhase(GlobalScope globals, ParseTreeProperty<Scope> scopes) {
        this.scopes = scopes;
        this.globals = globals;
    }
    
    @Override
    public void enterTtcn3module(t3parserParser.Ttcn3moduleContext ctx) {
        currentScope = globals;
    }
    
    // Record 定义 
    @Override 
    public void enterRecordDef(t3parserParser.RecordDefContext ctx) { 
    	currentScope = scopes.get(ctx);
    }   
    @Override 
    public void exitRecordDef(t3parserParser.RecordDefContext ctx) {
    	currentScope = currentScope.getEnclosingScope(); //出栈
    }
    
    // Altstep 定义
    @Override 
    public void enterAltstepDef(t3parserParser.AltstepDefContext ctx) {
    	currentScope = scopes.get(ctx);
    }
	@Override 
	public void exitAltstepDef(t3parserParser.AltstepDefContext ctx) {
		currentScope = currentScope.getEnclosingScope(); //出栈
	}
    
	// Component 定义
	@Override 
    public void enterComponentDef(t3parserParser.ComponentDefContext ctx) { 
		currentScope = scopes.get(ctx);
    }  
    @Override 
    public void exitComponentDef(t3parserParser.ComponentDefContext ctx) { 
    	currentScope = currentScope.getEnclosingScope(); //出栈
    }
	
    // function 定义
    @Override 
    public void enterFunctionDef(t3parserParser.FunctionDefContext ctx) {
    	// 从树节点上取 scope
    	currentScope = scopes.get(ctx);
    }   
    @Override 
    public void exitFunctionDef(t3parserParser.FunctionDefContext ctx) {
    	currentScope = currentScope.getEnclosingScope();
    }
    
    // port 定义
    @Override 
    public void enterPortDef(t3parserParser.PortDefContext ctx) { 
    	currentScope = scopes.get(ctx);
    }
    
    @Override 
    public void exitPortDef(t3parserParser.PortDefContext ctx) { 
    	currentScope = currentScope.getEnclosingScope(); //出栈
    }
    
    // statement 定义
    @Override 
    public void enterStatementBlock(t3parserParser.StatementBlockContext ctx) { 
    	currentScope = scopes.get(ctx);
    }   
    @Override 
    public void exitStatementBlock(t3parserParser.StatementBlockContext ctx) { 
    	currentScope = currentScope.getEnclosingScope(); //出栈
    }
    
    // testcase 定义
    @Override 
    public void enterTestcaseDef(t3parserParser.TestcaseDefContext ctx) { 
    	currentScope = scopes.get(ctx);
    }
    @Override 
    public void exitTestcaseDef(t3parserParser.TestcaseDefContext ctx) { 
    	currentScope = currentScope.getEnclosingScope(); //出栈
    }
    
    // 验证function
    @Override 
    public void enterFunctionInstance(t3parserParser.FunctionInstanceContext ctx) { 
    	String funcName = ctx.IDENTIFIER().get(0).getText();
        System.out.println("Checking: " + funcName + "  in  " + currentScope);
    	Symbol meth = currentScope.resolve(funcName);
    	if ( meth==null ) {
    		System.out.println("no such variable: "+funcName);
            ttcn3.error((Token)ctx.IDENTIFIER(), "no such function: "+funcName);
        }
        if ( meth instanceof VariableSymbol ) {
        	ttcn3.error((Token)ctx.IDENTIFIER(), funcName+" is not a function");
        }
    }
    
    // 验证variable
    @Override
    public void enterVariableRef(t3parserParser.VariableRefContext ctx) {
    	String name = ctx.IDENTIFIER().getText();
    	Symbol var = currentScope.resolve(name);
    	System.out.println("Checking: "+ name + "  in  " + currentScope);
    	if ( var==null ) {
            System.out.println("no such variable: "+name);
    		ttcn3.error((Token)ctx.IDENTIFIER(), "no such variable: "+name);
        }
        if ( var instanceof FunctionSymbol ) {
        	ttcn3.error((Token)ctx.IDENTIFIER(), name+" is not a variable");
        }
    }
    
    // 验证equalExpression
    @Override 
    public void enterEqualExpression(t3parserParser.EqualExpressionContext ctx) {
    	if (ctx.getChildCount() == 3) {
    		equalExpressionFlag = 1;
    	}
    }
    
    @Override 
    public void exitEqualExpression(t3parserParser.EqualExpressionContext ctx) {
    	if (ctx.getChildCount() == 3) {
    		equalExpressionFlag = 0;
    	}
    }
    
    // 验证extendedIdentifier
    @Override 
    public void enterExtendedIdentifier(t3parserParser.ExtendedIdentifierContext ctx) { 
    	// 处理equalExpression中的extended identifier
    	if (equalExpressionFlag == 1) {
    		String name = ctx.IDENTIFIER().get(0).getText();
        	Symbol var = currentScope.resolve(name);
        	System.out.println("Checking: "+ name + "  in  " + currentScope);
        	if ( var==null ) {
                System.out.println("no such variable: "+name);
        		ttcn3.error((Token)ctx.IDENTIFIER(), "no such variable: "+name);
            }
            if ( var instanceof FunctionSymbol ) {
            	ttcn3.error((Token)ctx.IDENTIFIER(), name+" is not a variable");
            }
    	}
    	// 处理record中的extended identifier
    	if (ctx.getChildCount() != 1) {
    		List<TerminalNode> idList = ctx.IDENTIFIER();
    		for (int i = 0; i < idList.size(); i++) {
//    			System.out.println(i + ":   " + ctx.IDENTIFIER(i).getText());
    			String name = ctx.IDENTIFIER(i).getText();
    			Symbol sym = currentScope.resolve(name);
    			System.out.println(sym);
    			if (sym != null) {
    				if (sym.type == Symbol.Type.tUSER) {
//    					recordFieldSymbol = (RecordSymbol) sym.; 
        			}
    			}
    			else {
    				
    			}
    		}
    		
    	}
    }
    
    // 验证array identifier
    // p1.send(result); p1
    @Override 
    public void enterArrayIdentifierRef(t3parserParser.ArrayIdentifierRefContext ctx) { 
    	String name = ctx.IDENTIFIER().getText();
    	Symbol arraySym = currentScope.resolve(name);
    	System.out.println("Checking: "+ name + "  in  " + currentScope);
    	if ( arraySym==null ) {
    		if (componentSymbol.ports.get(name) == null) {
    			System.out.println("no such variable: "+name);
        		ttcn3.error((Token)ctx.IDENTIFIER(), "no such variable: "+name);
    		}
        }
    }
    
    // 验证runs on
    @Override public void enterRunsOnSpec(t3parserParser.RunsOnSpecContext ctx) { 
    	String name = ctx.componentType().getText();
    	ComponentSymbol runsonSym = (ComponentSymbol) currentScope.resolve(name);
    	
    	System.out.println("Checking: "+ name + "  in  " + currentScope);
    	if (runsonSym == null) {
    		System.out.println("no such runs on type: "+name);
    		ttcn3.error((Token)ctx, "no such runs on type: "+name);
    	}
    	componentSymbol = runsonSym;
    }
    
}
