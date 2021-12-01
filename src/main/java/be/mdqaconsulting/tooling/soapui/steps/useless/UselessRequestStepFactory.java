package be.mdqaconsulting.tooling.soapui.steps.useless;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.eviware.soapui.config.HttpRequestConfig;
import com.eviware.soapui.config.RestParameterConfig;
import com.eviware.soapui.config.RestParametersConfig;
import com.eviware.soapui.config.TestStepConfig;
import com.eviware.soapui.impl.rest.actions.support.NewRestResourceActionBase;
import com.eviware.soapui.impl.rest.panels.resource.RestParamsTable;
import com.eviware.soapui.impl.rest.support.RestUtils;
import com.eviware.soapui.impl.rest.support.XmlBeansRestParamsTestPropertyHolder;
import com.eviware.soapui.impl.support.HttpUtils;
import com.eviware.soapui.impl.wsdl.monitor.WsdlMonitorMessageExchange;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.teststeps.registry.WsdlTestStepFactory;

/**
 * The actual factory class that creates new EMailTestSteps from scratch or an XMLBeans config.
 */

public class UselessRequestStepFactory extends WsdlTestStepFactory
{
	private static final String USELESS_STEP_ID = "useless";
	private static final String USELESSREQUEST_TYPE = USELESS_STEP_ID;
    private XmlBeansRestParamsTestPropertyHolder params;
    private RestParamsTable paramsTable;

	public UselessRequestStepFactory()
	{
		super( USELESS_STEP_ID, "Useless TestStep", "Sends an Useless request to some library that returns a consumable result that makes sense to use in the context of tests in SoapUI.", "/report.png" );
	}

	public UselessTestRequestStep buildTestStep( WsdlTestCase testCase, TestStepConfig config, boolean forLoadTest )
	{
		return new UselessTestRequestStep( testCase, config, forLoadTest );
	}

	public TestStepConfig createNewTestStep( WsdlTestCase testCase, String name )
	{
		params = new XmlBeansRestParamsTestPropertyHolder(testCase, RestParametersConfig.Factory.newInstance());
		paramsTable = new RestParamsTable(params, false, NewRestResourceActionBase.ParamLocation.RESOURCE, true, false);
		
        try {
                HttpRequestConfig httpRequest = HttpRequestConfig.Factory.newInstance();
                httpRequest.setEndpoint("");
                httpRequest.setMethod("POST");
                XmlBeansRestParamsTestPropertyHolder tempParams = new XmlBeansRestParamsTestPropertyHolder(testCase,
                        httpRequest.addNewParameters());
                tempParams.addParameters(params);
                tempParams.release();

                TestStepConfig testStep = TestStepConfig.Factory.newInstance();
                testStep.setType(USELESSREQUEST_TYPE);
                testStep.setConfig(httpRequest);
                testStep.setName(name);

                return testStep;
        } finally {
            paramsTable.release();
            paramsTable = null;
            params = null;
        }
	}
	
    public TestStepConfig createNewTestStep(WsdlTestCase testCase, String name, String endpoint, String method) {
        RestParametersConfig restParamConf = RestParametersConfig.Factory.newInstance();
        params = new XmlBeansRestParamsTestPropertyHolder(testCase, restParamConf);

        HttpRequestConfig httpRequest = HttpRequestConfig.Factory.newInstance();
        httpRequest.setMethod(method);

        endpoint = RestUtils.extractParams(endpoint, params, true);

        XmlBeansRestParamsTestPropertyHolder tempParams = new XmlBeansRestParamsTestPropertyHolder(testCase,
                httpRequest.addNewParameters());
        tempParams.addParameters(params);

        httpRequest.setEndpoint(HttpUtils.completeUrlWithHttpIfProtocolIsNotHttpOrHttpsOrPropertyExpansion(endpoint));

        TestStepConfig testStep = TestStepConfig.Factory.newInstance();
        testStep.setType(USELESSREQUEST_TYPE);
        testStep.setConfig(httpRequest);
        testStep.setName(name);

        return testStep;
    }

	public boolean canCreate()
	{
		return true;
	}
	
    public TestStepConfig createConfig(WsdlMonitorMessageExchange me, String stepName) {
        HttpRequestConfig testRequestConfig = HttpRequestConfig.Factory.newInstance();

        testRequestConfig.setName(stepName);
        testRequestConfig.setEncoding("UTF-8");
        testRequestConfig.setEndpoint(me.getEndpoint());
        testRequestConfig.setMethod(me.getRequestMethod());

        // set parameters
        RestParametersConfig parametersConfig = testRequestConfig.addNewParameters();
        Map<String, String> parametersMap = me.getHttpRequestParameters();
        List<RestParameterConfig> parameterConfigList = new ArrayList<RestParameterConfig>();
        for (String name : parametersMap.keySet()) {
            RestParameterConfig parameterConf = RestParameterConfig.Factory.newInstance();
            parameterConf.setName(name);
            parameterConf.setValue(parametersMap.get(name));
            parameterConfigList.add(parameterConf);
        }
        parametersConfig.setParameterArray(parameterConfigList.toArray(new RestParameterConfig[parametersMap.size()]));
        testRequestConfig.setParameters(parametersConfig);

        // String requestContent = me.getRequestContent();
        // testRequestConfig.addNewRequest().setStringValue( requestContent );

        TestStepConfig testStep = TestStepConfig.Factory.newInstance();
        testStep.setType(USELESSREQUEST_TYPE);
        testStep.setConfig(testRequestConfig);
        testStep.setName(stepName);
        return testStep;
    }
}
