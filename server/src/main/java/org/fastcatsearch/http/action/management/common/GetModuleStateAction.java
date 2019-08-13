package org.fastcatsearch.http.action.management.common;

import java.io.Writer;

import org.fastcatsearch.cluster.Node;
import org.fastcatsearch.cluster.NodeService;
import org.fastcatsearch.control.ResultFuture;
import org.fastcatsearch.http.ActionAuthority;
import org.fastcatsearch.http.ActionAuthorityLevel;
import org.fastcatsearch.http.ActionMapping;
import org.fastcatsearch.http.action.ActionRequest;
import org.fastcatsearch.http.action.ActionResponse;
import org.fastcatsearch.http.action.AuthAction;
import org.fastcatsearch.job.management.GetModuleStateJob;
import org.fastcatsearch.job.result.BasicStringResult;
import org.fastcatsearch.service.ServiceManager;
import org.fastcatsearch.util.JSONWrappedResultWriter;
import org.fastcatsearch.util.ResponseWriter;
import org.json.JSONObject;

@ActionMapping(value = "/management/common/modules-running-state", authority = ActionAuthority.Servers, authorityLevel = ActionAuthorityLevel.NONE)
public class GetModuleStateAction extends AuthAction {
	
	@Override
	public void doAuthAction(ActionRequest request, ActionResponse response) throws Exception {
		
		String nodeId = request.getParameter("nodeId");
		
		Writer writer = response.getWriter();
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		NodeService nodeService = ServiceManager.getInstance().getService(NodeService.class);
		Node node = nodeService.getNodeById(nodeId);
		GetModuleStateJob job = new GetModuleStateJob();
		ResultFuture resultFuture = nodeService.sendRequest(node, job);
		
		if(resultFuture!=null) {
		
			Object obj = resultFuture.take();
			
			if(obj instanceof BasicStringResult) {
				
				BasicStringResult result = (BasicStringResult) obj;
				
				String resultStr = result.getResult();
				
				JSONObject root = new JSONObject(resultStr);
				
				JSONWrappedResultWriter wrapper = new JSONWrappedResultWriter(root, responseWriter);
				
				wrapper.wrap();
			}
		}
		responseWriter.done();
	}
}
