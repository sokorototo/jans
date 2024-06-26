/*
 * Copyright [2024] [Janssen Project]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jans.lock.service.provider.metric;

import org.slf4j.Logger;

import io.jans.service.cdi.qualifier.Implementation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Null metric provider
 *
 * @author Yuriy Movchan Date: 05/23/2024
 */
@Implementation
@ApplicationScoped
public class NullMetricProvider extends MetricProvider {
	
	public static String METRIC_PROVIDER_TYPE = "DISABLED";

	@Inject
	private Logger log;

	@Override
	public void destroy() {
		log.debug("Destroy metric provider");
	}

}
