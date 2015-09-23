package org.fl.noodlenotify.console.init;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.fl.noodlenotify.console.service.GroupAuxiliaryService;
import org.fl.noodlenotify.console.vo.GroupAuxiliaryVo;

public class GroupAuxiliaryConsoleInit implements ConsoleInit {
	
	@Autowired
	private GroupAuxiliaryService groupAuxiliaryService;

	@Override
	public void initialize() throws Exception {
		
		List<GroupAuxiliaryVo> list = groupAuxiliaryService.queryGroupAuxiliaryList(null);
		if (list.size() == 0) {			
			for (int i = 0; i < 63; i++) {
				GroupAuxiliaryVo groupAuxiliaryVo = new GroupAuxiliaryVo();
				groupAuxiliaryVo.setGroup_Num(1l << i);
				groupAuxiliaryService.insertGroupAuxiliary(groupAuxiliaryVo);
			}
		}
	}
}
