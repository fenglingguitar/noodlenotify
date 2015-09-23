package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.MapVo;

@Controller
@RequestMapping(value = "console/remoting")
public class ConsoleRemotingController {
	
	@Autowired
	private ConsoleRemotingInvoke consoleRemotingInvoke;

	@RequestMapping(value = "/producerregister")
	@NoodleResponseBody
	public long producerRegister(String ip, int checkPort, String checkUrl, String checkType, String name) throws Exception {
		return consoleRemotingInvoke.producerRegister(ip, checkPort, checkUrl, checkType, name);
	}
	
	@RequestMapping(value = "/producergetexchangers")
	@NoodleResponseBody
	public MapVo<String, List<QueueExchangerVo>> producerGetExchangers(long producerId) throws Exception {
		return new MapVo<String, List<QueueExchangerVo>>(consoleRemotingInvoke.producerGetExchangers(producerId));
	}
}
