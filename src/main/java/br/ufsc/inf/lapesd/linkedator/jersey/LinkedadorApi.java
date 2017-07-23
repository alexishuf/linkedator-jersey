package br.ufsc.inf.lapesd.linkedator.jersey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import br.ufsc.inf.lapesd.linkedator.SemanticMicroserviceDescription;

public class LinkedadorApi {
    private LinkedatorConfig linkedatorConfig;
    private final ClientBuilder builder = ClientBuilder.newBuilder();

    public LinkedadorApi()  {
        linkedatorConfig = new LinkedatorConfig();
        linkedatorConfig.setEnableLinkedator(false);
    }


    public LinkedatorConfig getConfig() {
        return linkedatorConfig;
    }

    public LinkedadorApi setConfig(LinkedatorConfig config) {
        this.linkedatorConfig = config;
        return this;
    }

    public LinkedadorApi loadConfig() throws IOException {
        String configFile = new String(Files.readAllBytes(Paths.get("linkedator.config")));
        return setConfig(new Gson().fromJson(configFile, LinkedatorConfig.class));
    }

    public ClientBuilder getClientBuilder() {
        return builder;
    }

    public void registerMicroservice() throws IOException {
        if (!linkedatorConfig.isEnableLinkedator()) {
            System.out.println("Microservice NOT registred - Likedator not enabled");
            return;
        }

        System.out.println("Microservice: " + linkedatorConfig.getLabel());
        String microserviceDescriptionJson = new String(Files.readAllBytes(Paths.get(linkedatorConfig.getMicroserviceDescriptionFile())));
        SemanticMicroserviceDescription microserviceDescription = new Gson().fromJson(microserviceDescriptionJson, SemanticMicroserviceDescription.class);

        String ontology = new String(Files.readAllBytes(Paths.get(linkedatorConfig.getOntologyFile())));
        String ontologyBase64 = Base64.getEncoder().encodeToString(ontology.getBytes());
        microserviceDescription.setOntologyBase64(ontologyBase64);

        microserviceDescription.setServerPort(String.valueOf(linkedatorConfig.getServerPort()));
        microserviceDescription.setUriBase(linkedatorConfig.getMicroserviceUriBase());

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(linkedatorConfig.getUriLinkedatorApi()).path("microservice/description");

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        try {
            String stringDescription = new Gson().toJson(microserviceDescription);
            Response response = invocationBuilder.post(Entity.entity(stringDescription, MediaType.APPLICATION_JSON));

            int status = response.getStatus();
            if (status == 200) {
                System.out.println("Microservice registry successful");
            } else if (status == 404) {
                System.out.println("Microservice NOT registred - HTTP 404");
            }
        } catch (Exception e) {
            System.out.println("Microservice not registered");

        }

    }

    public Object createLinks(Object entity, MediaType mediaType) throws IOException {
        if (!linkedatorConfig.isEnableLinkedator()) {
            return entity;
        }

        Client client = getClientBuilder().build();
        Invocation.Builder builder = client.target(linkedatorConfig.getUriLinkedatorApi())
                .path("createLinks")
                .queryParam("verifyLinks", linkedatorConfig.isVerifyLinks())
                .request(mediaType);

        try {
            Response response = builder.post(Entity.entity(entity,mediaType));

            int status = response.getStatus();
            if (status == 200)
                entity = response.readEntity(entity.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return entity;
    }

}
