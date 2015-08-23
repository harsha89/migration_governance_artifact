Sample Artifact Migration Clinet

In this migration client, it has written only to migrate single governance artifact from one server to other. 


GovernanceArtifactCheckinClient contains the remote server properties that governance artifacts that should migrate. 
Server url will need to be changed appropriately
	private static final String serverURL = "https://localhost:9444/services/";

And valid admin user should authenticate agains remote server to send artifacts
	String sessionCookie = adminServiceClient.authenticate("admin", "admin");

ResourceAdminServiceClient contains the properties of remote server where it used to add registry resources.

Server url will need to be changed appropriately
private static final String serverURL = "https://localhost:9444/services/";

And valid admin user should authenticate agains remote server to send artifacts
	String sessionCookie = adminServiceClient.authenticate("admin", "admin");

ArtifactMigrationManager class does the migration. The current artifact key has set to fmrservice. If you need please 
change it
	private final String artifactKey = "fmrservice";

After configuring above values. Execute mvn clean install

Goto the target folder and copy org.wso2.carbon.greg.artifacts.migrate.client-1.0.0.jar to the repository/components/dropins 
directory

Then execute ./wso2server.sh -Dmigrate=true 
