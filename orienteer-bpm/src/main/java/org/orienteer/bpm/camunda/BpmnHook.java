package org.orienteer.bpm.camunda;

import java.util.List;

import org.apache.wicket.util.string.Strings;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.deploy.DeploymentCache;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.orienteer.bpm.BPMModule;
import org.orienteer.bpm.camunda.handler.DeploymentEntityHandler;
import org.orienteer.bpm.camunda.handler.HandlersManager;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.ProcessDefinitionEntityHandler;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * Hook to handle BPMN specific entities 
 */
public class BpmnHook extends ODocumentHookAbstract {

	public BpmnHook() {
		setIncludeClasses(IEntityHandler.BPM_ENTITY_CLASS);
	}
	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.BOTH;
	}
	
	@Override
	public RESULT onRecordBeforeCreate(ODocument iDocument) {
		String id = iDocument.field("id");
		RESULT res = RESULT.RECORD_NOT_CHANGED;
		if(Strings.isEmpty(id)) {
			iDocument.field("id", getNextId());
			res = RESULT.RECORD_CHANGED;
		}
		if(iDocument.getSchemaClass().isSubClassOf(ProcessDefinitionEntityHandler.OCLASS_NAME)) {
			OIdentifiable deployment = iDocument.field("deployment");
			if(deployment==null) {
				deployment = getOrCreateDeployment();
				iDocument.field("deployment", deployment);
				res = RESULT.RECORD_CHANGED;
			}
		}
		return res;
	}
	
	/*@Override
	public void onRecordAfterCreate(ODocument iDocument) {
		if(iDocument.getSchemaClass().isSubClassOf(ProcessDefinitionEntityHandler.OCLASS_NAME)) {
			ProcessDefinitionEntityHandler handler = HandlersManager.get().getHandlerByClass(ProcessDefinitionEntityHandler.class);
			ProcessDefinitionEntity pd = handler.mapToEntity(iDocument, null, null);
			Context.getProcessEngineConfiguration().getDeploymentCache().addProcessDefinition(pd);
		}
	}*/
	
	/*@Override
	public void onRecordAfterUpdate(ODocument iDocument) {
		if(iDocument.getSchemaClass().isSubClassOf(ProcessDefinitionEntityHandler.OCLASS_NAME)) {
			DeploymentCache cache = Context.getProcessEngineConfiguration().getDeploymentCache();
			ProcessDefinitionEntityHandler handler = HandlersManager.get().getHandlerByClass(ProcessDefinitionEntityHandler.class);
			ProcessDefinitionEntity pd = handler.mapToEntity(iDocument, null, null);
			cache.removeProcessDefinition((String) iDocument.field("id"));
			cache.addProcessDefinition(pd);
		}
	}*/
	
	protected ODocument getOrCreateDeployment() {
		ODocument deployment = getDeployment();
		if(deployment==null) {
			deployment = new ODocument(DeploymentEntityHandler.OCLASS_NAME);
			deployment.field("id", getNextId());
			deployment.field("name", "Orienteer");
			deployment.save();
		}
		return deployment;
	}
	
	protected ODocument getDeployment() {
		List<ODocument> deployments = database.query(new OSQLSynchQuery<>("select from "+DeploymentEntityHandler.OCLASS_NAME, 1));
		return deployments==null || deployments.isEmpty()?null:deployments.get(0);
	}
	
	protected String getNextId() {
		return ((OProcessEngineConfiguration) BpmPlatform.getDefaultProcessEngine()
				.getProcessEngineConfiguration()).getIdGenerator().getNextId();
	}

}
