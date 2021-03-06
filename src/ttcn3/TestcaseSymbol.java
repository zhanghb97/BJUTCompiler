package ttcn3;

import java.util.LinkedHashMap;
import java.util.Map;

public class TestcaseSymbol extends Symbol implements Scope {
	Map<String, Symbol> runson = new LinkedHashMap<String, Symbol>();
	Scope enclosingScope;
	
	public TestcaseSymbol(String name, Type retType, Scope enclosingScope) {
        super(name, retType);
        this.enclosingScope = enclosingScope;
    }
	
	public Symbol resolve(String name) {
        Symbol s = runson.get(name);
        if ( s!=null ) return s;
        // if not here, check any enclosing scope
        if ( getEnclosingScope() != null ) {
            return getEnclosingScope().resolve(name);
        }
        return null; // not found
    }

    public void define(Symbol sym) {
    	runson.put(sym.name, sym);
        sym.scope = this; // track the scope in each symbol
        System.out.println("Defining: " + sym.name + "  Scope: " + sym.scope + "  Symbol: "+sym);
    }

    public Scope getEnclosingScope() { return enclosingScope; }
    public String getScopeName() { return name; }

    public String toString() { return "testcase"+super.toString()+":"+runson.values(); }
}
