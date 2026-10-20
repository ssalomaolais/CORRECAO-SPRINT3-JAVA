package com.motiva.model;

// trecho seco com sensor instalado
public class TrechoSecoInteligente extends TrechoSeco implements MonitoravelViaIoT {

    public TrechoSecoInteligente(String nomeRodovia, int kmInicial, int kmFinal, double nivelVegetacao) {
        super(nomeRodovia, kmInicial, kmFinal, nivelVegetacao);
    }

    @Override
    public double transmitirDadosSensor() {
        System.out.println("[SENSOR IoT] Lendo dados do trecho " + getIdentificacao() + "...");
        return getNivelVegetacao();
    }
}
