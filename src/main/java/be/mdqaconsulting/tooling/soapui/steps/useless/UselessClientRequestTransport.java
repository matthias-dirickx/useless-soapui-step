package be.mdqaconsulting.tooling.soapui.steps.useless;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.support.AbstractHttpRequestInterface;
import com.eviware.soapui.impl.support.http.HttpRequestInterface;
import com.eviware.soapui.impl.wsdl.AbstractWsdlModelItem;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.submit.RequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.EndpointRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.EndpointStrategyRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.GlobalHttpHeadersRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.HttpCompressionRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.HttpPackagingResponseFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.HttpSettingsRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.PostPackagingRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.PropertyExpansionRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.RemoveEmptyContentRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.RestRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.StripWhitespacesRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.WsdlPackagingRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.WsrmRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.WssRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.transports.http.BaseHttpRequestTransport;
import com.eviware.soapui.impl.wsdl.submit.transports.http.ExtendedHttpMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.SinglePartHttpResponse;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.attachments.MimeMessageResponse;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedCopyMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedDeleteMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedGetMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedHeadMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedLockMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedOptionsMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedPatchMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedPostMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedPropFindMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedPurgeMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedPutMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedTraceMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedUnlockMethod;
import com.eviware.soapui.impl.wsdl.support.PathUtils;
import com.eviware.soapui.impl.wsdl.support.http.HeaderRequestInterceptor;
import com.eviware.soapui.impl.wsdl.support.http.HttpClientSupport;
import com.eviware.soapui.impl.wsdl.support.http.SoapUIHttpRoute;
import com.eviware.soapui.impl.wsdl.support.wss.WssCrypto;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.iface.SubmitContext;
import com.eviware.soapui.model.propertyexpansion.PropertyExpander;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.model.support.ModelSupport;
import com.eviware.soapui.settings.HttpSettings;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.soapui.support.types.StringToStringsMap;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

import javax.annotation.CheckForNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * HTTP transport that uses HttpClient to send/receive SOAP messages
 *
 * @author Ole.Matzura
 */

@SuppressWarnings("deprecation")
public class UselessClientRequestTransport implements BaseHttpRequestTransport {
    private List<RequestFilter> filters = new ArrayList<RequestFilter>();

    public UselessClientRequestTransport() {
    	/*
    	 * We're not passing through the usual mill because of the 
    	 * patchwork-customization attempts. For that reason I add
    	 * them statically in the class instantiation.
    	 */
        addRequestFilter(new EndpointRequestFilter());
        addRequestFilter(new HttpSettingsRequestFilter());
        addRequestFilter(new RestRequestFilter());
        addRequestFilter(new PropertyExpansionRequestFilter());
        addRequestFilter(new RemoveEmptyContentRequestFilter());
        addRequestFilter(new StripWhitespacesRequestFilter());
        addRequestFilter(new EndpointStrategyRequestFilter());
        addRequestFilter(new WsrmRequestFilter());
        addRequestFilter(new WssRequestFilter());
        addRequestFilter(new GlobalHttpHeadersRequestFilter());

        addRequestFilter(new WsdlPackagingRequestFilter());
        addRequestFilter(new HttpCompressionRequestFilter());
        addRequestFilter(new HttpPackagingResponseFilter());
        addRequestFilter(new PostPackagingRequestFilter());
    }

    public void addRequestFilter(RequestFilter filter) {
        filters.add(filter);
    }

    public void removeRequestFilter(RequestFilter filterToRemove) {
        if (!filters.remove(filterToRemove)) {
            for (RequestFilter requestFilter : filters) {
                if (requestFilter.getClass().equals(filterToRemove.getClass())) {
                    filters.remove(requestFilter);
                    break;
                }
            }
        }
    }

    @Override
    public void insertRequestFilter(RequestFilter filter, RequestFilter refFilter) {
        int ix = filters.indexOf( refFilter );
        if( ix == -1 )
            filters.add( filter );
        else
            filters.add( ix, filter );
    }

    public <T> void removeRequestFilter(Class<T> filterClass) {
        RequestFilter filter = findFilterByType(filterClass);

        if (filter != null) {
            removeRequestFilter(filter);
        }
    }

    public <T> void replaceRequestFilter(Class<T> filterClass, RequestFilter newFilter) {
        RequestFilter filter = findFilterByType(filterClass);

        if (filter != null) {
            for (int i = 0; i < filters.size(); i++) {
                RequestFilter oldFilter = filters.get(i);
                if (oldFilter == filter) {
                    filters.remove(i);
                    filters.add(i, newFilter);
                    break;
                }
            }
        }
    }

    @CheckForNull
    public <T extends Object> RequestFilter findFilterByType(Class<T> filterType) {
        for (int i = 0; i < filters.size(); i++) {
            RequestFilter filter = filters.get(i);
            if (filter.getClass() == filterType) {
                return filter;
            }
        }
        return null;
    }

    public void abortRequest(SubmitContext submitContext) {
        HttpRequestBase postMethod = (HttpRequestBase) submitContext.getProperty(HTTP_METHOD);
        if (postMethod != null) {
            postMethod.abort();
        }
    }

    public Response sendRequest(SubmitContext submitContext, Request request) throws Exception {

        AbstractHttpRequestInterface<?> httpRequest = (AbstractHttpRequestInterface<?>) request;
        ExtendedHttpMethod httpMethod = createHttpMethod(httpRequest);
        
        boolean createdContext = false;
        HttpContext httpContext = (HttpContext) submitContext.getProperty(SubmitContext.HTTP_STATE_PROPERTY);
        if (httpContext == null) {
            httpContext = HttpClientSupport.createEmptyContext();
            submitContext.setProperty(SubmitContext.HTTP_STATE_PROPERTY, httpContext);
            createdContext = true;
        }
        
        String localAddress = System.getProperty("soapui.bind.address", httpRequest.getBindAddress());
        if (localAddress == null || localAddress.trim().length() == 0) {
            localAddress = SoapUI.getSettings().getString(HttpSettings.BIND_ADDRESS, null);
        }

        @SuppressWarnings("unused")
		org.apache.http.HttpResponse httpResponse;
        if (localAddress != null && localAddress.trim().length() > 0) {
            try {
                httpMethod.getParams().setParameter(ConnRoutePNames.LOCAL_ADDRESS, InetAddress.getByName(localAddress));
            } catch (Exception e) {
                SoapUI.logError(e, "Failed to set localAddress to [" + localAddress + "]");
            }
        }

        submitContext.removeProperty(RESPONSE);
        submitContext.setProperty(HTTP_METHOD, httpMethod);
        submitContext.setProperty(POST_METHOD, httpMethod);
        submitContext.setProperty(HTTP_CLIENT, null);
        submitContext.setProperty(REQUEST_CONTENT, httpRequest.getRequestContent());
        submitContext.setProperty(WSDL_REQUEST, httpRequest);
        submitContext.setProperty(RESPONSE_PROPERTIES, new StringToStringMap());

        filterRequest(submitContext, httpRequest);

        BasicHttpEntity requestEntity = new BasicHttpEntity();
        /*
        requestEntity.setContent(IOUtils.toInputStream(request.getRequestContent(), request.getEncoding()));
        requestEntity.setContentEncoding(request.getEncoding());
        requestEntity.setContentLength(request.getRequestContent().getBytes().length);
        requestEntity.setContentType("text/plain");
        */
        requestEntity.setContent(IOUtils.toInputStream((String)submitContext.getProperty(REQUEST_CONTENT)));
        requestEntity.setContentEncoding(request.getEncoding());
        requestEntity.setContentLength(((String)submitContext.getProperty(REQUEST_CONTENT)).getBytes().length);
        requestEntity.setContentType("text/plain");
        
        httpContext.setAttribute(HttpCoreContext.HTTP_REQUEST, requestEntity);
        
        try {
            Settings settings = httpRequest.getSettings();

            // custom http headers last so they can be overridden
            StringToStringsMap headers = httpRequest.getRequestHeaders();

            // clear headers specified in GUI, and re-add them, with property expansion
            for (String headerName : headers.keySet()) {
                String expandedHeaderName = PropertyExpander.expandProperties(submitContext, headerName);
                httpMethod.removeHeaders(expandedHeaderName);
                for (String headerValue : headers.get(headerName)) {
                    headerValue = PropertyExpander.expandProperties(submitContext, headerValue);
                    httpMethod.addHeader(expandedHeaderName, headerValue);
                }
            }

            // do request
            WsdlProject project = (WsdlProject) ModelSupport.getModelItemProject(httpRequest);
            WssCrypto crypto = null;
            if (project != null && project.getWssContainer() != null) {
                crypto = project.getWssContainer().getCryptoByName(
                        PropertyExpander.expandProperties(submitContext, httpRequest.getSslKeystore()));
            }

            if (crypto != null && WssCrypto.STATUS_OK.equals(crypto.getStatus())) {
                httpMethod.getParams().setParameter(SoapUIHttpRoute.SOAPUI_SSL_CONFIG,
                        crypto.getSource() + " " + crypto.getPassword());
            }

            // dump file?
            httpMethod.setDumpFile(PathUtils.expandPath(httpRequest.getDumpFile(),
                    (AbstractWsdlModelItem<?>) httpRequest, submitContext));

            // include request time?
            if (settings.getBoolean(HttpSettings.INCLUDE_REQUEST_IN_TIME_TAKEN)) {
                httpMethod.initStartTime();
            }
            if (httpMethod.getMetrics() != null) {
                httpMethod.getMetrics().setHttpMethod(httpMethod.getMethod());
                
                captureMetrics(httpMethod, null);
                httpMethod.getMetrics().getTotalTimer().start();
            }

            // submit!
            httpResponse = submitRequest(httpMethod, httpContext);

            // save request headers captured by interceptor
            saveRequestHeaders(httpMethod, httpContext);

            if (httpMethod.getMetrics() != null) {
                httpMethod.getMetrics().getReadTimer().stop();
                httpMethod.getMetrics().getTotalTimer().stop();
            }
            
        } catch (Throwable t) {
            httpMethod.setFailed(t);

            if (t instanceof Exception) {
                throw (Exception) t;
            }

            SoapUI.logError(t);
            throw new Exception(t);
        } finally {
            if (!httpMethod.isFailed()) {
                if (httpMethod.getMetrics() != null) {
                    if (httpMethod.getMetrics().getReadTimer().getStop() == 0) {
                        httpMethod.getMetrics().getReadTimer().stop();
                    }
                    if (httpMethod.getMetrics().getTotalTimer().getStop() == 0) {
                        httpMethod.getMetrics().getTotalTimer().stop();
                    }
                }
            } else {
                httpMethod.getMetrics().reset();
                httpMethod.getMetrics().setTimestamp(System.currentTimeMillis());
                captureMetrics(httpMethod, null);
            }

            for (int c = filters.size() - 1; c >= 0; c--) {
                RequestFilter filter = filters.get(c);
                filter.afterRequest(submitContext, httpRequest);
            }

            if (!submitContext.hasProperty(RESPONSE)) {
                createDefaultResponse(submitContext, httpRequest, httpMethod);
            }

            Response response = (Response) submitContext.getProperty(BaseHttpRequestTransport.RESPONSE);
            StringToStringMap responseProperties = (StringToStringMap) submitContext
                    .getProperty(BaseHttpRequestTransport.RESPONSE_PROPERTIES);

            for (String key : responseProperties.keySet()) {
                response.setProperty(key, responseProperties.get(key));
            }

            if (createdContext) {
                submitContext.setProperty(SubmitContext.HTTP_STATE_PROPERTY, null);
            }
        }
        return (Response) submitContext.getProperty(BaseHttpRequestTransport.RESPONSE);
        
    }

    protected org.apache.http.HttpResponse submitRequest(ExtendedHttpMethod httpMethod, HttpContext httpContext)
    		throws UnsupportedOperationException, IOException {
        return UselessClientSupport.execute(httpMethod, httpContext);
    }

    protected HttpClientSupport.SoapUIHttpClient getSoapUIHttpClient() {
        return HttpClientSupport.getHttpClient();
    }
    
    private void filterRequest(SubmitContext submitContext, AbstractHttpRequestInterface<?> httpRequest) {
		for(RequestFilter filter: filters) {
			filter.filterRequest(submitContext, httpRequest);
		}
	}

    private void createDefaultResponse(
    		SubmitContext submitContext,
    		AbstractHttpRequestInterface<?> httpRequest,
    		ExtendedHttpMethod httpMethod)
    {
        String requestContent = (String) submitContext.getProperty(BaseHttpRequestTransport.REQUEST_CONTENT);

        // check content-type for multipart
        String responseContentTypeHeader = null;
        if (httpMethod.hasHttpResponse() && httpMethod.getHttpResponse().getEntity() != null) {
            Header h = httpMethod.getHttpResponse().getEntity().getContentType();
            responseContentTypeHeader = h.toString();
        }

        Response response;
        if (responseContentTypeHeader != null && responseContentTypeHeader.toUpperCase().startsWith("MULTIPART")) {
            response = new MimeMessageResponse(httpRequest, httpMethod, requestContent, submitContext);
        } else {
            response = new SinglePartHttpResponse(httpRequest, httpMethod, requestContent, submitContext);
        }

        submitContext.setProperty(BaseHttpRequestTransport.RESPONSE, response);
    }

    @SuppressWarnings("incomplete-switch")
	private ExtendedHttpMethod createHttpMethod(AbstractHttpRequestInterface<?> httpRequest) {
        if (httpRequest instanceof HttpRequestInterface<?>) {
            HttpRequestInterface<?> restRequest = (HttpRequestInterface<?>) httpRequest;
            switch (restRequest.getMethod()) {
                case GET:
                    return new ExtendedGetMethod();
                case HEAD:
                    return new ExtendedHeadMethod();
                case DELETE:
                    return new ExtendedDeleteMethod();
                case PUT:
                    return new ExtendedPutMethod();
                case OPTIONS:
                    return new ExtendedOptionsMethod();
                case TRACE:
                    return new ExtendedTraceMethod();
                case PATCH:
                    return new ExtendedPatchMethod();
                case PROPFIND:
                    return new ExtendedPropFindMethod();
                case LOCK:
                    return new ExtendedLockMethod();
                case UNLOCK:
                    return new ExtendedUnlockMethod();
                case COPY:
                    return new ExtendedCopyMethod();
                case PURGE:
                    return new ExtendedPurgeMethod();

            }
        }

        ExtendedPostMethod extendedPostMethod = new ExtendedPostMethod();

        extendedPostMethod.setAfterRequestInjection(httpRequest.getAfterRequestInjection());
        return extendedPostMethod;
    }

    private void captureMetrics(ExtendedHttpMethod httpMethod, HttpClient httpClient) {
        try {
        	httpMethod.getMetrics().setIpAddress(InetAddress.getByName(httpMethod.getURI().getHost()).getHostAddress());
            httpMethod.getMetrics().setPort(
                    httpMethod.getURI().getPort(),
                    0);
        } catch (UnknownHostException uhe) {
            /* ignore */
        } catch (IllegalStateException ise) {
            /* ignore */
        }
    }

	@SuppressWarnings("unchecked")
	private void saveRequestHeaders(ExtendedHttpMethod httpMethod, HttpContext httpContext) {
		List<Header> requestHeaders = (List<Header>) httpContext
                .getAttribute(HeaderRequestInterceptor.SOAPUI_REQUEST_HEADERS);

        if (requestHeaders != null) {
            for (Header header : requestHeaders) {
                Header[] existingHeaders = httpMethod.getHeaders(header.getName());

                int c = 0;
                for (; c < existingHeaders.length; c++) {
                    if (existingHeaders[c].getValue().equals(header.getValue())) {
                        break;
                    }
                }

                if (c == existingHeaders.length) {
                    httpMethod.addHeader(header);
                }
            }
        }
    }

}

