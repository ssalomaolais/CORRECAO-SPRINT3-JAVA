package com.motiva.model;

public class RocadaMecanizada extends IntervencaoOperacional {

    @Override
    public void executarServico(TrechoRodovia trecho) {
        System.out.println("[ROÇADA MECANIZADA] Trator enviado para: " + trecho.getIdentificacao());
    }
}
