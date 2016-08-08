package com.activity.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MyController {
	
	 @Autowired
	 private RuntimeService runtimeService;
	
	 @Autowired
	 private TaskService taskService;
	 
	 @RequestMapping("/")
		public String index() {
	        return "apply";
	        
		}
	 
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
	   
	   List<TaskMess> mess = new ArrayList();
	   
	   for(Task t:tasks){
		   Execution exe = runtimeService.createExecutionQuery().executionId(t.getExecutionId()).singleResult();
		 //  System.out.println(username+"通过"+runtimeService.getVariable(exe.getId(), "username"));
		   TaskMess temp = new TaskMess();
		   temp.setTask(t);
		   temp.setUsername((String)runtimeService.getVariable(exe.getId(), "username"));
		   mess.add(temp);
		   //taskService.complete(t.getId());
	   }
	   mode.put("tasks", mess);
	   return "success";
	}
	
	@RequestMapping("/apply/{id}/{exeid}")
	public String apply(@PathVariable("id") String id,@PathVariable("exeid") String exeid,Map<String, Object> mode) {
	//public String apply(@RequestParam("task") Task task,Map<String, Object> mode) {
		Execution exe = runtimeService.createExecutionQuery().executionId(exeid).singleResult();
		System.out.println("通过"+runtimeService.getVariable(exe.getId(), "username"));
		taskService.complete(id);
        return "ok";
	}
	
	
}

class TaskMess{
	private Task task;
	private String username;
	
	public TaskMess(){
	}
	
	public void setTask(Task task){
		this.task = task;
	}
	
	public void setUsername(String username){
		this.username=username;
	}
	
	public Task getTask(){
		return task;
	}
	
	public String getUsername(){
		return username;
	}
	
}





