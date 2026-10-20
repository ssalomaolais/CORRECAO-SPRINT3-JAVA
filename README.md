# motiva-sprint3

Sistema de monitoramento e priorização de roçada de vegetação nas rodovias (Motiva).
Nesta Sprint 3 o sistema ganhou **persistência em banco Oracle usando JDBC puro**, em cima das classes da Sprint 2.

## Integrantes

| Nome | RM |
|---|---|
| Guilherme Ferraz de Medeiros | 564935 |
| Roberto Marques Moreira | 564743 |
| Anny Elly | 565055 |
| Lais Salomão | 565262 |

## Requisitos

- Java 17
- Driver JDBC da Oracle (`ojdbc17.jar` ou, pelo Maven, `ojdbc11`)
- Acesso ao banco Oracle da FIAP (usuário e senha configurados no `ConexaoBD.java`)

## Driver JDBC

- **Maven:** o `pom.xml` já tem a dependência `ojdbc11`, o Maven baixa sozinho.
- **Sem Maven (como na aula):** baixar o `ojdbc17.jar` em https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html e salvar na pasta `sprint3/lib/`.

## Estrutura

```
script-criacao.sql      -> cria as tabelas
script-dados.sql        -> dados de teste
sprint3/
  pom.xml
  lib/                  -> ojdbc17.jar (para rodar sem Maven)
  src/main/java/com/motiva/
    db/        ConexaoBD
    model/     classes de domínio da Sprint 2 + records das entidades
    dao/       um DAO para cada entidade
    service/   GeradorRelatorio
    main/      Main
```

## Melhorias da Sprint 2 (feedback do professor)

- **Classe abstrata:** `TrechoRodovia` agora é abstrata.
- **Polimorfismo:** `TrechoSeco` e `TrechoUmido` herdam de `TrechoRodovia` e definem o próprio ritmo de crescimento (`getTaxaCrescimentoDiaria()`). O boolean `isUmido` virou um método implementado por cada subclasse.
- **Sensor IoT:** `TrechoSecoInteligente` e `TrechoUmidoInteligente` herdam do tipo de trecho e implementam `MonitoravelViaIoT`.
- **@Override:** adicionado em `Pulverizacao`, que estava sem a anotação.
- **Constantes:** os números soltos viraram constantes nomeadas (`LIMITE_CRITICO`, `LIMITE_ATENCAO`, `TAXA_CRESCIMENTO_SECO`, `TAXA_CRESCIMENTO_UMIDO`).

## Persistência

| Tabela | Classe / record | Observação |
|---|---|---|
| `EquipeManutencao` | record `EquipeManutencao` | |
| `TrechoRodovia` | `TrechoRodovia` (abstrata) e suas subclasses | `tipo_clima` (SECO/UMIDO) e `possui_sensor` definem qual subclasse o DAO devolve; `id_equipe` guarda a equipe alocada |
| `IntervencaoOperacional` | record `IntervencaoRegistro` | `tipo` (ROCADA_MECANIZADA/PULVERIZACAO) define a classe filha que executa o serviço |
| `RelatorioPrioridade` | record `RelatorioPrioridade` | quantidades de trechos críticos, em atenção e normais |

`TrechoRodovia` é abstrata e por isso não pode ser um record. O `TrechoRodoviaDAO` lê a linha para um record interno e monta o objeto da subclasse correta.

## Relatório de prioridade

O `GeradorRelatorio` (antigo `GerenciadorDePrioridades`) mantém as regras da Sprint 2 e, depois de imprimir no console, salva o resultado em `RelatorioPrioridade`:

- vegetação >= `LIMITE_CRITICO` (30): trecho crítico, roçada mecanizada;
- vegetação >= `LIMITE_ATENCAO` (15) e abaixo do crítico: atenção, pulverização;
- abaixo disso: normal, nenhuma ação.

## Como executar

1. Conectar no Oracle da FIAP (SQL Developer ou outro cliente) e rodar o `script-criacao.sql`.
2. Rodar o `script-dados.sql`.
3. Conferir usuário e senha no `sprint3/src/main/java/com/motiva/db/ConexaoBD.java`.
4. Dentro da pasta `sprint3`, executar de um dos jeitos abaixo.

**Com Maven:**

```
mvn compile exec:java
```

**Sem Maven (Windows, o separador do classpath é `;`):**

```
javac -cp "lib\ojdbc17.jar" -d bin -sourcepath src\main\java src\main\java\com\motiva\main\Main.java
java -cp "bin;lib\ojdbc17.jar" com.motiva.main.Main
```

**Sem Maven (Linux/Mac, o separador do classpath é `:`):**

```
javac -cp "lib/ojdbc17.jar" -d bin -sourcepath src/main/java src/main/java/com/motiva/main/Main.java
java -cp "bin:lib/ojdbc17.jar" com.motiva.main.Main
```

## O que o Main faz

1. Testa a conexão com o banco.
2. CRUD de equipe (inserir, buscar, listar, atualizar, deletar).
3. CRUD de trecho: valida os dados, insere um trecho úmido, lista (os trechos com sensor transmitem os dados), simula o crescimento, aloca a equipe, atualiza e deleta.
4. CRUD de intervenção, executando o serviço da classe filha do tipo guardado.
5. Gera o relatório no console e salva o resultado na tabela `RelatorioPrioridade`.
6. Lista o histórico de relatórios salvos.
7. Fecha a conexão.

## Observações

- Os DAOs usam `PreparedStatement` e fecham `PreparedStatement` e `ResultSet` com try-with-resources. A conexão é única (`ConexaoBD`) e é fechada no final do `Main`.
- A intervenção tem chave estrangeira para o trecho, e o trecho tem chave estrangeira para a equipe. Por isso, ao apagar um trecho ou uma equipe, é preciso apagar antes os registros que dependem dele (`ORA-02292`).
