package br.com.fiap.aether.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.fiap.aether.model.CategoriaLocal;
import br.com.fiap.aether.model.Local;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {

    /**
     * Busca de locais por recursos de acessibilidade. Cada filtro é opcional:
     * quando o parâmetro é nulo, ele não restringe o resultado.
     */
    @Query("""
            select l from Local l
            where (:cidade is null or lower(l.cidade) = lower(:cidade))
              and (:categoria is null or l.categoria = :categoria)
              and (:possuiRampa is null or l.possuiRampa = :possuiRampa)
              and (:atendimentoLibras is null or l.atendimentoLibras = :atendimentoLibras)
              and (:cardapioBraille is null or l.cardapioBraille = :cardapioBraille)
              and (:aceitaCaoGuia is null or l.aceitaCaoGuia = :aceitaCaoGuia)
              and (:ambienteBaixoEstimulo is null or l.ambienteBaixoEstimulo = :ambienteBaixoEstimulo)
              and (:possuiBanheiroAdaptado is null or l.possuiBanheiroAdaptado = :possuiBanheiroAdaptado)
              and (:possuiElevador is null or l.possuiElevador = :possuiElevador)
              and (:possuiPisoTatil is null or l.possuiPisoTatil = :possuiPisoTatil)
              and (:verificado is null or l.verificado = :verificado)
              and (:notaMinima is null or l.notaMedia >= :notaMinima)
            order by l.id
            """)
    List<Local> buscar(@Param("cidade") String cidade,
            @Param("categoria") CategoriaLocal categoria,
            @Param("possuiRampa") Boolean possuiRampa,
            @Param("atendimentoLibras") Boolean atendimentoLibras,
            @Param("cardapioBraille") Boolean cardapioBraille,
            @Param("aceitaCaoGuia") Boolean aceitaCaoGuia,
            @Param("ambienteBaixoEstimulo") Boolean ambienteBaixoEstimulo,
            @Param("possuiBanheiroAdaptado") Boolean possuiBanheiroAdaptado,
            @Param("possuiElevador") Boolean possuiElevador,
            @Param("possuiPisoTatil") Boolean possuiPisoTatil,
            @Param("verificado") Boolean verificado,
            @Param("notaMinima") Double notaMinima);
}
