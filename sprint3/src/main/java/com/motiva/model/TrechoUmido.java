package com.motiva.model;

public class TrechoUmido extends TrechoRodovia {

    public static final double TAXA_CRESCIMENTO_UMIDO = 5.0;

    public TrechoUmido(String nomeRodovia, int kmInicial, int kmFinal, double nivelVegetacao) {
        super(nomeRodovia, kmInicial, kmFinal, nivelVegetacao);
    }

    @Override
    protected double getTaxaCrescimentoDiaria() {
        return TAXA_CRESCIMENTO_UMIDO;
    }

    @Override
    public boolean isUmido() {
        return true;
    }
}
