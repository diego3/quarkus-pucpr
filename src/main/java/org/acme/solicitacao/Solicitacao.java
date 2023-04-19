package org.acme.solicitacao;

import java.time.LocalDateTime;

public class Solicitacao {
    public String id;
    public String status;
    public String requestedBy;
    public LocalDateTime requestAt;
    public String description;

    public Solicitacao() {}

    public static Solicitacao open(String email, String description) {
        var request = new Solicitacao();
        request.requestAt = LocalDateTime.now();
        request.status = "OPEN";
        request.requestedBy = email;
        request.description = description;
        return request;
    }
}
