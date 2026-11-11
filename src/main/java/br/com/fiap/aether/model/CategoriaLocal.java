package br.com.fiap.aether.model;

/** Categoria do local, com o rótulo exibido para o usuário (ex.: no app mobile). */
public enum CategoriaLocal {
    RESTAURANTE("Restaurante"),
    SAUDE("Saúde"),
    ORGAO_PUBLICO("Serviço Público"),
    TRANSPORTE("Transporte"),
    COMERCIO("Comércio"),
    EDUCACAO("Educação"),
    CULTURA("Cultura"),
    ESPORTE("Esporte"),
    LAZER("Lazer"),
    OUTRO("Outro");

    private final String rotulo;

    CategoriaLocal(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
