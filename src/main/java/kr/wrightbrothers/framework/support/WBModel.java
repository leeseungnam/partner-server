package kr.wrightbrothers.framework.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.web.servlet.ModelAndView;

@JsonInclude(Include.NON_NULL)
public class WBModel extends ModelAndView{

	public WBModel() {
		super(WBKey.View);
	}
	
	public WBModel(String viewName) {
		super(viewName);
	}
	
}
