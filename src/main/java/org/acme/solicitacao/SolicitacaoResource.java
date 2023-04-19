package org.acme.solicitacao;

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

import org.acme.exception.NotFound;
import org.acme.utils.HttpResponse;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/sisrh/rest")
public class SolicitacaoResource {
    
    @GET
    @Path("/solicitacao")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll() {
        List<SolicitacaoDoc> all = SolicitacaoDoc.listAll();
        List<Solicitacao> solicitacoes = new ArrayList<>();

        for (var doc : all) {
            var newSolicitacao = new Solicitacao();
            newSolicitacao.id = doc.id.toString();
            newSolicitacao.description = doc.description;
            newSolicitacao.requestAt = doc.requestAt;
            newSolicitacao.requestedBy = doc.requestedBy;
            newSolicitacao.status = doc.status;
            solicitacoes.add(newSolicitacao);
        }

        return Response
            .status(Status.CREATED)
            .entity(solicitacoes)
            .build();
    }

    @GET
    @Path("/solicitacao/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") String id) {
        Optional<SolicitacaoDoc> optional = SolicitacaoDoc.find("_id", new ObjectId(id))
            .firstResultOptional();
        
        if (optional.isEmpty()) {
            throw new NotFound("Solicitação não encontrada");
        }

        var doc = optional.get();

        // map from doc to entity
        var solicitacao = new Solicitacao();
        solicitacao.id = doc.id.toString();
        solicitacao.description = doc.description;
        solicitacao.requestAt = doc.requestAt;
        solicitacao.requestedBy = doc.requestedBy;
        solicitacao.status = doc.status;

        return Response.ok(solicitacao)
            .build();
    }

    @POST
    @Path("/solicitacao")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(SolicitacaoRequest request) {
        var solicitacao = Solicitacao.open(request.email, request.description);

        SolicitacaoDoc doc = new SolicitacaoDoc();
        doc.description = solicitacao.description;
        doc.requestAt = solicitacao.requestAt;
        doc.requestedBy = solicitacao.requestedBy;
        doc.status = solicitacao.status;
        doc.persist();
        
        // map from doc to entity
        var newSolicitacao = new Solicitacao();
        newSolicitacao.id = doc.id.toString();
        newSolicitacao.description = doc.description;
        newSolicitacao.requestAt = doc.requestAt;
        newSolicitacao.requestedBy = doc.requestedBy;
        newSolicitacao.status = doc.status;

        return Response
            .status(Status.CREATED)
            .entity(newSolicitacao)
            .build();
    }

    @PUT
    @Path("/solicitacao")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(SolicitacaoChangeRequest request) {
        Optional<SolicitacaoDoc> optional = SolicitacaoDoc.find("_id", new ObjectId(request.solicitacaoId))
                .firstResultOptional();
        if (optional.isEmpty()) {
            throw new NotFound("solicitacao não encontrada");
        }
    
        var solicitacao = optional.get();
        solicitacao.description = request.description;

        solicitacao.update();

        // map from doc to entity
        var dbSolicitacao = new Solicitacao();
        dbSolicitacao.description = solicitacao.description;
        dbSolicitacao.requestAt = solicitacao.requestAt;
        dbSolicitacao.requestedBy = solicitacao.requestedBy;
        dbSolicitacao.status = solicitacao.status;

        return Response
            .ok()
            .entity(dbSolicitacao)
            .build(); 
    }

    @DELETE
    @Path("/solicitacao/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") String solicitacaoId) {
        Optional<SolicitacaoDoc> optional = SolicitacaoDoc.find("_id", new ObjectId(solicitacaoId))
                .firstResultOptional();
        if (optional.isEmpty()) {
            throw new NotFound("solicitacao não encontrada");
        }
    
        var solicitacao = optional.get();
        solicitacao.delete();

        return Response.ok(new HttpResponse(
                String.format("solicitação %s removida com sucesso", solicitacaoId))
            ).build(); 
    }
}
