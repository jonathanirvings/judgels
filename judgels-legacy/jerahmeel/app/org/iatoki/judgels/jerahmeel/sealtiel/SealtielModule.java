package org.iatoki.judgels.jerahmeel.sealtiel;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.palantir.conjure.java.api.config.service.UserAgent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import judgels.sealtiel.api.SealtielClientConfiguration;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import judgels.service.jaxrs.JaxRsClients;

import javax.inject.Named;

public final class SealtielModule extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    SealtielClientConfiguration config() {
        Config config = ConfigFactory.load();
        return new SealtielClientConfiguration.Builder()
                .baseUrl(config.getString("sealtiel.baseUrl"))
                .clientJid(config.getString("sealtiel.clientJid"))
                .clientSecret(config.getString("sealtiel.clientSecret"))
                .build();
    }

    @Provides
    @Named("sealtiel")
    BasicAuthHeader clientAuthHeader(SealtielClientConfiguration config) {
        return BasicAuthHeader.of(Client.of(config.getClientJid(), config.getClientSecret()));
    }

    @Provides
    MessageService messageService(SealtielClientConfiguration config) {
        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("sandalphon", UserAgent.Agent.DEFAULT_VERSION));
        return JaxRsClients.create(MessageService.class, config.getBaseUrl(), userAgent);
    }
}
