package com.motiva.dao;

import com.motiva.db.ConexaoBD;
import com.motiva.model.RelatorioPrioridade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RelatorioPrioridadeDAO {

    // data_geracao fica de fora porque a tabela ja preenche sozinha
    private static final String SQL_INSERIR =
            "INSERT INTO RelatorioPrioridade (qt_critico, qt_atencao, qt_normal, resumo) VALUES (?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id, qt_critico, qt_atencao, qt_normal, resumo, data_geracao "
                    + "FROM RelatorioPrioridade WHERE id = ?";
    private static final String SQL_LISTAR_TODAS =
            "SELECT id, qt_critico, qt_atencao, qt_normal, resumo, data_geracao "
                    + "FROM RelatorioPrioridade ORDER BY id";
    private static final String SQL_ATUALIZAR =
            "UPDATE RelatorioPrioridade SET qt_critico = ?, qt_atencao = ?, qt_normal = ?, resumo = ? "
                    + "WHERE id = ?";
    private static final String SQL_DELETAR =
            "DELETE FROM RelatorioPrioridade WHERE id = ?";

    public RelatorioPrioridadeDAO() {
    }

    // usado pelo GeradorRelatorio para guardar o resultado no banco
    public boolean salvarRelatorio(int qtCritico, int qtAtencao, int qtNormal, String resumo) {
        RelatorioPrioridade relatorio = new RelatorioPrioridade(0, qtCritico, qtAtencao, qtNormal, resumo, null);
        return inserir(relatorio) > 0;
    }

    // insere e devolve o id gerado (-1 se der erro)
    public int inserir(RelatorioPrioridade relatorio) {
        try {
            Connection conn = ConexaoBD.getInstancia().conectar();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERIR, new String[] {"ID"})) {
                stmt.setInt(1, relatorio.qtCritico());
                stmt.setInt(2, relatorio.qtAtencao());
                stmt.setInt(3, relatorio.qtNormal());
                stmt.setString(4, relatorio.resumo());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // devolve o relatorio ou null se nao achar
    public RelatorioPrioridade buscarPorId(int id) {
        try {
            Connection conn = ConexaoBD.getInstancia().conectar();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_BUSCAR_POR_ID)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapear(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<RelatorioPrioridade> listarTodas() {
        List<RelatorioPrioridade> lista = new ArrayList<>();
        try {
            Connection conn = ConexaoBD.getInstancia().conectar();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR_TODAS);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // true se atualizou alguma linha
    public boolean atualizar(RelatorioPrioridade relatorio) {
        try {
            Connection conn = ConexaoBD.getInstancia().conectar();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_ATUALIZAR)) {
                stmt.setInt(1, relatorio.qtCritico());
                stmt.setInt(2, relatorio.qtAtencao());
                stmt.setInt(3, relatorio.qtNormal());
                stmt.setString(4, relatorio.resumo());
                stmt.setInt(5, relatorio.id());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // true se apagou alguma linha
    public boolean deletar(int id) {
        try {
            Connection conn = ConexaoBD.getInstancia().conectar();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETAR)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // transforma a linha do ResultSet no record
    private RelatorioPrioridade mapear(ResultSet rs) throws SQLException {
        return new RelatorioPrioridade(
                rs.getInt("id"),
                rs.getInt("qt_critico"),
                rs.getInt("qt_atencao"),
                rs.getInt("qt_normal"),
                rs.getString("resumo"),
                rs.getTimestamp("data_geracao"));
    }
}
