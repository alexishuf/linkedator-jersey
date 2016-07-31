package br.ufsc.inf.lapesd.linkedator.jersey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    public void registryMicroservice() throws IOException {
        String configFile = new String(Files.readAllBytes(Paths.get("linkedator.config")));
        LinkedatorConfig linkedatorConfig = new Gson().fromJson(configFile, LinkedatorConfig.class);

        if (!linkedatorConfig.isEnableLinkedator()) {
            System.out.println("Microservice NOT registred - Likedator not enabled");
            return;
        }

        System.out.println("Microservice: " + linkedatorConfig.getLabel());
        String microserviceDescriptionJson = new String(Files.readAllBytes(Paths.get(linkedatorConfig.getMicroserviceDescriptionFile())));
        SemanticMicroserviceDescription microserviceDescription = new Gson().fromJson(microserviceDescriptionJson, SemanticMicroserviceDescription.class);

        microserviceDescription.setServerPort(String.valueOf(linkedatorConfig.getServerPort()));
        microserviceDescription.setUriBase(linkedatorConfig.getMicroserviceUriBase());

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(linkedatorConfig.getUriLinkedatorApi()).path("microservice/description");

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        try {
            String stringDescription = microserviceDescription.toString();
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

    public String createLinks(String representation) throws IOException {
        String configFile = new String(Files.readAllBytes(Paths.get("linkedator.config")));
        LinkedatorConfig linkedatorConfig = new Gson().fromJson(configFile, LinkedatorConfig.class);

        if (!linkedatorConfig.isEnableLinkedator()) {
            return representation;
        }

        String responseRepresentation = representation;
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(linkedatorConfig.getUriLinkedatorApi()).path("createLinks").queryParam("verifyLinks", linkedatorConfig.isVerifyLinks());

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        try {
            Response response = invocationBuilder.post(Entity.entity(representation, MediaType.APPLICATION_JSON));

            int status = response.getStatus();
            if (status == 200) {
                responseRepresentation = response.readEntity(String.class);
            }
        } catch (Exception e) {
            responseRepresentation = representation;
        }

        return responseRepresentation;
    }

}
