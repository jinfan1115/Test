package com.glp.collie.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 流程工具类<br>
 * 此核心类主要处理：流程启动、任务节点查询、通过、驳回、会签、转办、中止核心操作<br>
 *
 * @author dcliu
 *
 */
public class WorkFlowUtils {
	
	public static final Logger log = LoggerFactory.getLogger(WorkFlowUtils.class);
	
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	
	/**
	 * 根据流程实例Id获取历史参数
	 * 
	 * @param procInstId
	 * @return
	 */
	public HistoricVariableInstance getHisVariables(String procInstId,String name){
		return historyService.createHistoricVariableInstanceQuery()
		.processInstanceId(procInstId).variableName(name).singleResult();
	}
	/**
     * 根据任务流程实例Id获取
     *
     * @param processInstanceId
     *        任务ID
     * @return
     * @throws Exception
     */
	public HistoricTaskInstance findHisTaskProcID(String pId){
		List<HistoricTaskInstance> hisTaskInstances = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(pId).finished().orderByHistoricTaskInstanceEndTime().desc().list();
		return "ApprovePass".equals(hisTaskInstances.get(0).getTaskDefinitionKey())?hisTaskInstances.get(1):hisTaskInstances.get(0);
	}
	/**
	 * 根据流程key启动流程实例
	 * 
	 * @param defineKey
	 * @param varibles
	 * @return
	 */
	public ProcessInstance startWorkFlow(String defineKey,Map<String,Object> varibles){
		if(varibles==null)
			varibles = new HashMap<String,Object>();
		 return runtimeService.startProcessInstanceByKey(defineKey, varibles);
	}
	/**
     * 根据历史任务ID获得历史任务实例
     *
     * @param taskId
     *        任务ID
     * @return
     * @throws Exception
     */
	public HistoricTaskInstance findHisTaskDefineKeyById(String taskId) throws Exception {
		HistoricTaskInstance hisTaskInstance = historyService.createHistoricTaskInstanceQuery()
   			 .taskId(taskId).singleResult();
		 if (hisTaskInstance == null) {
			 throw new Exception("历史任务实例未找到!");
		 }
		 return hisTaskInstance;
	}
	/**
     * 根据任务ID判断任务实例是否是并发任务
     *
     * @param taskId
     *        任务ID
     * @return
     * @throws Exception
     */
	public boolean isParalleOrInclusive(String taskId) throws Exception {
		 ActivityImpl currActivity = findActivitiImpl(taskId,null);
		 boolean flag = false,flag2 = false;
         List<PvmTransition> nextTransitionList = currActivity.getIncomingTransitions(); 
         for(PvmTransition transition : nextTransitionList) {
        	 PvmActivity pvm = transition.getSource();
        	 if(pvm.getProperty("type").equals("inclusiveGateway") || pvm.getProperty("type").equals("parallelGateWay")){
        		flag = true; 
        	 }
         }
         if(!flag){
        	 return flag;
         }
         List<PvmTransition> pvmTransitionList = currActivity.getOutgoingTransitions();  
         for(PvmTransition pvmTransition:pvmTransitionList){ 
        	 PvmActivity pvm = pvmTransition.getDestination();
        	 if(pvm.getProperty("type").equals("inclusiveGateway") || pvm.getProperty("type").equals("parallelGateWay")){
        		 flag2 = true;
        	 }
         }
         return flag && flag2;
	}
	
	/**
     * 根据任务流程实例Id获取
     *
     * @param processInstanceId
     *        任务ID
     * @return
     * @throws Exception
     */
	public List<HistoricTaskInstance> findPreHisTaskByPID(String pId){
		List<HistoricTaskInstance> hisTaskInstances = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(pId).finished().orderByHistoricTaskInstanceEndTime().desc().list();
		return hisTaskInstances;
	}
	/**
	 * 通过任务节点定义key获取任务实例
	 * 
	 * @param defineKey
	 * @return
	 * @throws Exception
	 */
	public HistoricTaskInstance findHisTaskDefineKeyByKey(String defineKey) throws Exception {
		HistoricTaskInstance hisTaskInstance = historyService.createHistoricTaskInstanceQuery()
   			 .taskDefinitionKey("defineKey").singleResult();
		 if (hisTaskInstance == null) {
			 throw new Exception("历史任务实例未找到!");
		 }
		 return hisTaskInstance;
	}
	/**
     * 根据任务ID获得任务实例
     *
     * @param taskId
     *        任务ID
     * @return
     * @throws Exception
     */
     public TaskEntity findTaskById(String taskId) throws Exception {
    	 TaskEntity task = (TaskEntity) taskService.createTaskQuery()
    			 .taskId(taskId).singleResult();
		 if (task == null) {
			 throw new Exception("任务实例未找到!");
		 }
		 return task;
	 }
     
     /**
      * 根据任务ID获取对应的流程实例
      *
      * @param taskId
      *        任务ID
      * @return
      * @throws Exception
      */
     public ProcessInstance findProcessInstanceByTaskId(String taskId) throws Exception {
         ProcessInstance processInstance = runtimeService
             .createProcessInstanceQuery().processInstanceId(
             findTaskById(taskId).getProcessInstanceId())
             .singleResult();
         if (processInstance == null) {
             throw new Exception("流程实例未找到!");
         }
         return processInstance;
     }
     /**
      * 根据任务ID获取流程定义
      *
      * @param taskId
      *        任务ID
      * @return
      * @throws Exception
      */
     public ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(String taskId) throws Exception {
         ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity)((RepositoryServiceImpl) repositoryService)
             .getDeployedProcessDefinition(findTaskById(taskId)
             .getProcessDefinitionId());
         if (processDefinition == null) {
             throw new Exception("流程定义未找到!");
         }
         return processDefinition;
     }
     
     /**
      * 根据流程实例ID查询所有任务集合
      *
      * @param processInstanceId
      * 
      * @return
      */
     public List<Task> findTaskListByPID(String processInstanceId) {
         return taskService.createTaskQuery().processInstanceId(
             processInstanceId).list();
     }
     /**
      * 根据流程实例ID和任务key值查询所有同级任务集合
      *
      * @param processInstanceId
      * @param key
      * 
      * @return
      */
     public List<Task> findTaskListByKey(String processInstanceId, String key) {
         return taskService.createTaskQuery().processInstanceId(
             processInstanceId).taskDefinitionKey(key).list();
     }
     
     
     /**
      * 根据任务ID和节点ID获取活动节点 <br>
      *
      * @param taskId
      *        任务ID
      * @param activityId
      *        活动节点ID <br>
      *        如果为null或""，则默认查询当前活动节点 <br>
      *        如果为"end"，则查询结束节点 <br>
      *
      * @return
      * @throws Exception
      */
     public ActivityImpl findActivitiImpl(String taskId, String activityId) throws Exception {
         // 取得流程定义
         ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);
         // 获取当前活动节点ID
         if (StringUtils.isBlank(activityId)) {
             activityId = findTaskById(taskId).getTaskDefinitionKey();
         }
         // 根据流程定义，获取该流程实例的结束节点
         if (activityId.toUpperCase().equals("END")) {
             for (ActivityImpl activityImpl: processDefinition.getActivities()) {
                 List<PvmTransition> pvmTransitionList = activityImpl
                     .getOutgoingTransitions();
                 if (pvmTransitionList.isEmpty()) {
                     return activityImpl;
                 }
             }
         }
         // 根据节点ID，获取对应的活动节点
         ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)
             .findActivity(activityId);
         return activityImpl;
     }
     
     /**
      * 查询指定任务节点的最新记录
      *
      * @param processInstance
      *            流程实例
      * @param activityId
      * @return
      */
     public HistoricActivityInstance findHistoricUserTask(ProcessInstance processInstance, String activityId) {
         HistoricActivityInstance rtnVal = null;
         // 查询当前流程实例审批结束的历史节点
         List<HistoricActivityInstance> historicActivityInstances = historyService
             .createHistoricActivityInstanceQuery().activityType("userTask")
             .processInstanceId(processInstance.getId()).activityId(
             activityId).finished()
             .orderByHistoricActivityInstanceEndTime().desc().list();
         if (historicActivityInstances.size() > 0) {
             rtnVal = historicActivityInstances.get(0);
         }
         return rtnVal;
     }
     
     
     /**
      * 根据流入任务集合，查询最近一次的流入任务节点
      *
      * @param processInstance
      *            流程实例
      * @param tempList
      *            流入任务集合
      * @return
      */
     public ActivityImpl filterNewestActivity(ProcessInstance processInstance,List<ActivityImpl> tempList) {
         while (tempList.size() > 0) {
             ActivityImpl activity_1 = tempList.get(0);
             HistoricActivityInstance activityInstance_1 = findHistoricUserTask(
                 processInstance, activity_1.getId());
             if (activityInstance_1 == null) {
                 tempList.remove(activity_1);
                 continue;
             }
             if (tempList.size() > 1) {
                 ActivityImpl activity_2 = tempList.get(1);
                 HistoricActivityInstance activityInstance_2 = findHistoricUserTask(
                     processInstance, activity_2.getId());
                 if (activityInstance_2 == null) {
                     tempList.remove(activity_2);
                     continue;
                 }
                 if (activityInstance_1.getEndTime().before(
                     activityInstance_2.getEndTime())) {
                     tempList.remove(activity_1);
                 } else {
                     tempList.remove(activity_2);
                 }
             } else {
                 break;
             }
         }
         if (tempList.size() > 0) {
             return tempList.get(0);
         }
         return null;
     }
     /**
      * 反向排序list集合，便于驳回节点按顺序显示
      *
      * @param list
      * @return
      */
     public List<ActivityImpl> reverList(List<ActivityImpl> list) {
         List<ActivityImpl> rtnList = new ArrayList<ActivityImpl> ();
         // 由于迭代出现重复数据，排除重复
         for (int i = list.size(); i > 0; i--) {
             if (!rtnList.contains(list.get(i - 1)))
                 rtnList.add(list.get(i - 1));
         }
         return rtnList;
     }
     /**
      * 根据当前节点，查询输出流向是否为并行终点，如果为并行终点，则拼装对应的并行起点ID
      *
      * @param activityImpl
      *            当前节点
      * @return
      */
     public String findParallelGatewayId(ActivityImpl activityImpl) {
         List<PvmTransition> incomingTransitions = activityImpl
             .getOutgoingTransitions();
         for (PvmTransition pvmTransition: incomingTransitions) {
             TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
             activityImpl = transitionImpl.getDestination();
             String type = (String) activityImpl.getProperty("type");
             if ("parallelGateway".equals(type)) { // 并行路线
                 String gatewayId = activityImpl.getId();
                 String gatewayType = gatewayId.substring(gatewayId
                     .lastIndexOf("_") + 1);
                 if ("END".equals(gatewayType.toUpperCase())) {
                     return gatewayId.substring(0, gatewayId.lastIndexOf("_")) + "_start";
                 }
             }
         }
         return null;
     }
     
     /**
      * 迭代循环流程树结构，查询当前节点可驳回的任务节点
      *
      * @param taskId
      *            当前任务ID
      * @param currActivity
      *            当前活动节点
      * @param rtnList
      *            存储回退节点集合
      * @param tempList
      *            临时存储节点集合（存储一次迭代过程中的同级userTask节点）
      * @return 回退节点集合
      */
     public List<ActivityImpl> iteratorBackActivity(String taskId,ActivityImpl currActivity,
    		 List<ActivityImpl> rtnList,List<ActivityImpl> tempList) throws Exception {
         // 查询流程定义，生成流程树结构
         ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
         // 当前节点的流入来源
         List<PvmTransition> incomingTransitions = currActivity
             .getIncomingTransitions();
         // 条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点
         List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();
         // 并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点
         List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();
         // 遍历当前节点所有流入路径
         for (PvmTransition pvmTransition: incomingTransitions) {
             TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
             ActivityImpl activityImpl = transitionImpl.getSource();
             String type = (String) activityImpl.getProperty("type");
             /**
              * 并行节点配置要求：<br>
              * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)
              */
             if ("parallelGateway".equals(type)) { // 并行路线
                 String gatewayId = activityImpl.getId();
                 String gatewayType = gatewayId.substring(gatewayId
                     .lastIndexOf("_") + 1);
                 if ("START".equals(gatewayType.toUpperCase())) { // 并行起点，停止递归
                     return rtnList;
                 } else { // 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
                     parallelGateways.add(activityImpl);
                 }
             } else if ("startEvent".equals(type)) { // 开始节点，停止递归
                 return rtnList;
             } else if ("userTask".equals(type)) { // 用户任务
                 tempList.add(activityImpl);
             } else if ("exclusiveGateway".equals(type)) { // 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
                 currActivity = transitionImpl.getSource();
                 exclusiveGateways.add(currActivity);
             }
         }
         /**
          * 迭代条件分支集合，查询对应的userTask节点
          */
         for (ActivityImpl activityImpl: exclusiveGateways) {
             iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
         }
         /**
          * 迭代并行集合，查询对应的userTask节点
          */
         for (ActivityImpl activityImpl: parallelGateways) {
             iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
         }
         /**
          * 根据同级userTask集合，过滤最近发生的节点
          */
         currActivity = filterNewestActivity(processInstance, tempList);
         if (currActivity != null) {
             // 查询当前节点的流向是否为并行终点，并获取并行起点ID
             String id = findParallelGatewayId(currActivity);
             if (StringUtils.isBlank(id)) { // 并行起点ID为空，此节点流向不是并行终点，符合驳回条件，存储此节点
                 rtnList.add(currActivity);
             } else { // 根据并行起点ID查询当前节点，然后迭代查询其对应的userTask任务节点
                 currActivity = findActivitiImpl(taskId, id);
             }
             // 清空本次迭代临时集合
             tempList.clear();
             // 执行下次迭代
             iteratorBackActivity(taskId, currActivity, rtnList, tempList);
         }
         return rtnList;
     }
     
     /**
      * @param taskId
      *            当前任务ID
      * @param variables
      *            流程变量
      * @param activityId
      *            流程转向执行任务节点ID<br>
      *            此参数为空，默认为提交操作
      * @throws Exception
      */
     public void commitProcess(String taskId, Map<String,Object> variables,String activityId) throws Exception {
         if (variables == null) {
             variables = new HashMap<String,Object>();
         }
         // 跳转节点为空，默认提交操作
         if (StringUtils.isBlank(activityId)) {
             taskService.complete(taskId, variables);
         } else { // 流程转向操作
             turnTransition(taskId, activityId, variables);
         }
     }
  
  
     /**
      * 通过任务ID提交流程
      * 
      * @param taskId
      * @param variables
      */
    public void complete(String taskId, Map<String,Object> variables){
         if (variables == null) {
             variables = new HashMap<String,Object>();
         }
         taskService.complete(taskId, variables);
     }
     /**
      * 清空指定活动节点流向
      *
      * @param activityImpl
      *            活动节点
      * @return 节点流向集合
      */
     public List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
         // 存储当前节点所有流向临时变量
         List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition> ();
         // 获取当前节点所有流向，存储到临时变量，然后清空
         List<PvmTransition> pvmTransitionList = activityImpl
             .getOutgoingTransitions();
         for (PvmTransition pvmTransition: pvmTransitionList) {
             oriPvmTransitionList.add(pvmTransition);
         }
         pvmTransitionList.clear();
         return oriPvmTransitionList;
     }
  
     /**
      * 还原指定活动节点流向
      *
      * @param activityImpl
      *            活动节点
      * @param oriPvmTransitionList
      *            原有节点流向集合
      */
     public void restoreTransition(ActivityImpl activityImpl,List<PvmTransition> oriPvmTransitionList) {
         // 清空现有流向
         List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
         pvmTransitionList.clear();
         // 还原以前流向
         for (PvmTransition pvmTransition: oriPvmTransitionList) {
             pvmTransitionList.add(pvmTransition);
         }
     }
  
     /**
      * 流程转向操作
      *
      * @param taskId
      *            当前任务ID
      * @param activityId
      *            目标节点任务ID
      * @param variables
      *            流程变量
      * @throws Exception
      */
     public void turnTransition(String taskId, String activityId,Map<String,Object> variables) throws Exception {
         // 当前节点
         ActivityImpl currActivity = findActivitiImpl(taskId, null);
         // 清空当前流向
         List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);
         // 创建新流向
         TransitionImpl newTransition = currActivity.createOutgoingTransition();
         // 目标节点
         ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
         // 设置新流向的目标节点
         newTransition.setDestination(pointActivity);
         // 执行转向任务
         taskService.complete(taskId, variables);
         // 删除目标节点新流入
         pointActivity.getIncomingTransitions().remove(newTransition);
         // 还原以前流向
         restoreTransition(currActivity, oriPvmTransitionList);
     }
     
     
     /**
      * 根据当前任务ID，查询可以驳回的任务节点
      *
      * @param taskId
      *        当前任务ID
      */
    public List<ActivityImpl> findBackAvtivity(String taskId) throws Exception {
         List<ActivityImpl> rtnList = null;
         rtnList = iteratorBackActivity(taskId, findActivitiImpl(taskId,
                 null), new ArrayList<ActivityImpl>(),
                 new ArrayList<ActivityImpl>());
         return reverList(rtnList);
     }
    
     /**
      * 审批通过(驳回直接跳回功能需后续扩展)
      *
      * @param taskId
      *            当前任务ID
      * @param variables
      *            流程存储参数
      * @throws Exception
      */
     public void passProcess(String taskId, Map <String,Object> variables)
     throws Exception {
         List<Task> tasks = taskService.createTaskQuery().taskId(taskId)
             .taskDescription("jointProcess").list();
         for (Task task: tasks) { // 级联结束本节点发起的会签任务
             commitProcess(task.getId(), null, null);
         }
         commitProcess(taskId, variables, null);
     }
      
     /**
      * 驳回流程
      *
      * @param taskId
      *            当前任务ID
      * @param activityId
      *            驳回节点ID
      * @param variables
      *            流程存储参数
      * @throws Exception
      */
     public void backProcess(String taskId, String activityId,
         Map<String, Object> variables) throws Exception {
         if (StringUtils.isBlank(activityId)) {
             throw new Exception("驳回目标节点ID为空！");
         }
         // 查询本节点发起的会签任务，并结束
         List<Task> tasks = taskService.createTaskQuery().taskId(taskId)
             .taskDescription("jointProcess").list();
         for (Task task: tasks) {
             commitProcess(task.getId(), null, null);
         }
         // 查找所有并行任务节点，同时驳回
         List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(
             taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());
         for(Task task: taskList){
             commitProcess(task.getId(), variables, activityId);
         }
     }
     /**
      * 取回流程
      *
      * @param taskId
      *        当前任务ID
      * @param activityId
      *        取回节点ID
      * @throws Exception
      */
     public void callBackProcess(String taskId, String activityId)
     throws Exception {
         if (StringUtils.isBlank(activityId)) {
             throw new Exception("目标节点ID为空！");
         }
         // 查找所有并行任务节点，同时取回
         List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(
             taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());
         for (Task task: taskList) {
             commitProcess(task.getId(), null, activityId);
         }
     }
     /**
      * 中止流程(特权人直接审批通过等)
      *
      * @param taskId
      */
     public void endProcess(String taskId) throws Exception {
         ActivityImpl endActivity = findActivitiImpl(taskId, "end");
         commitProcess(taskId, null, endActivity.getId());
     }
     /**
      * 会签操作
      *
      * @param taskId
      *            当前任务ID
      * @param userCodes
      *            会签人账号集合
      * @throws Exception
      */
     public void jointProcess(String taskId, List<String> userCodes)throws Exception {
         for (String userCode: userCodes) {
             TaskEntity task = (TaskEntity) taskService.newTask(UUID.randomUUID().toString());
             task.setAssignee(userCode);
             task.setName(findTaskById(taskId).getName() + "-会签");
             task.setProcessDefinitionId(findProcessDefinitionEntityByTaskId(
                 taskId).getId());
             task.setProcessInstanceId(findProcessInstanceByTaskId(taskId)
                 .getId());
             task.setParentTaskId(taskId);
             task.setDescription("jointProcess");
             taskService.saveTask(task);
         }
     }
     /**
      * 转办流程
      *
      * @param taskId
      *            当前任务节点ID
      * @param userCode
      *            被转办人Code
      */
     public void transferAssignee(String taskId, String userCode) {
         taskService.setAssignee(taskId, userCode);
     }
     
     public void taskRollBack(String taskId){  
         try {  
             Map<String, Object> variables;  
             // 取得当前任务  
             HistoricTaskInstance currTask = historyService  
                     .createHistoricTaskInstanceQuery().taskId(taskId)  
                     .singleResult();  
             // 取得流程实例  
             ProcessInstance instance = runtimeService  
                     .createProcessInstanceQuery()  
                     .processInstanceId(currTask.getProcessInstanceId())  
                     .singleResult();  
             if (instance == null) {
            	 throw new RuntimeException("流程实例不存在！");
             }  
             variables = instance.getProcessVariables();  
             // 取得流程定义  
             ProcessDefinitionEntity definition = findProcessDefinitionEntityByTaskId(taskId);  
             if (definition == null) {  
                 throw new RuntimeException("流程定义不存在！");
             }  
             // 取得上一步活动  
             ActivityImpl currActivity = ((ProcessDefinitionImpl) definition)  
                     .findActivity(currTask.getTaskDefinitionKey());  
             List<PvmTransition> nextTransitionList = currActivity
                     .getIncomingTransitions(); 
             
             // 清除当前活动的出口  
             List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();  
             List<PvmTransition> pvmTransitionList = currActivity  
                     .getOutgoingTransitions();  
             for (PvmTransition pvmTransition : pvmTransitionList) {  
                 oriPvmTransitionList.add(pvmTransition);  
             }  
             pvmTransitionList.clear();  
    
             // 建立新出口  
             List<TransitionImpl> newTransitions = new ArrayList<TransitionImpl>();  
             for (PvmTransition nextTransition : nextTransitionList) {  
                 PvmActivity nextActivity = nextTransition.getSource();  
                 ActivityImpl nextActivityImpl = ((ProcessDefinitionImpl) definition)  
                         .findActivity(nextActivity.getId());  
                 TransitionImpl newTransition = currActivity  
                         .createOutgoingTransition();  
                 newTransition.setDestination(nextActivityImpl);  
                 newTransitions.add(newTransition);  
             }  
             // 完成任务  
             List<Task> tasks = taskService.createTaskQuery()  
                     .processInstanceId(instance.getId())  
                     .taskDefinitionKey(currTask.getTaskDefinitionKey()).list();  
             for (Task task : tasks) {  
                 taskService.complete(task.getId(), variables);  
                 historyService.deleteHistoricTaskInstance(task.getId());  
             }  
             // 恢复方向  
             for (TransitionImpl transitionImpl : newTransitions) {  
                 currActivity.getOutgoingTransitions().remove(transitionImpl);  
             }  
             for (PvmTransition pvmTransition : oriPvmTransitionList) {  
                 pvmTransitionList.add(pvmTransition);  
             }  
             return ;  
         } catch (Exception e) {  
             return ;  
         }  
     }  
  
     public void setHistoryService(HistoryService historyService) {
         this.historyService = historyService;
     }
  
     public void setRepositoryService(RepositoryService repositoryService) {
         this.repositoryService = repositoryService;
     }
  
     public void setRuntimeService(RuntimeService runtimeService) {
         this.runtimeService = runtimeService;
     }
  
     public void setTaskService(TaskService taskService) {
         this.taskService = taskService;
     }
}
