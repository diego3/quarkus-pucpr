package org.acme.login;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.acme.exception.Forbidden;
import org.acme.exception.NotFound;

@Path("/sisrh/rest")
public class LoginResource {

    @POST
    @Path("/loginunico/jwt")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Login login) {
        Optional<UserDoc> optional = UserDoc.find("name", login.name)
            .firstResultOptional();
        if (optional.isEmpty()) {
            throw new NotFound("Usuário não encontrado");
        }
        
        UserDoc user = optional.get();
        if (!user.password.equals(login.password)) {
            throw new Forbidden("credenciais inválidas");
        }

        var token = Login.createToken(login.name, login.password, user.role);
        if ("".equals(token) || token == null) {
            throw new Forbidden("falhou ao tentar criar token");
        }

        return Response.status(Status.OK)
            .entity(token)
            .build();
    }

    
}
