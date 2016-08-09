package com.activity.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDiagramCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MyController {
	
	 @Autowired
	 private RuntimeService runtimeService;
	
	 @Autowired
	 private TaskService taskService;
	 
	 @Autowired
	 private ManagementService managementService;
	 
	 @Autowired
	 private RepositoryService repositoryService;
	 
	 @Autowired
	 private ProcessEngine processEngine;

	 
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
	
	
	 
	@RequestMapping("/hary")
	public String assign(Map<String, Object> mode) {
		Map<String, Object> variables = new HashMap<String, Object>();
       variables.put("username", "hary");
       List<Task> tasksAssignee = taskService.createTaskQuery().taskAssignee("hary").list();
       List<TaskMess> mess = new ArrayList();
	   
	   for(Task t:tasksAssignee){
		   Execution exe = runtimeService.createExecutionQuery().executionId(t.getExecutionId()).singleResult();
		   String processId = exe.getProcessInstanceId();
		  
		   //String processId = t.getProcessDefinitionId();
		   
		   

		   System.out.println("hahahahahahhahah");
		   TaskMess temp = new TaskMess();
		   temp.setTask(t);
		   temp.setUsername((String)runtimeService.getVariable(exe.getId(), "username"));
		   temp.setProcessId(processId);
		   mess.add(temp);  
	   }
	   mode.put("tasks", mess);
	   return "hary";
      
	}
	
	@RequestMapping("/haryassign")
	public String haryAssign(String name, String taskId){
		System.out.println(name+"    "+taskId);
		taskService.setVariable(taskId, "todoUser", name);
		taskService.complete(taskId);
		return "redirect:/process?role=" + name; 
	}
	
	
	
	@RequestMapping("/process")
	public String managerInterview(@RequestParam("role") String username,Map<String, Object> mode) {
	    List<Task> tasksAssignee = taskService.createTaskQuery().taskAssignee(username).list();
		List<Task> tasksCandidateUser = taskService.createTaskQuery().taskCandidateUser(username).list();
//	   if(tasksAssignee.size()==0){
//		   System.out.println("您没有需要处理的任务");
//		   return "error";
//	   }
	   
	   List<TaskMess> mess = new ArrayList();
	   
	   for(Task t:tasksAssignee){
		   Execution exe = runtimeService.createExecutionQuery().executionId(t.getExecutionId()).singleResult();
		   String processId = exe.getProcessInstanceId();
		  
		   //String processId = t.getProcessDefinitionId();
		   
		   

		 //  System.out.println(username+"通过"+runtimeService.getVariable(exe.getId(), "username"));
		   TaskMess temp = new TaskMess();
		   temp.setTask(t);
		   temp.setUsername((String)runtimeService.getVariable(exe.getId(), "username"));
		   temp.setProcessId(processId);
		   mess.add(temp);
		   //taskService.complete(t.getId());
		   
	   }
	   
	   
	   List<TaskMess> mess2 = new ArrayList();
	   
	   for(Task t:tasksCandidateUser){
		   Execution exe = runtimeService.createExecutionQuery().executionId(t.getExecutionId()).singleResult();
		   String processId = exe.getProcessInstanceId();
		 //  System.out.println(username+"通过"+runtimeService.getVariable(exe.getId(), "username"));
		   TaskMess temp = new TaskMess();
		   temp.setTask(t);
		   temp.setUsername((String)runtimeService.getVariable(exe.getId(), "username"));
		   temp.setProcessId(processId);
		   mess2.add(temp);
		   //taskService.complete(t.getId());
	   }
	   
	   
	   
	   
	   
	   mode.put("tasks", mess);
	   mode.put("tasks2", mess2);
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
	
	@RequestMapping("/claim/{id}/{exeid}")
	public String claimApply(@PathVariable("id") String id,@PathVariable("exeid") String exeid,Map<String, Object> mode) {
	//public String apply(@RequestParam("task") Task task,Map<String, Object> mode) {
		Execution exe = runtimeService.createExecutionQuery().executionId(exeid).singleResult();
		System.out.println("claim"+runtimeService.getVariable(exe.getId(), "username"));
		taskService.claim(id, "hr");
        return "ok";
	}
	
	@RequestMapping("/image/{processId}")
	public void gene(HttpServletRequest request,HttpServletResponse response,@PathVariable("processId") String processId){
		 FileInputStream fis = null;
		    response.setContentType("image/gif");
		    try {
		    	OutputStream out = response.getOutputStream();
		    	//Command cmd=null;
				//这里输入的流程ID，不是KEY
				//cmd=new GetDeploymentProcessDiagramCmd(processId);
		    	ProcessInstance pi = runtimeService.createProcessInstanceQuery()
		    			.processInstanceId(processId).singleResult();
		    	
		    	RepositoryServiceImpl rs = (RepositoryServiceImpl)processEngine.getRepositoryService();
		    	
		    	ProcessDefinitionEntity pd = (ProcessDefinitionEntity) rs.getDeployedProcessDefinition(pi.getProcessDefinitionId());
		    	 BpmnModel bpmnModel =
		    			 repositoryService.getBpmnModel(pi.getProcessDefinitionId());
		    	 
		    	 				 //ProcessDiagramGenerator.generateDiagram
		    	 InputStream is = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator()
		    	 .generateDiagram(bpmnModel, "png", runtimeService.getActiveActivityIds(pi.getId()));
		    	//InputStream is = ProcessDiagramGenerator.generateDiagram(pd,"png",
		    		//	runtimeService.getActiveActivityIds(processId));
				ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		    	int len = 0;
		    	while((len=is.read())!=-1){
		    		bytestream.write(len);
		    	}
				byte[] b = bytestream.toByteArray();
				bytestream.close();
				out.write(b);
				
//				while ((len = is.read(b, 0, 1024)) != -1) {
//					System.out.write(b, 0, len);
//					out.write(b);
//					}
				out.flush();
		    	
		    	
		    	
		        
//		        File file = new File("timg.jpg");
//		        fis = new FileInputStream(file);
//		        byte[] b = new byte[fis.available()];
//		        fis.read(b);
//		        out.write(b);
//		        out.flush();
		    } catch (Exception e) {
		         e.printStackTrace();
		    } finally {
		        if (fis != null) {
		            try {
		               fis.close();
		            } catch (IOException e) {
			        e.printStackTrace();
			    }   
		          }
		    }
	}

}

class TaskMess{
	private Task task;
	private String username;
	private String processId;

	
	public TaskMess(){
	}

	public void setProcessId(String processId){
		this.processId = processId;
	}
	
	public String getProcessId(){
		return processId;
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





