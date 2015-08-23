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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceStub;

import javax.activation.DataHandler;
import java.rmi.RemoteException;

public class ResourceAdminServiceClient {

	private static final Log log = LogFactory.getLog(ResourceAdminServiceClient.class);
	private final String serviceName = "ResourceAdminService";
	private ResourceAdminServiceStub stub;
	private static final String serverURL = "https://localhost:9444/services/"; //Should be remote server url
	private String epr;
	private static final String MEDIA_TYPE_WSDL = "application/wsdl+xml";
	private static final String MEDIA_TYPE_WADL = "application/wadl+xml";
	private static final String MEDIA_TYPE_SCHEMA = "application/x-xsd+xml";
	private static final String MEDIA_TYPE_POLICY = "application/policy+xml";
	private static final String MEDIA_TYPE_GOVERNANCE_ARCHIVE = "application/vnd.wso2.governance-archive";

	public ResourceAdminServiceClient() throws RemoteException, LoginAuthenticationExceptionException {
		epr = serverURL + serviceName;
		stub = new ResourceAdminServiceStub(epr);
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

	public void addWSDLResource(String description, DataHandler dh)
			throws RemoteException,
			ResourceAdminServiceExceptionException {
		String fileName = dh.getName().substring(dh.getName().lastIndexOf("/") + 1);
		this.stub.addResource("/" + fileName, MEDIA_TYPE_WSDL, description, dh, null, null);
	}

	public void addTextResource(String parentPath, String fileName, String mediaType, String description, String content)
																throws RemoteException, ResourceAdminServiceExceptionException {
		this.stub.addTextResource(parentPath, fileName, mediaType, description, content);
	}
}
