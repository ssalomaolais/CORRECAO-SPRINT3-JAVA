package com.motiva.model;

public class Pulverizacao extends IntervencaoOperacional {

    @Override
    public void executarServico(TrechoRodovia trecho) {
        System.out.println("[PULVERIZAÇÃO] Aplicando herbicida no trecho: " + trecho.getIdentificacao());
    }
}
