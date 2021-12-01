package be.mdqaconsulting.tooling.soapui.steps.useless;

import com.eviware.soapui.config.HttpRequestConfig;
import com.eviware.soapui.impl.wsdl.teststeps.HttpTestRequestInterface;
import com.eviware.soapui.impl.wsdl.teststeps.TestRequest;

public interface UselessTestRequestInterface extends TestRequest, HttpTestRequestInterface<HttpRequestConfig> {
    public static final String RESPONSE_PROPERTY = UselessTestRequestInterface.class.getName() + "@response";
    public static final String STATUS_PROPERTY = UselessTestRequestInterface.class.getName() + "@status";
    @Override
    public UselessTestRequestStep getTestStep();
}
