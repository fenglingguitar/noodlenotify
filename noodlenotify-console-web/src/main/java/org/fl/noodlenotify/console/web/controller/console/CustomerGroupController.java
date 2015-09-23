package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.CustomerGroupService;
import org.fl.noodlenotify.console.vo.CustomerGroupVo;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/customergroup")
public class CustomerGroupController {
	
	@Autowired
	private CustomerGroupService customerGroupService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<CustomerGroupVo> queryPage(@NoodleRequestParam CustomerGroupVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return customerGroupService.queryCustomerGroupPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryincludepage")
	@NoodleResponseBody
	public PageVo<CustomerVo> queryIncludePage(@NoodleRequestParam CustomerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return customerGroupService.queryCustomerIncludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryexcludepage")
	@NoodleResponseBody
	public PageVo<CustomerVo> queryExcludePage(@NoodleRequestParam CustomerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return customerGroupService.queryCustomerExcludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<CustomerGroupVo> queryList(@NoodleRequestParam CustomerGroupVo vo) throws Exception {
		return customerGroupService.queryCustomerGroupList(vo);
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam CustomerGroupVo vo) throws Exception {
		customerGroupService.insertCustomerGroup(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam CustomerGroupVo[] vos) throws Exception {
		customerGroupService.insertsCustomerGroup(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam CustomerGroupVo vo) throws Exception {
		customerGroupService.updateCustomerGroup(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam CustomerGroupVo[] vos) throws Exception {
		customerGroupService.updatesCustomerGroup(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam CustomerGroupVo vo) throws Exception {
		customerGroupService.deleteCustomerGroup(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam CustomerGroupVo[] vos) throws Exception {
		customerGroupService.deletesCustomerGroup(vos);
		return VoidVo.VOID;
	}
}
