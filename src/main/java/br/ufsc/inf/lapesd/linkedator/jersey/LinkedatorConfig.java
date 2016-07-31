package br.ufsc.inf.lapesd.linkedator.jersey;

public class LinkedatorConfig {

    private String label;
    private String uriLinkedatorApi;
    private String microserviceDescriptionFile;
    private String microserviceUriBase;
    private int serverPort;
    private boolean verifyLinks;
    private boolean enableLinkedator;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

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

    public String getMicroserviceUriBase() {
        return microserviceUriBase;
    }

    public void setMicroserviceUriBase(String microserviceUriBase) {
        this.microserviceUriBase = microserviceUriBase;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isVerifyLinks() {
        return verifyLinks;
    }

    public void setVerifyLinks(boolean verifyLinks) {
        this.verifyLinks = verifyLinks;
    }

    public boolean isEnableLinkedator() {
        return enableLinkedator;
    }

    public void setEnableLinkedator(boolean enableLinkedator) {
        this.enableLinkedator = enableLinkedator;
    }

}
