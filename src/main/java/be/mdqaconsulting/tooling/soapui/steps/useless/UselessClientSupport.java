package be.mdqaconsulting.tooling.soapui.steps.useless;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

import com.eviware.soapui.impl.wsdl.submit.transports.http.ExtendedHttpMethod;

public class UselessClientSupport {
    
    public static HttpResponse execute(ExtendedHttpMethod httpMethod, HttpContext context)
    		throws UnsupportedOperationException, IOException {
    	String ip = httpMethod.getURI().getHost();
    	String port = Integer.toString(httpMethod.getURI().getPort());
    	
    	UselessDummyClient uc = new UselessDummyClient(ip, port);
    	
    	StringWriter sw = new StringWriter();
    	IOUtils.copy(
    			((HttpEntity)(context.getAttribute(HttpCoreContext.HTTP_REQUEST))).getContent(),
    			sw,
    			((HttpEntity)(context.getAttribute(HttpCoreContext.HTTP_REQUEST))).getContentEncoding().getValue());
    	String msg = sw.toString();
    	/*
    	 * Due to implementation choice this needs to be split out.
    	 * The format it generates is String.format("%s %s%s", cmd, key, msg).
    	 * It suffices to split the command off. The key can be in the message
    	 * because it has no expected whitespaces that are added.
    	 * Penality for not respecting this: null message returned
    	 * which is ill-handled by the implementation.
    	 */
    	uc.executeRaw(msg);
    	
    	//Get a default response (anything except the entity)
    	HttpResponse httpResponse = getDefaultResponse(uc);
    	//Add the entity
    	httpResponse.setEntity(getResponseEntity(uc));
    	//Add it to the method
    	//This needs to be done to support further SoapUI native processing.
    	httpMethod.setHttpResponse(httpResponse);
    	context.setAttribute(HttpCoreContext.HTTP_RESPONSE, httpResponse);
    	return httpResponse;
    }
    
    private static HttpEntity getResponseEntity(UselessDummyClient uc) {
    	BasicHttpEntity httpEntity = new BasicHttpEntity();
    	httpEntity.setContentEncoding("UTF-8");
    	httpEntity.setContentType("application/xml");
    	httpEntity.setContent(new ByteArrayInputStream(uc.getRawResponse()));
    	httpEntity.setContentLength(uc.getRawResponse().length);
    	return httpEntity;
    }
    
    private static HttpResponse getDefaultResponse(UselessDummyClient uc) {
    	int responseCode = uc.isCompleted() ? 200 : 400;
    	String reasonPhrase = uc.isCompleted() ? "OK" : "Useless Dummy Client Dummy Error - Bad Request as proxy status";
    	BasicHttpResponse httpResponse = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 0), responseCode, reasonPhrase);
    	return httpResponse;
    }
}
