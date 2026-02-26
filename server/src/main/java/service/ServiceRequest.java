package service;

public abstract class ServiceRequest {
    private String jsonData;

    public ServiceRequest(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getJson() { return jsonData; }
}
