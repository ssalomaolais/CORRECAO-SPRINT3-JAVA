package com.motiva.dao;

import com.motiva.db.ConexaoBD;
import com.motiva.model.EquipeManutencao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipeManutencaoDAO {

    private static final String SQL_INSERIR =
            "INSERT INTO EquipeManutencao (nome) VALUES (?)";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id, nome FROM EquipeManutencao WHERE id = ?";
    private static final String SQL_LISTAR_TODAS =
            "SELECT id, nome FROM EquipeManutencao ORDER BY id";
    private static final String SQL_ATUALIZAR =
            "UPDATE EquipeManutencao SET nome = ? WHERE id = ?";
    private static final String SQL_DELETAR =
            "DELETE FROM EquipeManutencao WHERE id = ?";

    public EquipeManutencaoDAO() {
    }

    // insere e devolve o id gerado (-1 se der erro)
    public int inserir(EquipeManutencao equipe) {
        try {
            Connection conn = ConexaoBD.getInstancia().conectar();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERIR, new String[] {"ID"})) {
                stmt.setString(1, equipe.nome());
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

    // devolve a equipe ou null se nao achar
    public EquipeManutencao buscarPorId(int id) {
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

    public List<EquipeManutencao> listarTodas() {
        List<EquipeManutencao> lista = new ArrayList<>();
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
    public boolean atualizar(EquipeManutencao equipe) {
        try {
            Connection conn = ConexaoBD.getInstancia().conectar();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_ATUALIZAR)) {
                stmt.setString(1, equipe.nome());
                stmt.setInt(2, equipe.id());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // true se apagou alguma linha (falha se algum trecho ainda usa a equipe)
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
    private EquipeManutencao mapear(ResultSet rs) throws SQLException {
        return new EquipeManutencao(rs.getInt("id"), rs.getString("nome"));
    }
}
