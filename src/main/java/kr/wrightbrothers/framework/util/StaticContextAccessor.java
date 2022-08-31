package kr.wrightbrothers.framework.util;

import org.springframework.context.ApplicationContext;

public class StaticContextAccessor {
	
	private static ApplicationContext context;

	public StaticContextAccessor(ApplicationContext applicationContext) {
		context = applicationContext;
	}

	public static <T> T getBean(Class<T> clazz) {
		return context.getBean(clazz);
	}
}