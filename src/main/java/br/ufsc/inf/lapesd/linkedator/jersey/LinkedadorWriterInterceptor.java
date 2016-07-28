package br.ufsc.inf.lapesd.linkedator.jersey;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
public class LinkedadorWriterInterceptor implements WriterInterceptor, ContainerRequestFilter {
    
    private LinkedadorApi linkedatorApi = new LinkedadorApi();

    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MultivaluedMap<String, String> queryParameters = requestContext.getUriInfo().getQueryParameters();
        List<String> linkedatorOptions = queryParameters.get("linkedatorOptions");
        requestContext.setProperty("linkedatorOptions", linkedatorOptions);
    }
    
    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        Object entity = context.getEntity();
        List<String> linkedatorOptions = (List<String>) context.getProperty("linkedatorOptions");
        if(linkedatorOptions != null && linkedatorOptions.contains("linkVerify")){
            return;
        }
                
        if(entity != null){
            String representationWithLinks = linkedatorApi.createLinks(entity.toString());
            context.setEntity(representationWithLinks);
        }
        context.proceed();
        
    }
    
}