package ttcn3;

import ttcn3.t3parserParser.SingleVarInstanceContext;

public class componentTranslation extends t3parserBaseListener {
	@Override 
	public void enterComponentDef(t3parserParser.ComponentDefContext ctx) { 
		System.out.print(ctx.COMPONENT().getText()+" struct");
		System.out.print(ctx.LEFT_BRACE().getText()+"\n");
	}
	
	@Override 
	public void exitComponentDef(t3parserParser.ComponentDefContext ctx) { 
		System.out.print(ctx.RIGHT_BRACE().getText());
	}
	
	@Override 
	public void enterTypeDef(t3parserParser.TypeDefContext ctx) { 
		System.out.print(ctx.TYPE().getText()+" ");
	}
	
	@Override 
	public void enterTimerInstance(t3parserParser.TimerInstanceContext ctx) { 
		SingleVarInstanceContext s = ctx.varList().singleVarInstance().get(0);
		System.out.print(s.IDENTIFIER().getText() + " " + ctx.TIMER().getText() + "\n");
	}
}
