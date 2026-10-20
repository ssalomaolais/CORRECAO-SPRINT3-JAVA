package com.motiva.model;

import java.sql.Timestamp;

// representa uma linha da tabela RelatorioPrioridade
public record RelatorioPrioridade(int id, int qtCritico, int qtAtencao, int qtNormal,
                                  String resumo, Timestamp dataGeracao) {
}
