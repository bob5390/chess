package service;

import dataaccess.ServerData;
import kotlin.NotImplementedError;

public abstract class ServiceResult {
    private ServerData data;

    public ServiceResult(ServerData data) {
        this.data = data;
    }

    public String toJson() {
        throw new NotImplementedError();
    }
}
