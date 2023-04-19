package org.acme.solicitacao;

import java.time.LocalDateTime;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "solicitacoes")
public class SolicitacaoDoc extends PanacheMongoEntity {
    public String status;
    public String requestedBy;
    public LocalDateTime requestAt;
    public String description;
}
