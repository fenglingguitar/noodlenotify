package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodle.common.mvc.vo.VoidVo;
import org.fl.noodlenotify.console.service.CustomerService;
import org.fl.noodlenotify.console.vo.CustomerVo;

@Controller
@RequestMapping(value = "console/customer")
public class CustomerController {
	@Autowired
	private CustomerService customerService;
	
	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<CustomerVo> queryPage(@NoodleRequestParam CustomerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return customerService.queryCustomerPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querypagebyequal")
	@NoodleResponseBody
	public PageVo<CustomerVo> queryPageByEqual(@NoodleRequestParam CustomerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return customerService.queryCustomerPageByEqual(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}
	
	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<CustomerVo> queryList(@NoodleRequestParam CustomerVo vo) throws Exception {
		return customerService.queryCustomerList(vo);
	}
	
	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam CustomerVo vo) throws Exception {
		customerService.insertCustomer(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam CustomerVo[] vos) throws Exception {
		customerService.insertsCustomer(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam CustomerVo vo) throws Exception {
		customerService.updateCustomer(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam CustomerVo[] vos) throws Exception {
		customerService.updatesCustomer(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam CustomerVo vo) throws Exception {
		customerService.deleteCustomer(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam CustomerVo[] vos) throws Exception {
		customerService.deletesCustomer(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/deletesgroup")
	@NoodleResponseBody
	public VoidVo deletesgroup(@NoodleRequestParam CustomerVo[] vos) throws Exception {
		customerService.deletesgroupCustomer(vos);
		return VoidVo.VOID;
	}
}
