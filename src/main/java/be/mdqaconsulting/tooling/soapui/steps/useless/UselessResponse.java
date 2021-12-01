package be.mdqaconsulting.tooling.soapui.steps.useless;

import java.io.UnsupportedEncodingException;

import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.util.BaseResponse;

public class UselessResponse extends BaseResponse {
	
	private byte[] responseBytes;

	public UselessResponse(Request request, String responseContent, String responseContentType) {
		super(request, responseContent, responseContentType);
		responseBytes = responseContent.getBytes();
	}
	
	public UselessResponse(Request request, byte[] responseContent, String responseContentType) throws UnsupportedEncodingException {
		super(request, new String(responseContent, "UTF-8"), responseContentType);
		responseBytes = responseContent;
	}
	
	@Override
	public byte[] getRawResponseData() {
		return responseBytes;
	}
}
