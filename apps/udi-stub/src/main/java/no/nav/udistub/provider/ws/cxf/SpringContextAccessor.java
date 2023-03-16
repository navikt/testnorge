package no.nav.udistub.provider.ws.cxf;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;




public class SpringContextAccessor implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * Returns the Spring managed bean instance of the given class type if it exists.
     * Returns null otherwise.
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
    
    public static <T> T getBean(String beanName,Class<T> clazz) {
        return context.getBean(beanName, clazz);
    }

    public static Boolean hasContext() {
        return context != null;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextAccessor.context = context;
    }
}
