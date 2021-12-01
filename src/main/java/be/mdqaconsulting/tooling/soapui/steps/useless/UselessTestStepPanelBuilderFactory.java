package be.mdqaconsulting.tooling.soapui.steps.useless;

import javax.swing.JPanel;

import com.eviware.soapui.impl.EmptyPanelBuilder;
import com.eviware.soapui.model.PanelBuilder;
import com.eviware.soapui.model.util.PanelBuilderFactory;
import com.eviware.soapui.support.components.JPropertiesTable;

/**
 * Creates the DesktopPanel - could be extended to also support the bottom left Properties tab
 */

public class UselessTestStepPanelBuilderFactory implements PanelBuilderFactory<UselessTestRequestStep>
{
	@Override
	public PanelBuilder<UselessTestRequestStep> createPanelBuilder()
	{
		return new UselessTestRequestPanelBuilder();
	}

	@Override
	public Class<UselessTestRequestStep> getTargetModelItem()
	{
		return UselessTestRequestStep.class;
	}

	public static class UselessTestRequestPanelBuilder extends EmptyPanelBuilder<UselessTestRequestStep>
	{
		public UselessTestRequestPanelBuilder() {
			
		}
		
		public UselessTestRequestStepDesktopPanel buildDesktopPanel( UselessTestRequestStep testStep )
		{
			return new UselessTestRequestStepDesktopPanel( testStep );
		}

		@Override
		public boolean hasDesktopPanel()
		{
			return true;
		}
		
	    public JPanel buildOverviewPanel(UselessTestRequestStep testStep) {
	        UselessTestRequestInterface request = testStep.getTestRequest();
	        JPropertiesTable<UselessTestRequestInterface> table = new JPropertiesTable<UselessTestRequestInterface>(
	                "HTTP TestRequest Properties");

	        // basic properties
	        table.addProperty("Name", "name", true);
	        table.addProperty("Description", "description", true);
	        // table.addProperty( "Message Size", "contentLength", false );
	        table.addProperty("Encoding", "encoding", new String[]{null, "UTF-8", "iso-8859-1"});

			/*
	         * if( request.getOperation() != null ) table.addProperty( "Endpoint",
			 * "endpoint", request.getInterface().getEndpoints() );
			 */

	        table.addProperty("Endpoint", "endpoint", true);
	        table.addProperty("Timeout", "timeout", true);

	        table.addProperty("Bind Address", "bindAddress", true);
	        table.addProperty("Follow Redirects", "followRedirects", JPropertiesTable.BOOLEAN_OPTIONS);

			/*
			 * if( request.getOperation() != null ) { table.addProperty( "Service",
			 * "service" ); table.addProperty( "Resource", "path" ); }
			 */

	        // security / authentication
	        /*
	        table.addProperty("Username", "username", true);
	        table.addPropertyShadow("Password", "password", true);
	        table.addProperty("Domain", "domain", true);
	        table.addProperty("Authentication Type", "authType", new String[]{AuthType.GLOBAL_HTTP_SETTINGS.toString(),
	                AuthType.PREEMPTIVE.toString(), AuthType.SPNEGO_KERBEROS.toString(), AuthType.NTLM.toString()});

	        StringList keystores = new StringList(((WsdlProject) request.getTestStep().getTestCase().getTestSuite()
	                .getProject()).getWssContainer().getCryptoNames());
	        keystores.add("");
	        table.addProperty("SSL Keystore", "sslKeystore", keystores.toStringArray());
            */
	        
	        table.addProperty("Strip whitespaces", "stripWhitespaces", JPropertiesTable.BOOLEAN_OPTIONS);
	        table.addProperty("Remove Empty Content", "removeEmptyContent", JPropertiesTable.BOOLEAN_OPTIONS);
	        table.addProperty("Entitize Properties", "entitizeProperties", JPropertiesTable.BOOLEAN_OPTIONS);
	        table.addProperty("Multi-Value Delimiter", "multiValueDelimiter", true);

	        // post-processing
	        table.addProperty("Pretty Print", "prettyPrint", JPropertiesTable.BOOLEAN_OPTIONS);
	        table.addProperty("Dump File", "dumpFile", true).setDescription("Dumps response message to specified file");
	        table.addProperty("Max Size", "maxSize", true).setDescription("The maximum number of bytes to receive");
	        table.addProperty("Discard Response", "discardResponse", JPropertiesTable.BOOLEAN_OPTIONS);

	        table.addProperty("Send Empty Parameters", "sendEmptyParameters", JPropertiesTable.BOOLEAN_OPTIONS);
	        table.setPropertyObject(request);

	        return table;
	    }

	    public boolean hasOverviewPanel() {
	        return true;
	    }
	}
}
