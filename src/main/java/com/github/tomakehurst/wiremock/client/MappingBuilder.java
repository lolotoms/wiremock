/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tomakehurst.wiremock.client;

import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.matching.RequestMatcher;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import static com.google.common.base.Preconditions.checkArgument;

public class MappingBuilder implements LocalMappingBuilder, ScenarioMappingBuilder {
	
	private RequestPatternBuilder requestPatternBuilder;
	private ResponseDefinitionBuilder responseDefBuilder;
	private Integer priority;
	private String scenarioName;
	protected String requiredScenarioState;
	protected String newScenarioState;
	
	public MappingBuilder(RequestMethod method, UrlMatchingStrategy urlMatchingStrategy) {
		requestPatternBuilder = new RequestPatternBuilder(method, urlMatchingStrategy);
	}

	public MappingBuilder(RequestMatcher requestMatcher) {
		requestPatternBuilder = RequestPatternBuilder.forCustomMatcher(requestMatcher);
	}

	public MappingBuilder(String customRequestMatcherName, Parameters parameters) {
		requestPatternBuilder = RequestPatternBuilder.forCustomMatcher(customRequestMatcherName, parameters);
	}

	@Override
	public MappingBuilder willReturn(ResponseDefinitionBuilder responseDefBuilder) {
		this.responseDefBuilder = responseDefBuilder;
		return this;
	}

	@Override
	public MappingBuilder atPriority(Integer priority) {
		this.priority = priority;
		return this;
	}

	@Override
	public MappingBuilder withHeader(String key, ValueMatchingStrategy headerMatchingStrategy) {
		requestPatternBuilder.withHeader(key, headerMatchingStrategy);
		return this;
	}

    @Override
    public MappingBuilder withQueryParam(String key, ValueMatchingStrategy queryParamMatchingStrategy) {
        requestPatternBuilder.withQueryParam(key, queryParamMatchingStrategy);
        return this;
    }

	@Override
	public MappingBuilder withRequestBody(ValueMatchingStrategy bodyMatchingStrategy) {
		requestPatternBuilder.withRequestBody(bodyMatchingStrategy);
		return this;
	}

	@Override
    public ScenarioMappingBuilder inScenario(String scenarioName) {
        checkArgument(scenarioName != null, "Scenario name must not be null");

		this.scenarioName = scenarioName;
		return this;
	}

	@Override
    public StubMapping build() {
		RequestPattern requestPattern = requestPatternBuilder.build();
		ResponseDefinition response = responseDefBuilder.build();
		StubMapping mapping = new StubMapping(requestPattern, response);
		mapping.setPriority(priority);
		mapping.setScenarioName(scenarioName);
		mapping.setRequiredScenarioState(requiredScenarioState);
		mapping.setNewScenarioState(newScenarioState);
		return mapping;
	}

    @Override
    public ScenarioMappingBuilder whenScenarioStateIs(String stateName) {
        this.requiredScenarioState = stateName;
        return this;
    }

    @Override
    public ScenarioMappingBuilder willSetStateTo(String stateName) {
        this.newScenarioState = stateName;
        return this;
    }
}
