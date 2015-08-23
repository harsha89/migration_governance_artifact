/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.carbon.greg.artifacts.migration.client;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.governance.generic.stub.ManageGenericArtifactServiceRegistryExceptionException;
import org.wso2.carbon.governance.generic.stub.ManageGenericArtifactServiceStub;
import org.wso2.carbon.governance.generic.stub.beans.xsd.ArtifactBean;
import org.wso2.carbon.governance.generic.stub.beans.xsd.ArtifactsBean;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.pagination.PaginationContext;
import org.wso2.carbon.registry.core.pagination.PaginationUtils;
import org.wso2.carbon.registry.ws.client.registry.WSRegistryServiceClient;

import java.io.File;
import java.rmi.RemoteException;

public class GovernanceArtifactCheckinClient {
	private static final String serverURL = "https://localhost:9444/services/";
	private ManageGenericArtifactServiceStub stub;
	private String epr;

	public GovernanceArtifactCheckinClient()
			throws RemoteException, LoginAuthenticationExceptionException {
		epr = serverURL + "ManageGenericArtifactService";
		stub = new ManageGenericArtifactServiceStub(epr);
		//Authenticate Your stub from sessionCooke
		ServiceClient serviceClient;
		Options option;
		LoginAdminServiceClient adminServiceClient = new LoginAdminServiceClient(serverURL);
		String sessionCookie = adminServiceClient.authenticate("admin", "admin");
		serviceClient = stub._getServiceClient();
		option = serviceClient.getOptions();
		option.setManageSession(true);
		option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, sessionCookie);
	}

	public ArtifactBean[] listArtifacts(String key) throws RemoteException {
		ArtifactsBean artifactsBean;
		try {
			PaginationUtils.copyPaginationContext(stub._getServiceClient());
			artifactsBean = stub.listArtifacts(key, null);
		} finally {
			PaginationContext.destroy();
		}
		return artifactsBean.getArtifacts();
	}

	public String addArtifact(String key, String info)
			throws RemoteException, ManageGenericArtifactServiceRegistryExceptionException {
		return stub.addArtifact(key, info, null);
	}
}
