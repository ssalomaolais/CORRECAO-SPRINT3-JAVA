package com.motiva.dao;

import com.motiva.db.ConexaoBD;
import com.motiva.model.EquipeManutencao;
import com.motiva.model.MonitoravelViaIoT;
import com.motiva.model.TrechoRodovia;
import com.motiva.model.TrechoSeco;
import com.motiva.model.TrechoSecoInteligente;
import com.motiva.model.TrechoUmido;
import com.motiva.model.TrechoUmidoInteligente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TrechoRodoviaDAO {

    // valores guardados nas colunas tipo_clima e possui_sensor
    private static final String CLIMA_SECO = "SECO";
    private static final String CLIMA_UMIDO = "UMIDO";
    private static final int COM_SENSOR = 1;
    private static final int SEM_SENSOR = 0;

    private static final String SQL_SELECT =
            "SELECT t.id, t.nome_rodovia, t.km_inicial, t.km_final, t.tipo_clima, t.possui_sensor, "
                    + "t.nivel_vegetacao, t.id_equipe, e.nome AS nome_equipe "
                    + "FROM TrechoRodovia t LEFT JOIN EquipeManutencao e ON e.id = t.id_equipe";
    private static final String SQL_INSERIR =
            "INSERT INTO TrechoRodovia (nome_rodovia, km_inicial, km_final, tipo_clima, possui_sensor, "
                    + "nivel_vegetacao, id_equipe) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID = SQL_SELECT + " WHERE t.id = ?";
    private static final String SQL_LISTAR_TODAS = SQL_SELECT + " ORDER BY t.id";
    private static final String SQL_ATUALIZAR =
            "UPDATE TrechoRodovia SET nome_rodovia = ?, km_inicial = ?, km_final = ?, tipo_clima = ?, "
                    + "possui_sensor = ?, nivel_vegetacao = ?, id_equipe = ? WHERE id = ?";
    private static final String SQL_DELETAR =
            "DELETE FROM TrechoRodovia WHERE id = ?";

    // linha da tabela (TrechoRodovia e abstrata, entao o record guarda os dados lidos do banco)
    private record Registro(int id, String nomeRodovia, int kmInicial, int kmFinal, String tipoClima,
                            boolean possuiSensor, double nivelVegetacao, EquipeManutencao equipe) {
    }

    public TrechoRodoviaDAO() {
    }

    // insere e devolve o id gerado (-1 se der erro)
    public int inserir(TrechoRodovia trecho) {
        try {
            Connection conn = ConexaoBD.getInstancia().conectar();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERIR, new String[] {"ID"})) {
                preencher(stmt, trecho);
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        trecho.setId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // devolve o trecho (ja na subclasse certa) ou null se nao achar
    public TrechoRodovia buscarPorId(int id) {
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

    public List<TrechoRodovia> listarTodas() {
        List<TrechoRodovia> lista = new ArrayList<>();
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
    public boolean atualizar(TrechoRodovia trecho) {
        try {
            Connection conn = ConexaoBD.getInstancia().conectar();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_ATUALIZAR)) {
                preencher(stmt, trecho);
                stmt.setInt(8, trecho.getId());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // true se apagou alguma linha (falha se ainda houver intervencao ligada ao trecho)
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

    // parametros 1 a 7, iguais no insert e no update
    private void preencher(PreparedStatement stmt, TrechoRodovia trecho) throws SQLException {
        stmt.setString(1, trecho.getNomeRodovia());
        stmt.setInt(2, trecho.getQuilometroInicial());
        stmt.setInt(3, trecho.getQuilometroFinal());
        stmt.setString(4, trecho.isUmido() ? CLIMA_UMIDO : CLIMA_SECO);
        stmt.setInt(5, (trecho instanceof MonitoravelViaIoT) ? COM_SENSOR : SEM_SENSOR);
        stmt.setDouble(6, trecho.getNivelVegetacao());

        EquipeManutencao equipe = trecho.getEquipeResponsavel();
        if (equipe == null) {
            stmt.setNull(7, Types.NUMERIC);
        } else {
            stmt.setInt(7, equipe.id());
        }
    }

    // le a linha, guarda no record e monta o objeto da subclasse certa
    private TrechoRodovia mapear(ResultSet rs) throws SQLException {
        int idEquipe = rs.getInt("id_equipe");
        EquipeManutencao equipe = null;
        if (!rs.wasNull()) {
            equipe = new EquipeManutencao(idEquipe, rs.getString("nome_equipe"));
        }

        Registro registro = new Registro(
                rs.getInt("id"),
                rs.getString("nome_rodovia"),
                rs.getInt("km_inicial"),
                rs.getInt("km_final"),
                rs.getString("tipo_clima"),
                rs.getInt("possui_sensor") == COM_SENSOR,
                rs.getDouble("nivel_vegetacao"),
                equipe);

        return criarTrecho(registro);
    }

    private TrechoRodovia criarTrecho(Registro r) {
        TrechoRodovia trecho;

        if (CLIMA_UMIDO.equals(r.tipoClima())) {
            trecho = r.possuiSensor()
                    ? new TrechoUmidoInteligente(r.nomeRodovia(), r.kmInicial(), r.kmFinal(), r.nivelVegetacao())
                    : new TrechoUmido(r.nomeRodovia(), r.kmInicial(), r.kmFinal(), r.nivelVegetacao());
        } else {
            trecho = r.possuiSensor()
                    ? new TrechoSecoInteligente(r.nomeRodovia(), r.kmInicial(), r.kmFinal(), r.nivelVegetacao())
                    : new TrechoSeco(r.nomeRodovia(), r.kmInicial(), r.kmFinal(), r.nivelVegetacao());
        }

        trecho.setId(r.id());
        trecho.setEquipeResponsavel(r.equipe());
        return trecho;
    }
}
