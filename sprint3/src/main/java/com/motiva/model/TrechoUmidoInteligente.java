package com.motiva.model;

// trecho umido com sensor instalado
public class TrechoUmidoInteligente extends TrechoUmido implements MonitoravelViaIoT {

    public TrechoUmidoInteligente(String nomeRodovia, int kmInicial, int kmFinal, double nivelVegetacao) {
        super(nomeRodovia, kmInicial, kmFinal, nivelVegetacao);
    }

    @Override
    public double transmitirDadosSensor() {
        System.out.println("[SENSOR IoT] Lendo dados do trecho " + getIdentificacao() + "...");
        return getNivelVegetacao();
    }
}
