package org.acme.login;

public class UserViewDTO {
    public String id;
    public String name;
    public String role;
    public Integer matricula;

    public UserViewDTO() {}

    public UserViewDTO(String id, String name, String role, Integer matricula) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.matricula = matricula;
    }
}
