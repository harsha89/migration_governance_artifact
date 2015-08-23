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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.governance.generic.stub.ManageGenericArtifactServiceRegistryExceptionException;
import org.wso2.carbon.greg.artifacts.migration.client.internal.ServiceHolder;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.user.api.UserStoreException;

import java.rmi.RemoteException;

@SuppressWarnings("unchecked")
public class ArtifactMigrationManager implements MigrationClient {

	private static final Log log = LogFactory.getLog(ArtifactMigrationManager.class);
	private final String artifactKey = "fmrservice";

	/**
	 * This method is used to migrate governance artifacts
	 *
	 * @throws MigrationException
	 */
	@Override
	public void registryResourceMigration() throws MigrationException {
		migrateArtifacts();
	}

	/**
	 * This method is used to migrate governance artifacts from one server to another
	 * @throws MigrationException
	 */
	void migrateArtifacts() throws MigrationException {
		log.info("Rxt migration for API Manager 1.9.0 started.");
		boolean isTenantFlowStarted = false;
		try {
			PrivilegedCarbonContext.startTenantFlow();
			isTenantFlowStarted = true;
			PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain("carbon.super", true);
			PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(-1234, true);
			String adminName = ServiceHolder.getRealmService().getTenantUserRealm(-1234).getRealmConfiguration()
					.getAdminUserName();
			log.debug("Tenant admin username : " + adminName);
			ServiceHolder.getTenantRegLoader().loadTenantRegistry(-1234);
			Registry registry = ServiceHolder.getRegistryService().getGovernanceUserRegistry(adminName, -1234);
			GenericArtifactManager artifactManager;
			GovernanceUtils.loadGovernanceArtifacts((UserRegistry) registry);
			artifactManager = new GenericArtifactManager(registry, artifactKey);
			GovernanceUtils.loadGovernanceArtifacts((UserRegistry) registry);
			GenericArtifact[] artifacts = artifactManager.getAllGenericArtifacts();
			GovernanceArtifactCheckinClient artifactCheckinClient = new GovernanceArtifactCheckinClient();
			ResourceAdminServiceClient resourceAdminServiceClient = new ResourceAdminServiceClient();
			Resource resource;
			Resource wsdlResource;
			String wsdlPath;
			//Get all available services
			for (GenericArtifact artifact : artifacts) {
				log.info("Migrating resource in " + artifact.getPath());
				resource = registry.get(artifact.getPath());
				//Check saved wsdl already exists, if so we need to add wsdl first to the correct location
				wsdlPath = artifact.getAttribute("interface_wsdlURL");
				if(wsdlPath != null && wsdlPath.contains("/_system/governance/trunk/wsdls")) {
					wsdlPath = wsdlPath.split("_system/governance/")[1];
					String fileName = wsdlPath.substring(wsdlPath.lastIndexOf("/") + 1);
					wsdlResource = registry.get(wsdlPath);
					resourceAdminServiceClient.addTextResource(wsdlPath, fileName, "application/wsdl+xml",
							"Test Description", new String((byte[]) wsdlResource.getContent()));
				}
				//Adding artifact to remote client
				artifactCheckinClient.addArtifact(artifactKey, new String((byte[])resource.getContent()));
			}
		} catch (RemoteException e) {
			handleException("Error occurred while file operations stub", e);
		} catch (RegistryException e) {
			handleException("Error occurred while accessing the registry", e);
		} catch (LoginAuthenticationExceptionException e) {
			handleException("Error occurred while authenticate with remote service", e);
		} catch (UserStoreException e) {
			handleException("Error occurred while user store", e);
		} catch (ManageGenericArtifactServiceRegistryExceptionException e) {
			handleException("Error occurred while accessing stub", e);
		} catch (ResourceAdminServiceExceptionException e) {
			handleException("Error occurred while adding wsdl", e);
		} finally {
			if (isTenantFlowStarted) {
				PrivilegedCarbonContext.endTenantFlow();
			}
		}
		log.info("Rxt resource migration done for all the tenants");
	}

	public void handleException(String msg, Exception e) throws MigrationException {
		log.error(msg, e);
		throw new MigrationException("Error occurred while accessing the registry", e);
	}
}
