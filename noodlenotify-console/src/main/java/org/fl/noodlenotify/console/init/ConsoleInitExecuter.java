package org.fl.noodlenotify.console.init;

import java.util.List;

public class ConsoleInitExecuter {
	
	private List<ConsoleInit> consoleInits;

	public void start() throws Exception {
		for (ConsoleInit consoleInit : consoleInits) {
			consoleInit.initialize();
		}
	}
	
	public void setConsoleInits(List<ConsoleInit> consoleInits) {
		this.consoleInits = consoleInits;
	}
}
