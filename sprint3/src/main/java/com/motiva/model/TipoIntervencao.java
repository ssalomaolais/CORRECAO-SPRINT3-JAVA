package com.motiva.model;

// tipos de intervencao que podem ser guardados no banco
public enum TipoIntervencao {

    ROCADA_MECANIZADA {
        @Override
        public IntervencaoOperacional criarServico() {
            return new RocadaMecanizada();
        }
    },

    PULVERIZACAO {
        @Override
        public IntervencaoOperacional criarServico() {
            return new Pulverizacao();
        }
    };

    // devolve a classe filha que executa esse tipo de servico
    public abstract IntervencaoOperacional criarServico();
}
