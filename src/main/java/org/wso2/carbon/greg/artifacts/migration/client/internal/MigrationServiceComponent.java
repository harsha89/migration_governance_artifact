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

package org.wso2.carbon.greg.artifacts.migration.client.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.greg.artifacts.migration.client.ArtifactMigrationManager;
import org.wso2.carbon.greg.artifacts.migration.client.MigrationException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.service.TenantRegistryLoader;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="org.wso2.carbon.greg.artifacts.migration.client" immediate="true"
 * @scr.reference name="realm.service"
 * interface="org.wso2.carbon.user.core.service.RealmService" cardinality="1..1"
 * policy="dynamic" bind="setRealmService" unbind="unsetRealmService"
 * @scr.reference name="registry.service"
 * interface="org.wso2.carbon.registry.core.service.RegistryService" cardinality="1..1"
 * policy="dynamic" bind="setRegistryService" unbind="unsetRegistryService"
 * @scr.reference name="registry.core.dscomponent"
 * interface="org.wso2.carbon.registry.core.service.RegistryService" cardinality="1..1"
 * policy="dynamic" bind="setRegistryService" unbind="unsetRegistryService"
 * @scr.reference name="tenant.registryloader" interface="org.wso2.carbon.registry.core.service.TenantRegistryLoader"
 * cardinality="1..1"
 * policy="dynamic" bind="setTenantRegistryLoader" unbind="unsetTenantRegistryLoader"
 */

@SuppressWarnings("unused")
public class MigrationServiceComponent {

	private static final Log log = LogFactory.getLog(MigrationServiceComponent.class);

	/**
	 * Method to activate bundle.
	 *
	 * @param context OSGi component context.
	 */
	protected void activate(ComponentContext context) {
		boolean isRegistryMigration = Boolean.parseBoolean(System.getProperty("migrate"));
		try {
			//Only performs registry migration
			if (isRegistryMigration) {
				log.info("Migrating has started");
				ArtifactMigrationManager artifactMigrationManager = new ArtifactMigrationManager();
				artifactMigrationManager.registryResourceMigration();
			}
		} catch (MigrationException e) {
			log.info("Error occurred during the migration");
		}
	}

	/**
	 * Method to deactivate bundle.
	 *
	 * @param context OSGi component context.
	 */
	protected void deactivate(ComponentContext context) {
		log.info("WSO2 API Manager migration bundle is deactivated");
	}

	/**
	 * Method to set registry service.
	 *
	 * @param registryService service to get tenant data.
	 */
	protected void setRegistryService(RegistryService registryService) {
		if (log.isDebugEnabled()) {
			log.debug("Setting RegistryService for WSO2 API Manager migration");
		}
		ServiceHolder.setRegistryService(registryService);
	}

	/**
	 * Method to unset registry service.
	 *
	 * @param registryService service to get registry data.
	 */
	protected void unsetRegistryService(RegistryService registryService) {
		if (log.isDebugEnabled()) {
			log.debug("Unset Registry service");
		}
		ServiceHolder.setRegistryService(null);
	}

	/**
	 * Method to set realm service.
	 *
	 * @param realmService service to get tenant data.
	 */
	protected void setRealmService(RealmService realmService) {
		log.debug("Setting RealmService for WSO2 API Manager migration");
		ServiceHolder.setRealmService(realmService);
	}

	/**
	 * Method to unset realm service.
	 *
	 * @param realmService service to get tenant data.
	 */
	protected void unsetRealmService(RealmService realmService) {
		if (log.isDebugEnabled()) {
			log.debug("Unset Realm service");
		}
		ServiceHolder.setRealmService(null);
	}

	/**
	 * Method to set tenant registry loader
	 *
	 * @param tenantRegLoader tenant registry loader
	 */
	protected void setTenantRegistryLoader(TenantRegistryLoader tenantRegLoader) {
		log.debug("Setting TenantRegistryLoader for WSO2 API Manager migration");
		ServiceHolder.setTenantRegLoader(tenantRegLoader);
	}

	/**
	 * Method to unset tenant registry loader
	 *
	 * @param tenantRegLoader tenant registry loader
	 */
	protected void unsetTenantRegistryLoader(TenantRegistryLoader tenantRegLoader) {
		log.debug("Unset Tenant Registry Loader");
		ServiceHolder.setTenantRegLoader(null);
	}

}
