package com.neofoc.app.examples;

import com.neofoc.app.config.LabotronProperties;
import com.neofoc.app.utils.SpringContextUtil;

/**
 * Example class demonstrating how to use SpringContextUtil to access Spring beans
 * from non-Spring managed components.
 */
public class NonSpringComponent {

    /**
     * This method demonstrates accessing Spring beans from a static context
     */
    public static void accessSpringBeansFromStaticContext() {
        // Get the LabotronProperties bean
        LabotronProperties properties = SpringContextUtil.getBean(LabotronProperties.class);
        
        // Now you can use the properties
        System.out.println("LIS to Labotron Queue: " + properties.getConnector().getLis2LabotronQueue());
        System.out.println("Labotron to LIS Queue: " + properties.getConnector().getLabotron2LisQueue());
        System.out.println("Connector enabled: " + properties.getConnector().isEnabled());
    }
    
    /**
     * This method demonstrates accessing multiple Spring beans from a non-Spring managed component
     */
    public void useMultipleSpringBeans() {
        // Get any service bean you need
        // Replace these with your actual service classes
        try {
            Object someService = SpringContextUtil.getBean("communicationLogService");
            System.out.println("Successfully retrieved service: " + someService.getClass().getName());
            
            // You can get beans by name and cast them
            // SomeRepository repository = (SomeRepository) SpringContextUtil.getBean("someRepository");
            
            // Or get beans by class type directly
            // SomeOtherService otherService = SpringContextUtil.getBean(SomeOtherService.class);
        } catch (Exception e) {
            System.err.println("Error retrieving beans: " + e.getMessage());
        }
    }
    
    /**
     * You can also access the entire ApplicationContext if needed
     */
    public void workWithApplicationContext() {
        // Get all bean names
        String[] beanNames = SpringContextUtil.getApplicationContext().getBeanDefinitionNames();
        System.out.println("Available beans: ");
        for (String beanName : beanNames) {
            System.out.println(" - " + beanName);
        }
    }
}
