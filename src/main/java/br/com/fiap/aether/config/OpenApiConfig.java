package br.com.fiap.aether.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI aetherOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Incluze - API CitizenTech de Acessibilidade (Grupo Aether)")
                .version("v1")
                .description("""
                        Plataforma colaborativa (crowdsourcing) para mapear, avaliar e reportar \
                        as condições reais de acessibilidade de espaços públicos e privados. \
                        Inclui recursos de inclusão invisível (Libras, Braille, cão-guia, \
                        ambientes de baixo estímulo), alertas temporários de barreiras e gamificação. \
                        Alinhada aos ODS 10 e 11 da ONU."""));
    }
}
