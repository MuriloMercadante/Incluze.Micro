package br.com.fiap.aether.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import br.com.fiap.aether.dto.LocalAppResponse;
import br.com.fiap.aether.model.CategoriaLocal;
import br.com.fiap.aether.model.Local;

/** Testes do formato entregue ao app mobile (sem banco de dados). */
class LocalMapperTest {

    private final LocalMapper mapper = new LocalMapper();

    @Test
    void appDtoConverteCategoriaERecursos() {
        Local local = new Local();
        local.setId(7L);
        local.setNome("Café Encontro Sensorial");
        local.setEndereco("Rua Augusta, 1200");
        local.setBairro("Consolação");
        local.setCategoria(CategoriaLocal.SAUDE);
        local.setPossuiRampa(true);
        local.setPossuiBanheiroAdaptado(false);
        local.setPossuiElevador(true);
        local.setPossuiPisoTatil(false);
        local.setAtendimentoLibras(true);
        local.setCardapioBraille(false);
        local.setAceitaCaoGuia(true);
        local.setAmbienteBaixoEstimulo(true);
        local.setNotaMedia(4.5);
        local.setTotalAvaliacoes(6);
        local.setVerificado(true);

        LocalAppResponse dto = mapper.toAppDto(local);

        assertThat(dto.getCategoria()).isEqualTo("Saúde");
        assertThat(dto.getRecursos()).containsExactly("cadeirante", "elevador", "libras", "cao_guia", "baixo_estimulo");
        assertThat(dto.getNota()).isEqualTo(4.5);
        assertThat(dto.getAvaliacoes()).isEqualTo(6);
        assertThat(dto.getVerificado()).isTrue();
    }

    @Test
    void appDtoUsaPadroesQuandoNaoHaAvaliacoesNemTextos() {
        Local local = new Local();
        local.setId(1L);
        local.setNome("Local novo");
        local.setEndereco("Rua X, 1");
        local.setCategoria(CategoriaLocal.CULTURA);

        LocalAppResponse dto = mapper.toAppDto(local);

        assertThat(dto.getCategoria()).isEqualTo("Cultura");
        assertThat(dto.getNota()).isZero();
        assertThat(dto.getAvaliacoes()).isZero();
        assertThat(dto.getVerificado()).isFalse();
        assertThat(dto.getBairro()).isEmpty();
        assertThat(dto.getImagem()).isEmpty();
        assertThat(dto.getRecursos()).isEmpty();
    }
}
