package com.motiva.main;

import com.motiva.dao.EquipeManutencaoDAO;
import com.motiva.dao.IntervencaoOperacionalDAO;
import com.motiva.dao.RelatorioPrioridadeDAO;
import com.motiva.dao.TrechoRodoviaDAO;
import com.motiva.db.ConexaoBD;
import com.motiva.model.EquipeManutencao;
import com.motiva.model.IntervencaoRegistro;
import com.motiva.model.MonitoravelViaIoT;
import com.motiva.model.TipoIntervencao;
import com.motiva.model.TrechoRodovia;
import com.motiva.model.TrechoSeco;
import com.motiva.model.TrechoUmido;
import com.motiva.service.GeradorRelatorio;

import java.sql.SQLException;
import java.util.List;

public class Main {

    // dias de crescimento simulados no teste do trecho
    private static final int DIAS_SIMULACAO = 7;

    public static void main(String[] args) {
        // 1. testar conexao
        ConexaoBD conexao = ConexaoBD.getInstancia();
        try {
            conexao.conectar();
            System.out.println("Conexao com o Oracle OK.");
        } catch (SQLException e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 2. CRUD de equipe
        testarEquipe();

        // 3. CRUD de trecho
        testarTrecho();

        // 4. CRUD de intervencao
        testarIntervencao();

        // 5. gerar relatorio com persistencia
        System.out.println("\n--- Relatorio ---");
        TrechoRodoviaDAO daoTrecho = new TrechoRodoviaDAO();
        TrechoRodovia[] trechos = daoTrecho.listarTodas().toArray(new TrechoRodovia[0]);
        GeradorRelatorio gerador = new GeradorRelatorio();
        gerador.gerarRelatorio(trechos);

        // 6. historico de relatorios
        System.out.println("\n--- Historico de relatorios ---");
        RelatorioPrioridadeDAO daoRelatorio = new RelatorioPrioridadeDAO();
        daoRelatorio.listarTodas().forEach(r -> System.out.println(r));

        // 7. fechar conexao
        try {
            conexao.desconectar();
            System.out.println("\nConexao fechada.");
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexao: " + e.getMessage());
        }
    }

    private static void testarEquipe() {
        System.out.println("\n--- CRUD Equipe ---");
        EquipeManutencaoDAO dao = new EquipeManutencaoDAO();

        int id = dao.inserir(new EquipeManutencao("Equipe Teste"));
        if (id < 0) {
            System.out.println("Nao foi possivel inserir a equipe.");
            return;
        }
        System.out.println("Inserida com id " + id);

        System.out.println("Busca por id: " + dao.buscarPorId(id));

        System.out.println("Lista:");
        dao.listarTodas().forEach(e -> System.out.println("  " + e));

        dao.atualizar(new EquipeManutencao(id, "Equipe Teste Atualizada"));
        System.out.println("Depois do update: " + dao.buscarPorId(id));

        dao.deletar(id);
        System.out.println("Depois do delete: " + dao.buscarPorId(id));
    }

    private static void testarTrecho() {
        System.out.println("\n--- CRUD Trecho ---");
        testarValidacao();

        TrechoRodoviaDAO dao = new TrechoRodoviaDAO();

        TrechoRodovia trecho = new TrechoUmido("SP-999", 0, 10, 0.0);
        int id = dao.inserir(trecho);
        if (id < 0) {
            System.out.println("Nao foi possivel inserir o trecho.");
            return;
        }
        System.out.println("Inserido com id " + id);

        System.out.println("Busca por id: " + dao.buscarPorId(id));

        System.out.println("Lista:");
        for (TrechoRodovia t : dao.listarTodas()) {
            System.out.println("  " + t);

            // trechos com sensor transmitem os dados (interface MonitoravelViaIoT)
            if (t instanceof MonitoravelViaIoT sensor) {
                sensor.transmitirDadosSensor();
            }
        }

        // cada tipo de trecho cresce no seu ritmo (polimorfismo) e o novo nivel vai para o banco
        trecho.simularCrescimentoDias(DIAS_SIMULACAO);

        List<EquipeManutencao> equipes = new EquipeManutencaoDAO().listarTodas();
        if (!equipes.isEmpty()) {
            trecho.alocarEquipe(equipes.get(0));
        }

        dao.atualizar(trecho);
        System.out.println("Depois do update: " + dao.buscarPorId(id));

        dao.deletar(id);
        System.out.println("Depois do delete: " + dao.buscarPorId(id));
    }

    // mesma validacao que ja existia na Sprint 2
    private static void testarValidacao() {
        try {
            new TrechoSeco("BR-101", 50, 20, -5.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Sucesso na validacao (erro capturado): " + e.getMessage());
        }
    }

    private static void testarIntervencao() {
        System.out.println("\n--- CRUD Intervencao ---");

        // a intervencao precisa de um trecho que ja exista
        List<TrechoRodovia> trechos = new TrechoRodoviaDAO().listarTodas();
        if (trechos.isEmpty()) {
            System.out.println("Sem trecho cadastrado. Rode o script-dados.sql primeiro.");
            return;
        }
        TrechoRodovia trecho = trechos.get(0);

        IntervencaoOperacionalDAO dao = new IntervencaoOperacionalDAO();

        int id = dao.inserir(new IntervencaoRegistro(0, trecho.getId(), TipoIntervencao.ROCADA_MECANIZADA));
        if (id < 0) {
            System.out.println("Nao foi possivel inserir a intervencao.");
            return;
        }
        System.out.println("Inserida com id " + id);

        IntervencaoRegistro registro = dao.buscarPorId(id);
        System.out.println("Busca por id: " + registro);

        // a classe filha do tipo guardado executa o servico
        if (registro != null) {
            registro.tipo().criarServico().executarServico(trecho);
        }

        System.out.println("Lista:");
        dao.listarTodas().forEach(i -> System.out.println("  " + i));

        dao.atualizar(new IntervencaoRegistro(id, trecho.getId(), TipoIntervencao.PULVERIZACAO));
        System.out.println("Depois do update: " + dao.buscarPorId(id));

        dao.deletar(id);
        System.out.println("Depois do delete: " + dao.buscarPorId(id));
    }
}
