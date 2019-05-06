package ttcn3;

import java.util.LinkedHashMap;
import java.util.Map;

public class PortSymbol extends Symbol implements Scope{
	Map<String, Symbol> portFields = new LinkedHashMap<String, Symbol>();
	Scope enclosingScope;
	// port构造函数
	public PortSymbol(String name, Type type, Scope enclosingScope) {
        super(name, type);
        this.enclosingScope = enclosingScope;
    }
	// 在record作用域里查找name
	public Symbol resolve(String name) {
		Symbol s = portFields.get(name);
        if ( s!=null ) return s;
        // if not here, check any enclosing scope
        if ( getEnclosingScope() != null ) {
            return getEnclosingScope().resolve(name);
        }
        return null; // not found
	}
	// 在record作用域里定义symbol
	public void define(Symbol sym) {
		portFields.put(sym.name, sym);
        sym.scope = this; // track the scope in each symbol
        System.out.println("Defining: " + sym.name + "  Scope: " + sym.scope + "  Symbol: "+sym);
    }
	// 打印port信息
	public String toString() { 
		return "record"+super.toString()+":" ;
	}
	// 返回scope名称
	public String getScopeName() {
		return name;
	}
	// 返回上级作用域
	public Scope getEnclosingScope() { 
		return enclosingScope; 
	}
}
