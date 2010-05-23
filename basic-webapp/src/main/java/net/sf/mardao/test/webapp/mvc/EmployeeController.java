package net.sf.mardao.test.webapp.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mardao.test.webapp.dao.EmployeeDao;
import net.sf.mardao.test.webapp.domain.Employee;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class EmployeeController extends SimpleFormController {
	
	static final Logger LOG = Logger.getLogger(EmployeeController.class);
	
	private EmployeeDao employeeDao;
	
	@Override
	protected ModelAndView onSubmit(Object command) throws Exception {
		
		return super.onSubmit(command);
	}
	
	@Override
	protected ModelAndView showForm(HttpServletRequest request,
			HttpServletResponse response, BindException errors)
			throws Exception {
		final ModelAndView mav = new ModelAndView(getFormView());
		
		LOG.info("showForm() -> " + mav.getViewName());
		List<Employee> employees = employeeDao.findAll();
		mav.addObject("employees", employees);
		
		return mav;
	}

	public void setEmployeeDao(EmployeeDao employeeDao) {
		this.employeeDao = employeeDao;
	}

	public EmployeeDao getEmployeeDao() {
		return employeeDao;
	}

}
