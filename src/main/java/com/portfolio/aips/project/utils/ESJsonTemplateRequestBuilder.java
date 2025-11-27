package com.portfolio.aips.project.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;

import java.io.IOException;

public class ESJsonTemplateRequestBuilder {

    private final ElasticsearchClient client;

    private String methodType;
    private String reqUrl;
    private String jsonQuery;

    public ESJsonTemplateRequestBuilder(ElasticsearchClient client) {
        this.client = client;
    }

    public ESJsonTemplateRequestBuilder method(String methodType) {
        this.methodType = methodType;
        return this;
    }

    public ESJsonTemplateRequestBuilder url(String reqUrl) {
        this.reqUrl = reqUrl;
        return this;
    }

    public ESJsonTemplateRequestBuilder body(String jsonQuery) {
        this.jsonQuery = jsonQuery;
        return this;
    }

    public Response execute() throws IOException {
        Request request = new Request(methodType, reqUrl);
        if (jsonQuery != null) {
            request.setJsonEntity(jsonQuery);
        }

        return ((RestClientTransport) client._transport())
                .restClient()
                .performRequest(request);
    }
}
