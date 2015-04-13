/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.watcher.actions.webhook;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.watcher.actions.ActionFactory;
import org.elasticsearch.watcher.execution.Wid;
import org.elasticsearch.watcher.support.http.HttpClient;
import org.elasticsearch.watcher.support.http.HttpRequest;
import org.elasticsearch.watcher.support.http.HttpRequestTemplate;
import org.elasticsearch.watcher.support.template.TemplateEngine;

import java.io.IOException;

/**
 *
 */
public class WebhookActionFactory extends ActionFactory<WebhookAction, WebhookAction.Result, ExecutableWebhookAction> {

    private final HttpClient httpClient;
    private final HttpRequest.Parser requestParser;
    private final HttpRequestTemplate.Parser requestTemplateParser;
    private final TemplateEngine templateEngine;

    @Inject
    public WebhookActionFactory(Settings settings, HttpClient httpClient, HttpRequest.Parser requestParser,
                                HttpRequestTemplate.Parser requestTemplateParser, TemplateEngine templateEngine) {

        super(Loggers.getLogger(ExecutableWebhookAction.class, settings));
        this.httpClient = httpClient;
        this.requestParser = requestParser;
        this.requestTemplateParser = requestTemplateParser;
        this.templateEngine = templateEngine;
    }

    @Override
    public String type() {
        return WebhookAction.TYPE;
    }

    @Override
    public WebhookAction parseAction(String watchId, String actionId, XContentParser parser) throws IOException {
        return WebhookAction.parse(watchId, actionId, parser, requestTemplateParser);
    }

    @Override
    public WebhookAction.Result parseResult(Wid wid, String actionId, XContentParser parser) throws IOException {
        return WebhookAction.Result.parse(wid.watchId(), actionId, parser, requestParser);
    }

    @Override
    public ExecutableWebhookAction createExecutable(WebhookAction action) {
        return new ExecutableWebhookAction(action, actionLogger, httpClient, templateEngine);
    }
}
