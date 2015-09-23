package org.fl.noodlenotify.console.dao;

import java.util.List;
import org.fl.noodlenotify.console.vo.GroupAuxiliaryVo;

public interface GroupAuxiliaryDao {
	public List<GroupAuxiliaryVo> queryGroupAuxiliaryList(GroupAuxiliaryVo vo) throws Exception;
	public void insertGroupAuxiliary(GroupAuxiliaryVo vo) throws Exception;
}
