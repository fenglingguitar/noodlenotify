package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.dao.GroupAuxiliaryDao;
import org.fl.noodlenotify.console.service.GroupAuxiliaryService;
import org.fl.noodlenotify.console.vo.GroupAuxiliaryVo;

@Service("groupAuxiliaryService")
public class GroupAuxiliaryServiceImpl implements GroupAuxiliaryService {

	@Autowired
	private GroupAuxiliaryDao groupAuxiliaryDao;

	@Override
	public List<GroupAuxiliaryVo> queryGroupAuxiliaryList(GroupAuxiliaryVo vo)
			throws Exception {
		return groupAuxiliaryDao.queryGroupAuxiliaryList(vo);
	}
	
	@Override
	public void insertGroupAuxiliary(GroupAuxiliaryVo vo) throws Exception {
		groupAuxiliaryDao.insertGroupAuxiliary(vo);
	}
}
