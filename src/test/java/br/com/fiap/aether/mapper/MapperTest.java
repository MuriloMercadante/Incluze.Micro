package br.com.fiap.aether.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import br.com.fiap.aether.dto.AvaliacaoRequest;
import br.com.fiap.aether.dto.AvaliacaoResponse;
import br.com.fiap.aether.model.Avaliacao;
import br.com.fiap.aether.model.Local;
import br.com.fiap.aether.model.Usuario;

/** Testes unitários do mapeamento DTO <-> Entity (sem banco de dados). */
class MapperTest {

    private final AvaliacaoMapper mapper = new AvaliacaoMapper();

    @Test
    void requestNaoDevePreencherRelacionamentos() {
        Avaliacao avaliacao = mapper.toModel(request(4, 7L, 8L));

        assertThat(avaliacao.getNota()).isEqualTo(4);
        assertThat(avaliacao.getId()).isNull();
        assertThat(avaliacao.getUsuario()).isNull();
        assertThat(avaliacao.getLocal()).isNull();
    }

    @Test
    void atualizacaoNaoDeveAlterarEntidadesRelacionadas() {
        Avaliacao existente = new Avaliacao();
        existente.setId(1L);
        existente.setNota(2);
        existente.setUsuario(usuario(10L));
        existente.setLocal(local(20L));

        mapper.updateModel(request(5, 99L, 98L), existente);

        assertThat(existente.getId()).isEqualTo(1L);
        assertThat(existente.getNota()).isEqualTo(5);
        assertThat(existente.getUsuario().getId()).isEqualTo(10L);
        assertThat(existente.getLocal().getId()).isEqualTo(20L);
    }

    @Test
    void responseDeveAchatarRelacionamentos() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId(3L);
        avaliacao.setNota(5);
        avaliacao.setDataAvaliacao(LocalDateTime.now());
        avaliacao.setUsuario(usuario(10L));
        avaliacao.setLocal(local(20L));

        AvaliacaoResponse dto = mapper.toDto(avaliacao);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getUsuarioId()).isEqualTo(10L);
        assertThat(dto.getUsuarioNome()).isEqualTo("Usuario 10");
        assertThat(dto.getLocalId()).isEqualTo(20L);
        assertThat(dto.getLocalNome()).isEqualTo("Local 20");
    }

    private AvaliacaoRequest request(int nota, Long usuarioId, Long localId) {
        AvaliacaoRequest r = new AvaliacaoRequest();
        r.setNota(nota);
        r.setComentario("teste");
        r.setAcessibilidadeConfirmada(true);
        r.setUsuarioId(usuarioId);
        r.setLocalId(localId);
        return r;
    }

    private Usuario usuario(Long id) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNome("Usuario " + id);
        return u;
    }

    private Local local(Long id) {
        Local l = new Local();
        l.setId(id);
        l.setNome("Local " + id);
        return l;
    }
}
