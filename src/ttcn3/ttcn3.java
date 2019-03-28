package ttcn3;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

//import antlrTest.t3parserLexer;
//import antlrTest.HelloParser;
//import antlrTest.HelloParser.RContext;
import ttcn3.t3parserParser.Ttcn3moduleContext;

public class ttcn3 {
	public static Symbol.Type getType(String tokenType) {
        switch ( tokenType ) {
        	//根据识别出来的类型，返回符号表中的类型
            case "timer" :  
            	System.out.println("timer!!!!!!!!!");
            	return Symbol.Type.tTimer; 
            
        }
        return Symbol.Type.tINVALID;
    }
	
	public static void error(Token t, String msg) {
        System.err.printf("line %d:%d %s\n", t.getLine(), t.getCharPositionInLine(), msg);
    }

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//新建一个CharStream，从标准输入读入数据
		CharStream input = CharStreams.fromStream(System.in);
		
		//新建一个词法分析器,处理CharStream
		t3parserLexer lexer = new t3parserLexer(input);
		
		//新建一个词法符号的缓冲区，用于存储词法分析器将生成的词法符号
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		//新建一个语法分析器，处理词法符号缓冲区的内容
		t3parserParser parser = new t3parserParser(tokens);
		
//		ParseTreeWalker walker = new ParseTreeWalker();
		//针对ttcn3module规则开始语法分析
		Ttcn3moduleContext tree = parser.ttcn3module();
//		System.out.println(input);
//		System.out.println(tree.toStringTree(parser));
//		System.out.println(tree.getText());
		
		ParseTreeWalker walker = new ParseTreeWalker();
		DefPhase def = new DefPhase();
		walker.walk(def, tree);
		walker.walk(new RefPhase(def.globals, def.scopes), tree);
//		walker.walk(new t3parserBaseListener(), tree);
		System.out.println();
		
	}

}
