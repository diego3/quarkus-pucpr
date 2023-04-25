package org.acme.empregado;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
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

import org.acme.exception.BadRequest;
import org.acme.exception.NotFound;
import org.acme.utils.Database;
import org.acme.utils.HttpResponse;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.aayushatharva.brotli4j.common.annotations.Local;
import com.github.javafaker.Faker;

@Path("/sisrh/rest")
public class EmpregadoResource {
    @Inject
    Database database;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/empregado")
    public List<Empregado> listAll() {
        var all = new ArrayList<Empregado>();

        List<EmpregadoDoc> findAll = EmpregadoDoc.listAll();
        for (var employee : findAll) {
            all.add(new Empregado(
                employee.matricula,
                employee.nome,
                employee.admissao.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")),
                employee.desligamento != null ? employee.desligamento.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")) : "",
                employee.salario
            ));
        }
        return all;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/empregado/ativos")
    public List<Empregado> listAllActive() {
        var all = new ArrayList<Empregado>();

        List<EmpregadoDoc> findAll = EmpregadoDoc.list("desligamento is null");

        for (var employee : findAll) {
            all.add(new Empregado(
                employee.matricula,
                employee.nome,
                employee.admissao.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")),
                employee.desligamento != null ? employee.desligamento.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")) : "",
                employee.salario
            ));
        }
        return all;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/empregado/inativos")
    public List<Empregado> listAllInactives() {
        var all = new ArrayList<Empregado>();

        List<EmpregadoDoc> findAll = EmpregadoDoc.list("desligamento is not null");

        for (var employee : findAll) {
            all.add(new Empregado(
                employee.matricula,
                employee.nome,
                employee.admissao.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")),
                employee.desligamento != null ? employee.desligamento.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")) : "",
                employee.salario
            ));
        }
        return all;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/empregado/{matricula}")
    public Response listByMatricula(@PathParam("matricula") Integer matricula) {
        Optional<EmpregadoDoc> optional = EmpregadoDoc.find("matricula", matricula)
            .firstResultOptional();

        if (optional.isEmpty()) {
            throw new NotFound("Empregado não encontrado");
        }
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var employee = optional.get();
        var empregado = new Empregado(
            employee.matricula,
            employee.nome,
            employee.admissao.format(formatter),
            employee.desligamento != null ? employee.desligamento.format(formatter) : "",
            employee.salario
        );
            
        return Response.ok(empregado).build();
    }

    @POST
    @Path("/empregado")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Empregado empregado) {
        EmpregadoDoc first = EmpregadoDoc.find("matricula", empregado.matricula).firstResult();
        if (first != null && first.id != null && ObjectId.isValid(first.id.toString())) {
            throw new BadRequest("já existe um empregado com a matricula informada");
        }

        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        EmpregadoDoc newDoc = new EmpregadoDoc();
        newDoc.matricula = empregado.matricula;
        newDoc.admissao = LocalDate.parse(empregado.admissao, formatter);
        if (empregado.desligamento != null && empregado.desligamento.isEmpty()) {
            newDoc.desligamento = LocalDate.parse(empregado.desligamento, formatter);
        }
        newDoc.salario = empregado.salario;
        newDoc.persist();

        return Response.status(Status.CREATED)
            .entity(new Empregado(
                newDoc.matricula,
                newDoc.nome,
                newDoc.admissao.format(formatter),
                newDoc.desligamento != null ? newDoc.desligamento.format(formatter) : "",
                newDoc.salario
            ))
            .build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/empregado/{matricula}")
    public Response update(@PathParam("matricula") Integer matricula, Empregado empregado) {
        Optional<EmpregadoDoc> optional = EmpregadoDoc.find("matricula", matricula).firstResultOptional();
        if (optional.isEmpty()) {
            throw new NotFound("empregado não encontrado");
        }
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var empregadoDoc = optional.get();
        if (empregado.admissao != null) {
            empregadoDoc.admissao = LocalDate.parse(empregado.admissao, formatter);
        }
        if (empregado.desligamento != null) {
            empregadoDoc.desligamento = LocalDate.parse(empregado.desligamento, formatter);
        }
        empregadoDoc.salario = empregado.salario;

        empregadoDoc.update();

        var updated = new Empregado();
        updated.matricula = matricula;
        updated.admissao = empregadoDoc.admissao.format(formatter);
        if (empregadoDoc.desligamento != null) {
            updated.desligamento = empregadoDoc.desligamento.format(formatter);
        }
        updated.salario = empregadoDoc.salario;
        return Response.ok().entity(updated).build();
    }

    @DELETE
    @Path("/empregado/{matricula}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("matricula") Integer matricula) {
        Optional<EmpregadoDoc> optional = EmpregadoDoc.find("matricula", matricula)
            .firstResultOptional();
        if (optional.isEmpty()) {
            throw new NotFound("matricula não encontrada");
        }

        EmpregadoDoc empregado = optional.get();
        
        empregado.delete();

        return Response.ok()
            .entity(new HttpResponse("Matricula removida com sucesso"))
            .build();
    }

    @POST
    @Path("/empregado/seed")
    @Produces(MediaType.APPLICATION_JSON)
    public void seed() {
        var formatter2 = new SimpleDateFormat("YYYY-MM-dd");
        
        for(var i = 515455; i < 515455 + 10; i++) {
            EmpregadoDoc newDoc = new EmpregadoDoc();
            newDoc.matricula = i;
            newDoc.admissao = LocalDate.parse(
                formatter2.format(Faker.instance().date().birthday()), 
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            newDoc.salario = Faker.instance().random().nextInt(1300, 10000);
            newDoc.desligamento = LocalDate.now().minusDays(Faker.instance().random().nextInt(50, 200));

            newDoc.persist();
        }
    }
}
