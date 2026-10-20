package com.motiva.model;

public abstract class TrechoRodovia {

    // limites do nivel de vegetacao
    public static final double LIMITE_CRITICO = 30.0;
    public static final double LIMITE_ATENCAO = 15.0;

    private int id;
    protected String nomeRodovia;
    protected int quilometroInicial;
    protected int quilometroFinal;

    private double nivelVegetacao;
    private EquipeManutencao equipeResponsavel;

    protected TrechoRodovia(String nomeRodovia, int kmInicial, int kmFinal, double nivelVegetacao) {
        validarNomeRodovia(nomeRodovia);
        validarQuilometragem(kmInicial, kmFinal);
        validarNivelVegetacao(nivelVegetacao);

        this.nomeRodovia = nomeRodovia;
        this.quilometroInicial = kmInicial;
        this.quilometroFinal = kmFinal;
        this.nivelVegetacao = nivelVegetacao;
    }

    // cada tipo de trecho define o seu proprio ritmo de crescimento
    protected abstract double getTaxaCrescimentoDiaria();

    public abstract boolean isUmido();

    private void validarNomeRodovia(String nomeRodovia) {
        if (nomeRodovia == null || nomeRodovia.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da rodovia não pode ser vazio.");
        }
    }

    private void validarQuilometragem(int kmInicial, int kmFinal) {
        if (kmInicial >= kmFinal) {
            throw new IllegalArgumentException("O KM inicial deve ser menor que o KM final.");
        }
    }

    private void validarNivelVegetacao(double nivelVegetacao) {
        if (nivelVegetacao < 0) {
            throw new IllegalArgumentException("O nível de vegetação não pode ser negativo.");
        }
    }

    public void registrarCrescimento(double taxa) {
        validarTaxaCrescimento(taxa);
        this.nivelVegetacao += taxa;
    }

    private void validarTaxaCrescimento(double taxa) {
        if (taxa < 0) {
            throw new IllegalArgumentException("A taxa de crescimento não pode ser negativa.");
        }
    }

    public void simularCrescimentoDias(int dias) {
        registrarCrescimento(getTaxaCrescimentoDiaria() * dias);
    }

    public boolean isCritico() {
        return this.nivelVegetacao >= LIMITE_CRITICO;
    }

    public boolean isAtencao() {
        return this.nivelVegetacao >= LIMITE_ATENCAO && !isCritico();
    }

    public void alocarEquipe(EquipeManutencao equipe) {
        if (isCritico()) {
            this.equipeResponsavel = equipe;
            System.out.println("[ALOCAÇÃO] Equipe " + equipe.getNome() + " alocada ao trecho " + this.nomeRodovia);
            return;
        }

        System.out.println("[RECUSADO] O trecho " + this.nomeRodovia + " não está crítico. Equipe não alocada.");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeRodovia() {
        return nomeRodovia;
    }

    public int getQuilometroInicial() {
        return quilometroInicial;
    }

    public int getQuilometroFinal() {
        return quilometroFinal;
    }

    public double getNivelVegetacao() {
        return nivelVegetacao;
    }

    public void setNivelVegetacao(double nivelVegetacao) {
        validarNivelVegetacao(nivelVegetacao);
        this.nivelVegetacao = nivelVegetacao;
    }

    public EquipeManutencao getEquipeResponsavel() {
        return equipeResponsavel;
    }

    // usado pelo DAO ao ler do banco (nao passa pela regra do alocarEquipe)
    public void setEquipeResponsavel(EquipeManutencao equipeResponsavel) {
        this.equipeResponsavel = equipeResponsavel;
    }

    public String getIdentificacao() {
        return nomeRodovia + " (KM " + quilometroInicial + " ao " + quilometroFinal + ")";
    }

    @Override
    public String toString() {
        String equipe = (equipeResponsavel == null) ? "sem equipe" : equipeResponsavel.getNome();
        return getClass().getSimpleName() + "[id=" + id + ", " + getIdentificacao()
                + ", vegetacao=" + nivelVegetacao + ", " + equipe + "]";
    }
}
