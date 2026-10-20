package com.motiva.service;

import com.motiva.dao.RelatorioPrioridadeDAO;
import com.motiva.model.IntervencaoOperacional;
import com.motiva.model.Pulverizacao;
import com.motiva.model.RocadaMecanizada;
import com.motiva.model.TrechoRodovia;

public class GeradorRelatorio {

    public void gerarRelatorio(TrechoRodovia[] trechos) {
        // 1. parte do console
        imprimirCabecalho();

        int qtCritico = 0;
        int qtAtencao = 0;
        int qtNormal = 0;

        for (TrechoRodovia trecho : trechos) {
            tratarTrecho(trecho);

            if (trecho.isCritico()) {
                qtCritico++;
            } else if (trecho.isAtencao()) {
                qtAtencao++;
            } else {
                qtNormal++;
            }
        }

        System.out.println("===============================\n");

        String resumo = "Total de trechos: " + trechos.length
                + " | Criticos (rocada mecanizada): " + qtCritico
                + " | Atencao (pulverizacao): " + qtAtencao
                + " | Normais: " + qtNormal;
        System.out.println(resumo);

        // 2. salva o resultado no banco
        RelatorioPrioridadeDAO dao = new RelatorioPrioridadeDAO();
        boolean salvou = dao.salvarRelatorio(qtCritico, qtAtencao, qtNormal, resumo);

        if (salvou) {
            System.out.println("Relatorio salvo no banco.");
        } else {
            System.out.println("Nao foi possivel salvar o relatorio no banco.");
        }
    }

    private void imprimirCabecalho() {
        System.out.println("\n=== RELATÓRIO DE PRIORIDADE ===");
    }

    private void tratarTrecho(TrechoRodovia trecho) {
        if (trecho.isCritico()) {
            IntervencaoOperacional acao = new RocadaMecanizada();
            acao.executarServico(trecho);
            return;
        }

        if (trecho.isAtencao()) {
            IntervencaoOperacional acao = new Pulverizacao();
            acao.executarServico(trecho);
            return;
        }

        System.out.println("[OK] Vegetação controlada em " + trecho.getIdentificacao() + ". Nenhuma ação.");
    }
}
