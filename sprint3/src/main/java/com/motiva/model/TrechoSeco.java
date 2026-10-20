package com.motiva.model;

public class TrechoSeco extends TrechoRodovia {

    public static final double TAXA_CRESCIMENTO_SECO = 2.0;

    public TrechoSeco(String nomeRodovia, int kmInicial, int kmFinal, double nivelVegetacao) {
        super(nomeRodovia, kmInicial, kmFinal, nivelVegetacao);
    }

    @Override
    protected double getTaxaCrescimentoDiaria() {
        return TAXA_CRESCIMENTO_SECO;
    }

    @Override
    public boolean isUmido() {
        return false;
    }
}
