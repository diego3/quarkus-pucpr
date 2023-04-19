package org.acme.empregado;

public class Empregado {
    public Integer matricula;
    public String nome;
    public String admissao;
    public String desligamento;
    public Integer salario;

    public Empregado() {}

    public Empregado(Integer matricula, String nome, String admissao, String desligamento, Integer salario) {
        this.matricula = matricula;
        this.nome = nome;
        this.admissao = admissao;
        this.desligamento = desligamento;
        this.salario = salario;
    }
}
