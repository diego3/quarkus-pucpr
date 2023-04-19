package org.acme.empregado;

import java.time.LocalDate;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "empregados")
public class EmpregadoDoc extends PanacheMongoEntity {
    public Integer matricula;
    public String nome;
    public LocalDate admissao;
    public LocalDate desligamento;
    public Integer salario;
}
