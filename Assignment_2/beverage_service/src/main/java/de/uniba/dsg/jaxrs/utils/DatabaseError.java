package de.uniba.dsg.jaxrs.utils;

public class DatabaseError {
    private final int statusCode;
    private final String statusText;

    public DatabaseError(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }
}
