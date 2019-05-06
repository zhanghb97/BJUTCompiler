package ttcn3;

import java.util.LinkedHashMap;
import java.util.Map;


public class RecordSymbol extends Symbol implements Scope {
	// record 中的 fields
	Map<String, Symbol> fields = new LinkedHashMap<String, Symbol>();
	// 上级作用域
	Scope enclosingScope;
	// RecordSymbol 构造函数
	public RecordSymbol(String name,Type retType, Scope enclosingScope) {
        super(name, retType);
        this.enclosingScope = enclosingScope;
    }
	// 在record作用域里查找name
	public Symbol resolve(String name) {
		Symbol s = fields.get(name);
        if ( s!=null ) return s;
        // if not here, check any enclosing scope
        if ( getEnclosingScope() != null ) {
            return getEnclosingScope().resolve(name);
        }
        return null; // not found
	}
	// 在record作用域里定义symbol
	public void define(Symbol sym) {
		fields.put(sym.name, sym);
        sym.scope = this; // track the scope in each symbol
        System.out.println("Defining: " + sym.name + "  Scope: " + sym.scope + "  Symbol: "+sym);
    }
	// 打印record信息
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
