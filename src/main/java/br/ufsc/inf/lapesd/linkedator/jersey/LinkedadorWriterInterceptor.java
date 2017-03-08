package br.ufsc.inf.lapesd.linkedator.jersey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
        Stopwatch time = Stopwatch.createStarted();
        Object entity = context.getEntity();
        List<String> linkedatorOptions = (List<String>) context.getProperty("linkedatorOptions");

        if (linkedatorOptions != null && linkedatorOptions.contains("linkVerify")) {
            context.setEntity(entity);
            context.proceed();
            return;
        }

        if (entity != null) {
            String representationWithLinks = linkedatorApi.createLinks(entity.toString());

            String configFile = new String(Files.readAllBytes(Paths.get("linkedator.config")));
            LinkedatorConfig linkedatorConfig = new Gson().fromJson(configFile, LinkedatorConfig.class);
            if (linkedatorConfig.isEnableLinkedator()) {
                JsonElement parseRepresentation = timeStamp(time, representationWithLinks);
                context.setEntity(parseRepresentation.toString());
            } else {
                context.setEntity(representationWithLinks);
            }
        }
        context.proceed();
    }

    private JsonElement timeStamp(Stopwatch time, String representationWithLinks) {
        long elapsed = time.elapsed(TimeUnit.MICROSECONDS);
        double processingTime = elapsed / 1000.0;

        JsonElement parseRepresentation = new JsonParser().parse(representationWithLinks);
        if (parseRepresentation.isJsonArray()) {
            JsonObject timeObject = new JsonObject();
            timeObject.addProperty("linkedatorJerseyTime", processingTime);
            parseRepresentation.getAsJsonArray().add(timeObject);
        } else {
            parseRepresentation.getAsJsonObject().addProperty("linkedatorJerseyTime", processingTime);
        }
        return parseRepresentation;
    }

}