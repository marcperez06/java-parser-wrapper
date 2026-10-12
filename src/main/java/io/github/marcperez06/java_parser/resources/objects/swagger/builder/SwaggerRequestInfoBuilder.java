package io.github.marcperez06.java_parser.resources.objects.swagger.builder;

import io.github.marcperez06.java_parser.resources.objects.swagger.SwaggerRequestInfo;

import java.util.List;
import java.util.Map;

import io.github.marcperez06.java_parser.resources.objects.swagger.SwaggerEndpointObjectInfo;

public class SwaggerRequestInfoBuilder {

    private SwaggerRequestInfo buildedObject;

    private List<String> tags;

    private String summary;

    private String description;

    private String operationId;

    private List<String> consumes;

    private List<String> produces;

    private List<SwaggerEndpointObjectInfo> parameters;

    private Map<String, SwaggerEndpointObjectInfo> responses;

    private Boolean deprecated;

    private List<Map<String, Object>> security;

    public SwaggerRequestInfoBuilder() {
        this.buildedObject = null;
    }

    public List<String> tags() {
        return this.tags;
    }

    public SwaggerRequestInfoBuilder tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public String summary() {
        return this.summary;
    }

    public SwaggerRequestInfoBuilder summary(String summary) {
        this.summary = summary;
        return this;
    }

    public String description() {
        return this.description;
    }

    public SwaggerRequestInfoBuilder description(String description) {
        this.description = description;
        return this;
    }

    public String operationId() {
        return this.operationId;
    }

    public SwaggerRequestInfoBuilder operationId(String operationId) {
        this.operationId = operationId;
        return this;
    }

    public List<String> consumes() {
        return this.consumes;
    }

    public SwaggerRequestInfoBuilder consumes(List<String> consumes) {
        this.consumes = consumes;
        return this;
    }

    public List<String> produces() {
        return this.produces;
    }

    public SwaggerRequestInfoBuilder produces(List<String> produces) {
        this.produces = produces;
        return this;
    }

    public List<SwaggerEndpointObjectInfo> parameters() {
        return this.parameters;
    }

    public SwaggerRequestInfoBuilder parameters(List<SwaggerEndpointObjectInfo> parameters) {
        this.parameters = parameters;
        return this;
    }

    public Map<String, SwaggerEndpointObjectInfo> responses() {
        return this.responses;
    }

    public SwaggerRequestInfoBuilder responses(Map<String, SwaggerEndpointObjectInfo> responses) {
        this.responses = responses;
        return this;
    }

    public boolean deprecated() {
        return this.deprecated;
    }

    public SwaggerRequestInfoBuilder deprecated(Boolean deprecated) {
        this.deprecated = deprecated;
        return this;
    }

    public List<Map<String, Object>> security() {
        return this.security;
    }

    public SwaggerRequestInfoBuilder security(List<Map<String, Object>> security) {
        this.security = security;
        return this;
    }

    public SwaggerRequestInfo build() {
        this.buildedObject = new SwaggerRequestInfo();
        if (this.tags != null) {
            this.buildedObject.setTags(this.tags);
        }
        if (this.summary != null) {
            this.buildedObject.setSummary(this.summary);
        }
        if (this.description != null) {
            this.buildedObject.setDescription(this.description);
        }
        if (this.operationId != null) {
            this.buildedObject.setOperationId(this.operationId);
        }
        if (this.consumes != null) {
            this.buildedObject.setConsumes(this.consumes);
        }
        if (this.produces != null) {
            this.buildedObject.setProduces(this.produces);
        }
        if (this.parameters != null) {
            this.buildedObject.setParameters(this.parameters);
        }
        if (this.responses != null) {
            this.buildedObject.setResponses(this.responses);
        }
        if (this.deprecated != null) {
            this.buildedObject.setDeprecated(this.deprecated.booleanValue());
        }
        if (this.security != null) {
            this.buildedObject.setSecurity(this.security);
        }
        return this.buildedObject;
    }
}
