package org.fl.noodlenotify.console.service;

import java.util.List;
import org.fl.noodlenotify.console.vo.GroupAuxiliaryVo;

public interface GroupAuxiliaryService {
	public List<GroupAuxiliaryVo> queryGroupAuxiliaryList(GroupAuxiliaryVo vo) throws Exception;
	public void insertGroupAuxiliary(GroupAuxiliaryVo vo) throws Exception;
}
