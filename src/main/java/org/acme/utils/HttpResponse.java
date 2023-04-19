package org.acme.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HttpResponse {
    @JsonProperty("mensagem")
    public String message;

    public HttpResponse(String message) {
        this.message = message;
    }
}
