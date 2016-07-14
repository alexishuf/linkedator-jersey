package br.ufsc.inf.lapesd.linkedator.jersey;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
public class LinkedadorWriterInterceptor implements WriterInterceptor {
    
    private LinkedadorApi linkedatorApi = new LinkedadorApi();

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        Object entity = context.getEntity();
        String representationWithLinks = linkedatorApi.createLinks(entity.toString());
        context.setEntity(representationWithLinks);
        context.proceed();
    }
}