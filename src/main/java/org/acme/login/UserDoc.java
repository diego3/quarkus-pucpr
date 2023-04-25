package org.acme.login;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "users")
public class UserDoc extends PanacheMongoEntity {
    public String name;
    public String password;
    public String role;
    public Integer matricula;
}
