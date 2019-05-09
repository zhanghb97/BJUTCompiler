package ttcn3;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import ttcn3.t3parserParser.FunctionFormalParContext;
import ttcn3.t3parserParser.MessageAttribsContext;
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
    
    // 定义port instance
    void definePortInst(String typeCtx, Token nameToken) {
    	Symbol.Type type = ttcn3.getType(typeCtx);
        PortInstSymbol portInstSymbol = new PortInstSymbol(nameToken.getText(), type);
        currentScope.define(portInstSymbol); // Define symbol in current scope
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
    	// 函数名
    	String name = ctx.IDENTIFIER().getText();
    	// 函数返回值
    	String typeTokenType;
    	if(ctx.children.contains(ctx.returnType())) {
    		typeTokenType = ctx.returnType().type().predefinedType().getText();
    	} 
    	else {
    		typeTokenType = "NULL";
    	}
    	
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
    		// value参数
    		if (par.get(i).children.contains(par.get(i).formalValuePar())) {
    			String name = par.get(i).formalValuePar().IDENTIFIER().getText();
        		String typeTokenType = par.get(i).formalValuePar().type().predefinedType().getText();
        		Symbol.Type type = ttcn3.getType(typeTokenType);
        		FuncArgsSymbol funcArgsSymbol = new FuncArgsSymbol(name, type);
        		currentScope.define(funcArgsSymbol);
    		}
    		// timer参数
    		else if (par.get(i).children.contains(par.get(i).formalTimerPar())) {
    			String name = par.get(i).formalTimerPar().IDENTIFIER().getText();
    			String typeTokenType = "timer";
    			Symbol.Type type = ttcn3.getType(typeTokenType);
    			AltstepArgsSymbol altstepArgsSymbol = new AltstepArgsSymbol(name, type);
    			currentScope.define(altstepArgsSymbol);
    		}
    		
    	}
    }
    
    // Record 定义 
    @Override 
    public void enterRecordDef(t3parserParser.RecordDefContext ctx) { 
    	String name = ctx.structDefBody().IDENTIFIER().getText();
    	Symbol.Type type = Symbol.Type.tRECORD;
    	RecordSymbol recordSymbol = new RecordSymbol(name, type, currentScope);
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
    
    // 进入port定义
    @Override 
    public void enterPortDef(t3parserParser.PortDefContext ctx) { 
    	String name = ctx.IDENTIFIER().getText();
    	String typeTokenType = "port";
    	Symbol.Type type = ttcn3.getType(typeTokenType);
    	//新建一个指向外围作用域的作用域，这样就完成了入栈操作
    	PortSymbol portSymbol = new PortSymbol(name, type, currentScope);
    	currentScope.define(portSymbol);
    	saveScope(ctx, portSymbol);
    	currentScope = portSymbol;
    }
    
    @Override 
    public void exitPortDef(t3parserParser.PortDefContext ctx) { 
    	currentScope = currentScope.getEnclosingScope(); //出栈
    }
    
    // 进入port引用
    @Override
    public void enterPortInstance(t3parserParser.PortInstanceContext ctx) { 
    	String portType = ctx.IDENTIFIER().get(0).getText();
    	Symbol portSymbol = currentScope.resolve(portType);
    	if ( portSymbol == null ) {
    		System.out.println("no such port type: "+portType);
        }
    	definePortInst("port", ctx.portElement().get(0).IDENTIFIER().getSymbol());
    }
    
    // 进入component定义
    @Override 
    public void enterComponentDef(t3parserParser.ComponentDefContext ctx) { 
    	String name = ctx.IDENTIFIER().getText();
    	Symbol.Type type = Symbol.Type.tCOMPONENT;
    	ComponentSymbol componentSymbol = new ComponentSymbol(name, type, currentScope);
    	currentScope.define(componentSymbol);
    	saveScope(ctx, componentSymbol);
    	currentScope = componentSymbol;
    }
    
    @Override 
    public void exitComponentDef(t3parserParser.ComponentDefContext ctx) { 
    	currentScope = currentScope.getEnclosingScope(); //出栈
    }
    
    // 进入testcase定义
    @Override 
    public void enterTestcaseDef(t3parserParser.TestcaseDefContext ctx) { 
    	// 函数名
    	String name = ctx.IDENTIFIER().getText();
    	// 函数返回值
    	String typeTokenType = "testcase";
    	
    	Symbol.Type type = ttcn3.getType(typeTokenType);
    	//新建一个指向外围作用域的作用域，这样就完成了入栈操作
    	TestcaseSymbol testcaseSymbol = new TestcaseSymbol(name, type, currentScope);
    	currentScope.define(testcaseSymbol);
    	saveScope(ctx, testcaseSymbol);
    	
    }
    
    @Override 
    public void exitTestcaseDef(t3parserParser.TestcaseDefContext ctx) { 
    	currentScope = currentScope.getEnclosingScope(); //出栈
    }
    
    // statement 定义
    @Override 
    public void enterStatementBlock(t3parserParser.StatementBlockContext ctx) { 
    	LocalScope local = new LocalScope(currentScope);
    	saveScope(ctx, local);
    	currentScope = local;
    }
    
    @Override 
    public void exitStatementBlock(t3parserParser.StatementBlockContext ctx) { 
    	currentScope = currentScope.getEnclosingScope(); //出栈
    }
    
    // altstep 定义
    @Override public void enterAltstepDef(t3parserParser.AltstepDefContext ctx) {
    	// 函数名
    	String name = ctx.IDENTIFIER().getText();
    	// 函数返回值
    	String typeTokenType = "altstep";
    	Symbol.Type type = ttcn3.getType(typeTokenType);
    	//新建一个指向外围作用域的作用域，这样就完成了入栈操作
    	AltstepSymbol altstepSymbol = new AltstepSymbol(name, type, currentScope);
    	currentScope.define(altstepSymbol);
    	saveScope(ctx, altstepSymbol);
    	currentScope = altstepSymbol;
    }
	
	@Override public void exitAltstepDef(t3parserParser.AltstepDefContext ctx) {
		currentScope = currentScope.getEnclosingScope(); //出栈
	}
    
    // control scope 定义
    @Override 
    public void enterModuleControlPart(t3parserParser.ModuleControlPartContext ctx) { 
    	ControlScope local = new ControlScope(currentScope);
    	saveScope(ctx, local);
    	currentScope = local;
    }
    
    @Override 
    public void exitModuleControlPart(t3parserParser.ModuleControlPartContext ctx) {
    	currentScope = currentScope.getEnclosingScope(); //出栈
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
