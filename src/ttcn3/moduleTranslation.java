package ttcn3;

public class moduleTranslation extends t3parserBaseListener {
	@Override
	public void enterTtcn3module(t3parserParser.Ttcn3moduleContext ctx) {
		System.out.print("package ");
	}
	
	@Override 
	public void enterModuleId(t3parserParser.ModuleIdContext ctx) { 
		System.out.println(ctx.getText());
	}
}
