package br.ufsc.inf.lapesd.linkedator.jersey;

public class LinkedatorConfig {

    private String label;
    private String uriLinkedatorApi;
    private String microserviceDescriptionFile;

    public String getUriLinkedatorApi() {
        return uriLinkedatorApi;
    }

    public void setUriLinkedatorApi(String uriLinkedatorApi) {
        this.uriLinkedatorApi = uriLinkedatorApi;
    }

    public String getMicroserviceDescriptionFile() {
        return microserviceDescriptionFile;
    }

    public void setMicroserviceDescriptionFile(String microserviceDescriptionFile) {
        this.microserviceDescriptionFile = microserviceDescriptionFile;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
