package com.motiva.dao;

import com.motiva.db.ConexaoBD;
import com.motiva.model.IntervencaoRegistro;
import com.motiva.model.TipoIntervencao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IntervencaoOperacionalDAO {

    private static final String SQL_INSERIR =
            "INSERT INTO IntervencaoOperacional (id_trecho, tipo) VALUES (?, ?)";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id, id_trecho, tipo FROM IntervencaoOperacional WHERE id = ?";
    private static final String SQL_LISTAR_TODAS =
            "SELECT id, id_trecho, tipo FROM IntervencaoOperacional ORDER BY id";
    private static final String SQL_ATUALIZAR =
            "UPDATE IntervencaoOperacional SET id_trecho = ?, tipo = ? WHERE id = ?";
    private static final String SQL_DELETAR =
            "DELETE FROM IntervencaoOperacional WHERE id = ?";

    public IntervencaoOperacionalDAO() {
    }

    // insere e devolve o id gerado (-1 se der erro)
    public int inserir(IntervencaoRegistro intervencao) {
        try {
            Connection conn = ConexaoBD.getInstancia().conectar();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERIR, new String[] {"ID"})) {
                stmt.setInt(1, intervencao.idTrecho());
                stmt.setString(2, intervencao.tipo().name());
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

    // devolve a intervencao ou null se nao achar
    public IntervencaoRegistro buscarPorId(int id) {
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

    public List<IntervencaoRegistro> listarTodas() {
        List<IntervencaoRegistro> lista = new ArrayList<>();
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
    public boolean atualizar(IntervencaoRegistro intervencao) {
        try {
            Connection conn = ConexaoBD.getInstancia().conectar();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_ATUALIZAR)) {
                stmt.setInt(1, intervencao.idTrecho());
                stmt.setString(2, intervencao.tipo().name());
                stmt.setInt(3, intervencao.id());
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
    private IntervencaoRegistro mapear(ResultSet rs) throws SQLException {
        return new IntervencaoRegistro(
                rs.getInt("id"),
                rs.getInt("id_trecho"),
                TipoIntervencao.valueOf(rs.getString("tipo")));
    }
}
