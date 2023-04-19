package org.acme.utils;

import javax.inject.Singleton;

import org.acme.empregado.Empregado;

@Singleton
public class Database {
    
    public Empregado insertEmpregado(Empregado empregado) {

        return new Empregado(
           1,
           "",
           "",
           "",
           0
        );
    }
}
