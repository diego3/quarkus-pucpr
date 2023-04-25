package org.acme.login;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.acme.empregado.EmpregadoDoc;
import org.acme.exception.NotFound;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/sisrh/rest/user")
public class UserResource {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(CreateUserDTO userDTO) {
        Optional<EmpregadoDoc> optional = EmpregadoDoc.find("matricula", userDTO.matricula)
            .firstResultOptional();

        if (optional.isEmpty()) {
            throw new NotFound("Matricula inválida");
        }

        UserDoc doc = new UserDoc();
        doc.name = userDTO.name;
        doc.password = userDTO.password;
        doc.role = userDTO.role;
        doc.matricula = userDTO.matricula;
        doc.persist();

        return Response.status(Status.CREATED)
            .entity(doc)
            .build();
    }

    @GET
    @Path("/matricula/{matricula}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getByMatricula(@PathParam("matricula") Integer matricula) {
        Optional<UserDoc> optional = UserDoc.find("matricula", matricula)
            .firstResultOptional();

        if (optional.isEmpty()) {
            throw new NotFound("Usuário não encontrado");
        }

        return Response.status(Status.OK)
            .entity(optional.get())
            .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response findAll() {
        List<UserDoc> users = UserDoc.listAll();

        var view = new ArrayList<UserViewDTO>();

        for (var user : users) {
            view.add(new UserViewDTO(
                user.id.toString(),
                user.name, 
                user.role, 
                user.matricula));
        }

        return Response.status(Status.OK)
            .entity(view)
            .build();
    }

    @PUT
    @Path("/matricula/{matricula}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateByMatricula(@PathParam("matricula") Integer matricula, UpdateUserDTO updateUserDTO) {
        Optional<UserDoc> optional = UserDoc.find("matricula", matricula)
            .firstResultOptional();

        if (optional.isEmpty()) {
            throw new NotFound("Usuário não encontrado");
        }

        var user = optional.get();
        user.name = updateUserDTO.name;
        user.password = updateUserDTO.password;
        user.role = updateUserDTO.role;
        user.update();

        return Response.status(Status.OK)
            .entity(user)
            .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteByUserId(@PathParam("id") String userId) {
        Optional<UserDoc> optional = UserDoc.find("_id", new ObjectId(userId))
            .firstResultOptional();

        if (optional.isEmpty()) {
            throw new NotFound("Usuário não encontrado");
        }

        optional.get().delete();

        return Response.status(Status.OK)
            .entity("Usuário apagado com sucesso")
            .build();
    }
}
