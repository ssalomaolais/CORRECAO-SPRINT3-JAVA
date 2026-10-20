package com.motiva.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static ConexaoBD instancia;
    private Connection conexao;

    // Credenciais Oracle FIAP
    private final String HOST = "oracle.fiap.com.br";
    private final String PORT = "1521";
    private final String SID = "ORCL";
    private final String USER = "'RM565262'"; // RM
    private final String PASS = "041105";   // data de nascimento

    private ConexaoBD() {}

    public static ConexaoBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoBD();
        }
        return instancia;
    }

    public Connection conectar() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            try {
                // CORREÇÃO: Força o carregamento do driver do Oracle
                Class.forName("oracle.jdbc.OracleDriver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver do Oracle não encontrado!", e);
            }
            
            // URL no formato correto para o Oracle FIAP
            String url = "jdbc:oracle:thin:@" + HOST + ":" + PORT + ":" + SID;
            conexao = DriverManager.getConnection(url, USER, PASS);
        }
        return conexao;
    }

    public void desconectar() throws SQLException {
        if (conexao != null && !conexao.isClosed()) {
            conexao.close();
        }
    }
}
