/*
 * Copyright (c) 2016 Red Hat, Inc. and/or its affiliates.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.jberet.camel;

import javax.annotation.PostConstruct;
import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Named;

import _private.JBeretCamelLogger;
import org.apache.camel.ProducerTemplate;

/**
 * Implementation of {@code javax.batch.api.chunk.ItemProcessor} that processes
 * batch data using Apache Camel component.
 * <p>
 * The target Camel endpoint is configured through batch property
 * {@code endpoint} in job XML. For example,
 * <pre>
 * &lt;job id="camelReaderTest" xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="1.0"&gt;
 *   &lt;step id="camelReaderTest.step1"&gt;
 *     &lt;chunk&gt;
 *       ... ...
 *       &lt;processor ref="camelItemProcessor"&gt;
 *         &lt;properties&gt;
 *           &lt;property name="endpoint" value="#{jobParameters['endpoint']}"/&gt;
 *         &lt;/properties&gt;
 *       &lt;/processor&gt;
 *       ... ...
 * </pre>
 *
 * @see CamelItemReader
 * @see CamelItemWriter
 * @since 1.3.0
 */
@Named
public class CamelItemProcessor extends CamelArtifactBase implements ItemProcessor {

    /**
     * The Camel {@code ProducerTemplate} for forwarding the processing request
     * to Camel endpoint.
     */
    protected ProducerTemplate producerTemplate;

    @PostConstruct
    private void postConstruct() {
        init();
        if (producerTemplate == null) {
            producerTemplate = camelContext.createProducerTemplate();
        }
        JBeretCamelLogger.LOGGER.openProcessor(this, endpointUri, camelContext, producerTemplate);
    }

    /**
     * {@inheritDoc}
     * <p>
     *
     * This method forwards the current {@code item} to the configured Camel
     * endpoint for processing, and also retrieves the processing result.
     *
     * @param item the current item to be processed
     * @return processing result
     * @throws Exception
     */
    @Override
    public Object processItem(final Object item) throws Exception {
        return producerTemplate.requestBody(endpoint, item);
    }
}
