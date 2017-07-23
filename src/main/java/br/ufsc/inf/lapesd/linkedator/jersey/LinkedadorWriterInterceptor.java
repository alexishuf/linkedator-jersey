package br.ufsc.inf.lapesd.linkedator.jersey;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class LinkedadorWriterInterceptor implements WriterInterceptor, ContainerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(LinkedadorWriterInterceptor.class);
    private static LinkedadorApi globalLinkedatorApi = null;
    private LinkedadorApi linkedatorApi = null;

    @Nonnull
    public static synchronized LinkedadorApi getGlobalLinkedatorApi() {
        if (globalLinkedatorApi == null) {
            globalLinkedatorApi = new LinkedadorApi();
            try {
                globalLinkedatorApi.loadConfig();
            } catch (IOException ignored) {
                logger.warn("Failed to load LinkedatorConfig from disk. Configure it manually.");
                //keep disabled configuration
            }
        }
        return globalLinkedatorApi;
    }

    public LinkedadorWriterInterceptor() {
        linkedatorApi = getGlobalLinkedatorApi();
    }

    public LinkedadorWriterInterceptor(LinkedadorApi linkedatorApi) {
        this.linkedatorApi = linkedatorApi;
    }

    @Nonnull
    public LinkedadorApi getLinkedatorApi() {
        return linkedatorApi;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MultivaluedMap<String, String> queryParameters = requestContext.getUriInfo().getQueryParameters();
        List<String> linkedatorOptions = queryParameters.get("linkedatorOptions");
        requestContext.setProperty("linkedatorOptions", linkedatorOptions);
    }


    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        Stopwatch watch = Stopwatch.createStarted();
        Object entity = context.getEntity();
        MediaType mediaType = context.getMediaType();
        @SuppressWarnings("unchecked")
        List<String> linkedatorOptions = (List<String>) context.getProperty("linkedatorOptions");

        if (linkedatorOptions != null && linkedatorOptions.contains("linkVerify")) {
            context.proceed();
            return;
        }

        if (entity != null && getLinkedatorApi().getConfig().isEnableLinkedator()) {
            entity = getLinkedatorApi().createLinks(entity, mediaType);
            context.setEntity(entity);
            context.getHeaders().putSingle("X-Linkedator-Jersey-Time",
                    watch.elapsed(TimeUnit.MILLISECONDS));
        }
        context.proceed();
    }
}