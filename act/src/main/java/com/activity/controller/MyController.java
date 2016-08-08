package com.activity.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MyController {
	
	 @Autowired
	 private RuntimeService runtimeService;
	
	 @Autowired
	 private TaskService taskService;
	 
	@RequestMapping("/start")
	public String startInterview(@RequestParam("username") String username,Map<String, Object> mode) {
		Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("username", username);
        runtimeService.startProcessInstanceByKey("firstprocess", variables);
        return "index";
        
	}
	
	@RequestMapping("/process")
	public String managerInterview(@RequestParam("role") String username,Map<String, Object> mode) {
	   List<Task> tasks = taskService.createTaskQuery().taskAssignee(username).list();
	   
	   if(tasks.size()==0){
		   System.out.println("您没有需要处理的任务");
		   return "error";
	   }
	   
	   for(Task t:tasks){
		   Execution exe = runtimeService.createExecutionQuery().executionId(t.getExecutionId()).singleResult();
		   System.out.println(username+"通过"+runtimeService.getVariable(exe.getId(), "username"));
		   taskService.complete(t.getId());
	   }
	   return "success";
	}
	
	
	
}