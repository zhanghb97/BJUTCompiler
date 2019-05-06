package ttcn3;

import java.util.LinkedHashMap;
import java.util.Map;

public class ComponentSymbol extends Symbol implements Scope {
	// component 中的 ports
	Map<String, Symbol> ports = new LinkedHashMap<String, Symbol>();
	// 上级作用域
	Scope enclosingScope;
	// 构造函数
	public ComponentSymbol(String name, Type type, Scope enclosingScope) {
		super(name, type);
		this.enclosingScope = enclosingScope;
	}
	// 在component作用域里查找name
	public Symbol resolve(String name) {
		Symbol s = ports.get(name);
        if ( s!=null ) return s;
        // if not here, check any enclosing scope
        if ( getEnclosingScope() != null ) {
            return getEnclosingScope().resolve(name);
        }
        return null; // not found
	}
	// 在component作用域里定义symbol
	public void define(Symbol sym) {
		ports.put(sym.name, sym);
        sym.scope = this; // track the scope in each symbol
        System.out.println("Defining: " + sym.name + "  Scope: " + sym.scope + "  Symbol: "+sym);
    }
	// 打印component信息
	public String toString() { 
		return "component"+super.toString()+":" ;
	}
	// 返回component名称
	public String getScopeName() {
		return name;
	}
	// 返回上级作用域
	public Scope getEnclosingScope() { 
		return enclosingScope; 
	}
}
