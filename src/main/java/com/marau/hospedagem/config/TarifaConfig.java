package com.marau.hospedagem.config;

import com.marau.hospedagem.service.tarifa.GerenciadorTarifas;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ponte entre o Singleton GoF {@link GerenciadorTarifas} e o container do Spring.
 *
 * <p>O gerenciador continua sendo um Singleton clássico (acessível via
 * {@code getInstance()} em qualquer ponto do sistema, inclusive nas entidades de
 * domínio), mas também é exposto como bean para permitir injeção por construtor
 * nos controllers/serviços, sem que existam duas instâncias diferentes.</p>
 */
@Configuration
public class TarifaConfig {

    @Bean
    public GerenciadorTarifas gerenciadorTarifas() {
        return GerenciadorTarifas.getInstance();
    }
}
