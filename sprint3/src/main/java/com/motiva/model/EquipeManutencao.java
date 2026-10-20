package com.motiva.model;

// representa uma linha da tabela EquipeManutencao
public record EquipeManutencao(int id, String nome) {

    // equipe que ainda nao foi salva no banco (id 0)
    public EquipeManutencao(String nome) {
        this(0, nome);
    }

    public String getNome() {
        return nome;
    }
}
