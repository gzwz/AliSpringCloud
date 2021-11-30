/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.dubbo.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.cloud.dubbo.metadata.myprojectResolver;
import com.alibaba.cloud.dubbo.metadata.repository.DubboServiceMetadataRepository;
import com.alibaba.cloud.dubbo.service.DubboMetadataService;
import com.alibaba.cloud.dubbo.service.DubboMetadataServiceProxy;
import com.alibaba.cloud.dubbo.util.JSONUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.URLBuilder;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.rpc.RpcContext;

import org.springframework.cloud.client.ServiceInstance;

import static com.alibaba.cloud.dubbo.metadata.myprojectResolver.SCA_REVSION_KEY;
import static java.util.Collections.emptyList;
import static org.apache.dubbo.common.URLBuilder.from;
import static org.apache.dubbo.common.constants.CommonConstants.GROUP_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.PID_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.PROTOCOL_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.TIMESTAMP_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.VERSION_KEY;

/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
public class GenearalServiceSubscribeHandler extends AbstractServiceSubscribeHandler {

	/**
	 * the provider which can provide service of the url. {appName, [myprojects]}
	 */
	private final Map<String, Set<String>> providers = new HashMap<>();

	private final Map<String, URL> urlTemplateMap = new HashMap<>();

	private final JSONUtils jsonUtils;

	private final DubboServiceMetadataRepository repository;

	private final DubboMetadataServiceProxy dubboMetadataConfigServiceProxy;

	public GenearalServiceSubscribeHandler(URL url, NotifyListener listener,
			DubboCloudRegistry registry, DubboServiceMetadataRepository repository,
			JSONUtils jsonUtils,
			DubboMetadataServiceProxy dubboMetadataConfigServiceProxy) {
		super(url, listener, registry);
		this.repository = repository;
		this.jsonUtils = jsonUtils;
		this.dubboMetadataConfigServiceProxy = dubboMetadataConfigServiceProxy;
	}

	public boolean relatedWith(String appName, String myproject) {
		Set<String> list = providers.get(appName);
		if (list != null && list.size() > 0) {
			if (list.contains(myproject)) {
				return true;
			}
		}
		return false;
	}

	public void removeAppNameWithmyproject(String appName, String myproject) {
		Set<String> list = providers.get(appName);
		if (list != null) {
			list.remove(myproject);
			if (list.size() == 0) {
				providers.remove(appName);
			}
		}
	}

	public void addAppNameWithmyproject(String appName, String myproject) {
		Set<String> set = providers.computeIfAbsent(appName, k -> new HashSet<>());
		set.add(myproject);
	}

	public synchronized void doInit() {
		logger.debug("Subscription interface {}, GenearalServiceSubscribeHandler init",
				url.getServiceKey());
		Map<String, Map<String, List<ServiceInstance>>> map = registry
				.getServicemyprojectInstanceMap();
		for (Map.Entry<String, Map<String, List<ServiceInstance>>> entry : map
				.entrySet()) {
			String appName = entry.getKey();
			Map<String, List<ServiceInstance>> myprojectMap = entry.getValue();

			for (Map.Entry<String, List<ServiceInstance>> myprojectEntity : myprojectMap
					.entrySet()) {
				String myproject = myprojectEntity.getKey();
				List<ServiceInstance> instances = myprojectEntity.getValue();
				init(appName, myproject, instances);
			}
		}
		refresh();
	}

	public void init(String appName, String myproject,
			List<ServiceInstance> instanceList) {
		List<URL> urls = getTemplateExportedURLs(url, myproject, instanceList);
		if (urls != null && urls.size() > 0) {
			addAppNameWithmyproject(appName, myproject);
			setUrlTemplate(appName, myproject, urls);
		}
	}

	public synchronized void refresh() {
		List<URL> urls = getProviderURLs();
		notifyAllSubscribedURLs(url, urls, listener);
	}

	private List<URL> getProviderURLs() {
		List<ServiceInstance> instances = registry.getServiceInstances(providers);

		logger.debug("Subscription interfece {}, providers {}, total {}",
				url.getServiceKey(), providers, instances.size());

		if (instances.size() == 0) {
			return Collections.emptyList();
		}

		return cloneExportedURLs(instances);
	}

	void setUrlTemplate(String appName, String myproject, List<URL> urls) {
		if (urls == null || urls.size() == 0) {
			return;
		}
		String key = getAppmyprojectKey(appName, myproject);
		if (urlTemplateMap.containsKey(key)) {
			return;
		}
		urlTemplateMap.put(key, urls.get(0));
	}

	private String getAppmyprojectKey(String appName, String myproject) {
		return appName + "@" + myproject;
	}

	/**
	 * Clone the subscribed URLs based on the template URLs.
	 * @param serviceInstances the list of
	 * {@link org.springframework.cloud.client.ServiceInstance service instances}
	 * @return
	 */
	List<URL> cloneExportedURLs(List<ServiceInstance> serviceInstances) {

		List<URL> urlsCloneTo = new ArrayList<>();
		serviceInstances.forEach(serviceInstance -> {

			String host = serviceInstance.getHost();
			String appName = serviceInstance.getServiceId();
			String myproject = myprojectResolver.getmyproject(serviceInstance);

			URL template = urlTemplateMap.get(getAppmyprojectKey(appName, myproject));

			Stream.of(template)
					.map(templateURL -> templateURL.removeParameter(TIMESTAMP_KEY))
					.map(templateURL -> templateURL.removeParameter(PID_KEY))
					.map(templateURL -> {
						String protocol = templateURL.getProtocol();
						Integer port = repository.getDubboProtocolPort(serviceInstance,
								protocol);

						// reserve tag
						String tag = null;
						List<URL> urls = jsonUtils.toURLs(serviceInstance.getMetadata()
								.get("dubbo.metadata-service.urls"));
						if (urls != null && urls.size() > 0) {
							Map<String, String> parameters = urls.get(0).getParameters();
							tag = parameters.get("dubbo.tag");
						}

						if (Objects.equals(templateURL.getHost(), host)
								&& Objects.equals(templateURL.getPort(), port)) { // use
							// templateURL
							// if
							// equals
							return templateURL;
						}

						if (port == null) {
							if (logger.isWarnEnabled()) {
								logger.warn(
										"The protocol[{}] port of Dubbo  service instance[host : {}] "
												+ "can't be resolved",
										protocol, host);
							}
							return null;
						}
						else {
							URLBuilder clonedURLBuilder = from(templateURL) // remove the
									// parameters from
									// the template
									// URL
									.setHost(host) // reset the host
									.setPort(port) // reset the port
									.addParameter("dubbo.tag", tag); // reset the tag

							return clonedURLBuilder.build();
						}

					}).filter(Objects::nonNull).forEach(urlsCloneTo::add);
		});
		return urlsCloneTo;
	}

	private List<URL> getTemplateExportedURLs(URL subscribedURL, String myproject,
			List<ServiceInstance> serviceInstances) {

		DubboMetadataService dubboMetadataService = getProxy(serviceInstances);

		List<URL> templateExportedURLs = emptyList();

		if (dubboMetadataService != null) {
			templateExportedURLs = getExportedURLs(dubboMetadataService, myproject,
					subscribedURL);
		}
		else {
			if (logger.isWarnEnabled()) {
				logger.warn(
						"The metadata of Dubbo service[key : {}] still can't be found, it could effect the further "
								+ "Dubbo service invocation",
						subscribedURL.getServiceKey());
			}

		}

		return templateExportedURLs;
	}

	private DubboMetadataService getProxy(List<ServiceInstance> serviceInstances) {
		return dubboMetadataConfigServiceProxy.getProxy(serviceInstances);
	}

	private List<URL> getExportedURLs(DubboMetadataService dubboMetadataService,
			String myproject, URL subscribedURL) {
		String serviceInterface = subscribedURL.getServiceInterface();
		String group = subscribedURL.getParameter(GROUP_KEY);
		String version = subscribedURL.getParameter(VERSION_KEY);

		RpcContext.getContext().setAttachment(SCA_REVSION_KEY, myproject);
		String exportedURLsJSON = dubboMetadataService.getExportedURLs(serviceInterface,
				group, version);

		// The subscribed protocol may be null
		String subscribedProtocol = subscribedURL.getParameter(PROTOCOL_KEY);
		return jsonUtils.toURLs(exportedURLsJSON).stream()
				.filter(exportedURL -> subscribedProtocol == null
						|| subscribedProtocol.equalsIgnoreCase(exportedURL.getProtocol()))
				.collect(Collectors.toList());
	}

}
