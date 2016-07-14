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

public class LinkedadorApi {
    
    public void registryMicroservice() throws IOException {
        String configFile = new String(Files.readAllBytes(Paths.get("linkedator.config")));
        LinkedatorConfig linkedatorConfig = new Gson().fromJson(configFile, LinkedatorConfig.class);
        System.out.println("Microservice: " + linkedatorConfig.getLabel());
        String microserviceDescription = new String(Files.readAllBytes(Paths.get(linkedatorConfig.getMicroserviceDescriptionFile())));

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(linkedatorConfig.getUriLinkedatorApi()).path("microservice/description");

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        try {
            Response response = invocationBuilder.post(Entity.entity(microserviceDescription, MediaType.APPLICATION_JSON));

            int status = response.getStatus();
            if (status == 200) {
                System.out.println("Microservice registry successful");
            }
        } catch (Exception e) {
            System.out.println("Microservice not registered");

        }

    }

    public String createLinks(String representation) throws IOException {
        String configFile = new String(Files.readAllBytes(Paths.get("linkedator.config")));
        LinkedatorConfig linkedatorConfig = new Gson().fromJson(configFile, LinkedatorConfig.class);
        
        String responseRepresentation = representation;
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(linkedatorConfig.getUriLinkedatorApi()).path("createLinks");

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
