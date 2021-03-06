package com.activity.conf;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
	@Autowired
	IdentityService identityservice;
	
	
	@Bean
	InitializingBean usersAndGroupsInitializer(final IdentityService identityService) {

	    return new InitializingBean() {
	        public void afterPropertiesSet() throws Exception {

	        	User user = identityservice.createUserQuery().userId("manager").singleResult();
	    		if(user == null){
	    			User manager = identityservice.newUser("manager");
	    			manager.setPassword("manager");
	    	        identityservice.saveUser(manager);
	    		}
	    		
	    		user = identityservice.createUserQuery().userId("hr").singleResult();
	    		if(user == null){
	    			User hr = identityservice.newUser("hr");
	    	        hr.setPassword("hr");
	    	        identityservice.saveUser(hr);
	    		}
	    		
	    		user = identityservice.createUserQuery().userId("hary").singleResult();
	    		if(user == null){
	    			User hr = identityservice.newUser("hary");
	    	        hr.setPassword("hary");
	    	        identityservice.saveUser(hr);
	    		}

	        }
	    };
	}
	
//	@Bean
//	public String userinit() {
//		
//		User user = identityservice.createUserQuery().userId("manager").singleResult();
//		if(user == null){
//			User manager = identityservice.newUser("manager");
//			manager.setPassword("manager");
//	        identityservice.saveUser(manager);
//		}
//		
//		user = identityservice.createUserQuery().userId("hr").singleResult();
//		if(user == null){
//			User hr = identityservice.newUser("hr");
//	        hr.setPassword("hr");
//	        identityservice.saveUser(hr);
//		}
//       
//        return "hello world";
//	}
}
