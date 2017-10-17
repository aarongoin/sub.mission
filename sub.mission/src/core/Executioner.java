package core;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;


public class Executioner {
	
	ScriptEngine engine;
	SimpleBindings global;
	
	ScriptEngineManager manager;
	
	String name;
	
	public Executioner(String n) {
		manager = new ScriptEngineManager();
		engine = manager.getEngineByName("nashorn");
		
		name = n;
	}
	
	public void addToScope(String key, Object value) {
		global.put(key, value);
		engine.setBindings(global, ScriptContext.ENGINE_SCOPE);
	}
	
	public void execute(String script) {
		execute(script, true);
	}
	
	public void execute(String script, boolean scoped) {
		try {
			if (scoped)
				engine.eval("(function(){" + script + "})();");
			else
				engine.eval(script);
		} catch (ScriptException e) {
			System.err.println(e.getMessage().replace("<eval>", name));
		}
	}
	
	public String getName() {
		return name;
	}
	public void removeFromScope(String key) {
		global.remove(key);
		engine.setBindings(global, ScriptContext.ENGINE_SCOPE);
	}
	
	public void setName(String n) {
		name = n;
	}

}
