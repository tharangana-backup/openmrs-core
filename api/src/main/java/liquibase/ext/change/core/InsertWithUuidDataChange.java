/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package liquibase.ext.change.core;

import liquibase.change.ChangeMetaData;
import liquibase.change.ColumnConfig;
import liquibase.change.DatabaseChange;
import liquibase.change.core.InsertDataChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;
import liquibase.structure.core.Column;

import static java.util.UUID.randomUUID;

@DatabaseChange(name = "insertWithUuid", description = "Inserts data into an existing table and generates and inserts an uuid", priority = ChangeMetaData.PRIORITY_DEFAULT, appliesTo = "table")
public class InsertWithUuidDataChange extends InsertDataChange {
	
	private static final String UUID = "uuid";

	@Override
	public SqlStatement[] generateStatements(final Database database) {
		
		// Check if the insert change set specifies a value for the uuid column. If that is the case nothing else needs to be done.
		//
		for (final ColumnConfig column : getColumns()) {
			if (column.getName().equalsIgnoreCase(UUID)) {
				return super.generateStatements(database);
			}
		}
		
		// Add the uuid column to the insert statement.
		//
		ColumnConfig uuid = new ColumnConfig(new Column(UUID));
		uuid.setValue(randomUUID().toString());
		addColumn(uuid);
		return super.generateStatements(database);
	}
	import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.configuration.parsing.ParserRegistry;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.spring.embedded.provider.SpringEmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
* CacheConfig provides a cache manager for the @Cacheable annotation and uses Infinispan under the hood.
* The config of Infinispan is loaded from infinispan-api-local.xml/infinispan-api.xml and can be customized by
* providing a different file through the cache_config property. It is expected for the config to contain a template
* named "entity" to be used to create caches.
* <p>
* Caches can be added by modules through a cache-api.yaml file in the classpath.
* The file shall contain only the <b>caches</b> element as defined in Infinispan docs at
* <a href="https://infinispan.org/docs/13.0.x/titles/configuring/configuring.html#multiple_caches">multiple caches</a>
* <p>
* Please note the underlying implementation changed from ehcache to Infinispan since 2.8.x
* to support replicated/distributed caches.
private static Logger log = LoggerFactory.getLogger(CacheConfig.class);
@Value("${cache_type:local}")
private String cacheType;
@Value("${cache_config:}")
private String cacheConfig;
@Bean(name = "apiCacheManager")
public SpringEmbeddedCacheManager apiCacheManager() throws IOException {
if (StringUtils.isBlank(cacheConfig)) {
String local = "local".equalsIgnoreCase(cacheType.trim()) ? "-local" : "";
cacheConfig = "infinispan-api" + local + ".xml";
}
ParserRegistry parser = new ParserRegistry();
ConfigurationBuilderHolder baseConfigBuilder = parser.parseFile(cacheConfig);
// Determine cache type based on loaded template for "entity"
String cacheType = baseConfigBuilder.getNamedConfigurationBuilders().get("entity").build().elementName();
cacheType = StringUtils.removeEnd(cacheType, "-configuration");
cacheType = CaseUtils.toCamelCase(cacheType, false, '-');
DumperOptions options = new DumperOptions();
options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
options.setPrettyFlow(true);
Yaml yaml = new Yaml(options);
for (URL configFile : getCacheConfigurations()) {
// Apply cache type for caches using the 'entity' template
// and add the 'infinispan.cacheContainer.caches' parent
InputStream fullConfig = buildFullConfig(yaml, configFile, cacheType);
ConfigurationBuilderHolder configBuilder = parser.parse(fullConfig, baseConfigBuilder, null,
MediaType.APPLICATION_YAML);
// Merge cache definitions into baseConfigBuilder
configBuilder.getNamedConfigurationBuilders().forEach((name, builder) -> {
baseConfigBuilder.getNamedConfigurationBuilders().put(name, builder);
});
}
DefaultCacheManager cacheManager = new DefaultCacheManager(baseConfigBuilder, true);
return new SpringEmbeddedCacheManager(cacheManager);
}
private static InputStream buildFullConfig(Yaml yaml, URL configFile, String cacheType) throws IOException {
Map<String, Object> loadedConfig = yaml.load(configFile.openStream());
Map<String, Object> config = new LinkedHashMap<>();
Map<String, Object> cacheContainer = new LinkedHashMap<>();
Map<String, Object> caches = new LinkedHashMap<>();
Map<String, Object> cacheList = new LinkedHashMap<>();
config.put("infinispan", cacheContainer);
cacheContainer.put("cacheContainer", caches);
caches.put("caches", cacheList);
@SuppressWarnings("unchecked")
Map<String, Object> loadedCaches = (Map<String, Object>) loadedConfig.get("caches");
for (Map.Entry<String, Object> entry : loadedCaches.entrySet()) {
@SuppressWarnings("unchecked")
Map<String, Object> value = (Map<String, Object>) entry.getValue();
if ("entity".equals(value.get("configuration"))) {
Map<Object, Object> cache = new LinkedHashMap<>();
cache.put(cacheType, value);
cacheList.put(entry.getKey(), cache);
} else {
cacheList.put(entry.getKey(), value);
}
}
String configDump = yaml.dump(config);
return new ByteArrayInputStream(configDump.getBytes(StandardCharsets.UTF_8));
}
public List<URL> getCacheConfigurations() {
Resource[] configResources;
try {
ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
configResources = patternResolver.getResources("classpath*:cache-api.yaml");
} catch (IOException e) {
throw new IllegalStateException("Unable to find cache configurations", e);
}
List<URL> files = new ArrayList<>();
for (Resource configResource : configResources) {
try {
URL file = configResource.getURL();
files.add(file);
} catch (IOException e) {
log.error("Failed to get cache config file: {}", configResource, e);
}
}
return files;
}
import org.springframework.beans.factory.annotation.Value;
@Value("${cache_type:local}")
private String cacheType;
// Load infinispan config based on selected cache type
String local = "local".equalsIgnoreCase(cacheType.trim()) ? "-local" : "";
props.put("hibernate.cache.infinispan.cfg",
"org/infinispan/hibernate/cache/commons/builder/infinispan-configs" + local + ".xml");
<bean class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer">
<property name="locations">
<list>
<value>classpath:hibernate.default.properties</value>
<value>file:${OPENMRS_APPLICATION_DATA_DIRECTORY}/openmrs-runtime.properties</value>
</list>
</property>
<property name="ignoreResourceNotFound" value="true" />
<property name="localOverride" value="true" />
</bean>
caches:
userSearchLocales:
configuration: "entity"
conceptIdsByMapping:
configuration: "entity"
<property name="javax.persistence.sharedCache.mode">ENABLE_SELECTIVE</property>
<!-- These mappings are required because of references in Obs & Concept -->
hibernate.cache.use_structured_entries=true
# Hibernate cache
hibernate.cache.region.factory_class=infinispan
# hibernate.cache.infinispan.cfg is configured by HibernateSessionFactoryBean based on cache_type property
<?xml version="1.0" encoding="utf-8"?>
<!--
This Source Code Form is subject to the terms of the Mozilla Public License,
v. 2.0. If a copy of the MPL was not distributed with this file, You can
obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
graphic logo is a trademark of OpenMRS Inc.
-->
<infinispan xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="urn:infinispan:config:13.0 https://infinispan.org/schemas/infinispan-config-13.0.xsd"
xmlns="urn:infinispan:config:13.0">>
<cache-container>
<local-cache-configuration name="entity" simple-cache="true" statistics="false" statistics-available="false">
<encoding media-type="application/x-java-object"/>
<transaction mode="NONE" />
<expiration max-idle="100000" interval="5000"/>
<memory max-count="10000"/>
</local-cache-configuration>
</cache-container>
</infinispan>
<?xml version="1.0" encoding="utf-8"?>
<!--
This Source Code Form is subject to the terms of the Mozilla Public License,
v. 2.0. If a copy of the MPL was not distributed with this file, You can
obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
graphic logo is a trademark of OpenMRS Inc.
-->
<infinispan
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="urn:infinispan:config:13.0 https://infinispan.org/schemas/infinispan-config-13.0.xsd"
xmlns="urn:infinispan:config:13.0">
<jgroups>
<stack-file name="api-jgroups" path="${hibernate.cache.infinispan.jgroups_cfg:default-configs/default-jgroups-tcp.xml}"/>
</jgroups>
<cache-container>
<transport stack="api-jgroups" cluster="infinispan-api-cluster"/>
<!-- Default configuration is appropriate for entity/collection caching. -->
<invalidation-cache-configuration name="entity" remote-timeout="20000" statistics="false" statistics-available="false">
<encoding media-type="application/x-java-object"/>
<locking concurrency-level="1000" acquire-timeout="15000"/>
<transaction mode="NONE" />
<expiration max-idle="100000" interval="5000"/>
<memory max-count="10000"/>
</invalidation-cache-configuration>
</cache-container>
</infinispan>
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
SimpleKey key = new SimpleKey("wgt234", "sstrm", true);
assertThat(cache.get(key), is(nullValue()));
List<Integer> conceptIdsByMapping = conceptService.getConceptIdsByMapping("wgt234", "sstrm", true);
assertThat(cache.get(key).get(), is(conceptIdsByMapping));
SimpleKey cacheKey = new SimpleKey(crt.getCode(), cs.getHl7Code(), true);
List<Integer> conceptIdsByMapping = conceptService.getConceptIdsByMapping(crt.getCode(), cs.getHl7Code(), true);
assertThat(cache.get(cacheKey).get(), is(conceptIdsByMapping));
assertThat(cache.get(cacheKey), is(nullValue()));
conceptIdsByMapping = conceptService.getConceptIdsByMapping(crt.getCode(), cs.getHl7Code(), true);
assertThat(cache.get(cacheKey).get(), is(conceptIdsByMapping));
assertThat(cache.get(cacheKey), is(nullValue()));
conceptIdsByMapping = conceptService.getConceptIdsByMapping(crt.getCode(), cs.getHl7Code(), true);
assertThat(cache.get(cacheKey).get(), is(conceptIdsByMapping));
assertThat(cache.get(cacheKey), is(nullValue()));
import java.net.URL;
import java.util.List;
public class CacheConfigTest extends BaseContextSensitiveTest {
@Autowired
CacheManager cacheManager;
@Autowired
CacheConfig cacheConfig;
@Test
public void shouldContainSpecificCacheConfigurations(){
String[] expectedCaches = {"conceptDatatype", "subscription", "userSearchLocales", "conceptIdsByMapping"};
Collection<String> actualCaches = cacheManager.getCacheNames();
assertThat(actualCaches, containsInAnyOrder(expectedCaches));
}
public void shouldReturnCacheConfigurations(){
List<URL> cacheConfigurations = cacheConfig.getCacheConfigurations();
assertThat(cacheConfigurations.size(), is(2));
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import java.util.concurrent.TimeUnit;
import org.hibernate.Cache;
import org.hibernate.CacheMode;
import org.hibernate.cache.internal.EnabledCaching;
import org.hibernate.stat.EntityStatistics;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.Ignore;
import org.springframework.test.context.transaction.TestTransaction;
// Entities are not put in cache until transaction is committed thus running non-transactional test
TestTransaction.flagForCommit();
TestTransaction.end();
await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
assertTrue(sf.getCache().containsEntity(PERSON_NAME_CLASS, PERSON_NAME_ID_2));
assertTrue(sf.getCache().containsEntity(PERSON_NAME_CLASS, PERSON_NAME_ID_8));
});
TestTransaction.flagForCommit();
TestTransaction.end();
await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
assertTrue(sf.getCache().containsEntity(PERSON_NAME_CLASS, PERSON_NAME_ID_2));
assertTrue(sf.getCache().containsEntity(PERSON_NAME_CLASS, PERSON_NAME_ID_8));
assertTrue(sf.getCache().containsEntity(Patient.class, PERSON_NAME_ID_2));
});
TestTransaction.flagForCommit();
TestTransaction.end();
await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
assertTrue(sf.getCache().containsEntity(PERSON_NAME_CLASS, PERSON_NAME_ID_2));
assertTrue(sf.getCache().containsEntity(PERSON_NAME_CLASS, PERSON_NAME_ID_8));
assertTrue(sf.getCache().containsEntity(Patient.class, PERSON_NAME_ID_2));
});
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.configuration.parsing.ParserRegistry;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.spring.embedded.provider.SpringEmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
* CacheConfig provides a cache manager for the @Cacheable annotation and uses Infinispan under the hood.
* The config of Infinispan is loaded from infinispan-api-local.xml/infinispan-api.xml and can be customized by
* providing a different file through the cache_config property. It is expected for the config to contain a template
* named "entity" to be used to create caches.
* <p>
* Caches can be added by modules through a cache-api.yaml file in the classpath.
* The file shall contain only the <b>caches</b> element as defined in Infinispan docs at
* <a href="https://infinispan.org/docs/13.0.x/titles/configuring/configuring.html#multiple_caches">multiple caches</a>
* <p>
* Please note the underlying implementation changed from ehcache to Infinispan since 2.8.x
* to support replicated/distributed caches.
private static Logger log = LoggerFactory.getLogger(CacheConfig.class);
@Value("${cache_type:local}")
private String cacheType;
@Value("${cache_config:}")
private String cacheConfig;
@Bean(name = "apiCacheManager")
public SpringEmbeddedCacheManager apiCacheManager() throws IOException {
if (StringUtils.isBlank(cacheConfig)) {
String local = "local".equalsIgnoreCase(cacheType.trim()) ? "-local" : "";
cacheConfig = "infinispan-api" + local + ".xml";
}
ParserRegistry parser = new ParserRegistry();
ConfigurationBuilderHolder baseConfigBuilder = parser.parseFile(cacheConfig);
// Determine cache type based on loaded template for "entity"
String cacheType = baseConfigBuilder.getNamedConfigurationBuilders().get("entity").build().elementName();
cacheType = StringUtils.removeEnd(cacheType, "-configuration");
cacheType = CaseUtils.toCamelCase(cacheType, false, '-');
DumperOptions options = new DumperOptions();
options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
options.setPrettyFlow(true);
Yaml yaml = new Yaml(options);
for (URL configFile : getCacheConfigurations()) {
// Apply cache type for caches using the 'entity' template
// and add the 'infinispan.cacheContainer.caches' parent
InputStream fullConfig = buildFullConfig(yaml, configFile, cacheType);
ConfigurationBuilderHolder configBuilder = parser.parse(fullConfig, baseConfigBuilder, null,
MediaType.APPLICATION_YAML);
// Merge cache definitions into baseConfigBuilder
configBuilder.getNamedConfigurationBuilders().forEach((name, builder) -> {
baseConfigBuilder.getNamedConfigurationBuilders().put(name, builder);
});
}
DefaultCacheManager cacheManager = new DefaultCacheManager(baseConfigBuilder, true);
return new SpringEmbeddedCacheManager(cacheManager);
}
private static InputStream buildFullConfig(Yaml yaml, URL configFile, String cacheType) throws IOException {
Map<String, Object> loadedConfig = yaml.load(configFile.openStream());
Map<String, Object> config = new LinkedHashMap<>();
Map<String, Object> cacheContainer = new LinkedHashMap<>();
Map<String, Object> caches = new LinkedHashMap<>();
Map<String, Object> cacheList = new LinkedHashMap<>();
config.put("infinispan", cacheContainer);
cacheContainer.put("cacheContainer", caches);
caches.put("caches", cacheList);
@SuppressWarnings("unchecked")
Map<String, Object> loadedCaches = (Map<String, Object>) loadedConfig.get("caches");
for (Map.Entry<String, Object> entry : loadedCaches.entrySet()) {
@SuppressWarnings("unchecked")
Map<String, Object> value = (Map<String, Object>) entry.getValue();
if ("entity".equals(value.get("configuration"))) {
Map<Object, Object> cache = new LinkedHashMap<>();
cache.put(cacheType, value);
cacheList.put(entry.getKey(), cache);
} else {
cacheList.put(entry.getKey(), value);
}
}
String configDump = yaml.dump(config);
return new ByteArrayInputStream(configDump.getBytes(StandardCharsets.UTF_8));
}
public List<URL> getCacheConfigurations() {
Resource[] configResources;
try {
ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
configResources = patternResolver.getResources("classpath*:cache-api.yaml");
} catch (IOException e) {
throw new IllegalStateException("Unable to find cache configurations", e);
}
List<URL> files = new ArrayList<>();
for (Resource configResource : configResources) {
try {
URL file = configResource.getURL();
files.add(file);
} catch (IOException e) {
log.error("Failed to get cache config file: {}", configResource, e);
}
}
return files;
}
import org.springframework.beans.factory.annotation.Value;
@Value("${cache_type:local}")
private String cacheType;
// Load infinispan config based on selected cache type
String local = "local".equalsIgnoreCase(cacheType.trim()) ? "-local" : "";
props.put("hibernate.cache.infinispan.cfg",
"org/infinispan/hibernate/cache/commons/builder/infinispan-configs" + local + ".xml");
<bean class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer">
<property name="locations">
<list>
<value>classpath:hibernate.default.properties</value>
<value>file:${OPENMRS_APPLICATION_DATA_DIRECTORY}/openmrs-runtime.properties</value>
</list>
</property>
<property name="ignoreResourceNotFound" value="true" />
<property name="localOverride" value="true" />
</bean>
caches:
userSearchLocales:
configuration: "entity"
conceptIdsByMapping:
configuration: "entity"
<property name="javax.persistence.sharedCache.mode">ENABLE_SELECTIVE</property>
<!-- These mappings are required because of references in Obs & Concept -->
hibernate.cache.use_structured_entries=true
# Hibernate cache
hibernate.cache.region.factory_class=infinispan
# hibernate.cache.infinispan.cfg is configured by HibernateSessionFactoryBean based on cache_type property
<?xml version="1.0" encoding="utf-8"?>
<!--
This Source Code Form is subject to the terms of the Mozilla Public License,
v. 2.0. If a copy of the MPL was not distributed with this file, You can
obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
graphic logo is a trademark of OpenMRS Inc.
-->
<infinispan xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="urn:infinispan:config:13.0 https://infinispan.org/schemas/infinispan-config-13.0.xsd"
xmlns="urn:infinispan:config:13.0">>
<cache-container>
<local-cache-configuration name="entity" simple-cache="true" statistics="false" statistics-available="false">
<encoding media-type="application/x-java-object"/>
<transaction mode="NONE" />
<expiration max-idle="100000" interval="5000"/>
<memory max-count="10000"/>
</local-cache-configuration>
</cache-container>
</infinispan>
<?xml version="1.0" encoding="utf-8"?>
<!--
This Source Code Form is subject to the terms of the Mozilla Public License,
v. 2.0. If a copy of the MPL was not distributed with this file, You can
obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
graphic logo is a trademark of OpenMRS Inc.
-->
<infinispan
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="urn:infinispan:config:13.0 https://infinispan.org/schemas/infinispan-config-13.0.xsd"
xmlns="urn:infinispan:config:13.0">
<jgroups>
<stack-file name="api-jgroups" path="${hibernate.cache.infinispan.jgroups_cfg:default-configs/default-jgroups-tcp.xml}"/>
</jgroups>
<cache-container>
<transport stack="api-jgroups" cluster="infinispan-api-cluster"/>
<!-- Default configuration is appropriate for entity/collection caching. -->
<invalidation-cache-configuration name="entity" remote-timeout="20000" statistics="false" statistics-available="false">
<encoding media-type="application/x-java-object"/>
<locking concurrency-level="1000" acquire-timeout="15000"/>
<transaction mode="NONE" />
<expiration max-idle="100000" interval="5000"/>
<memory max-count="10000"/>
</invalidation-cache-configuration>
</cache-container>
</infinispan>
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
SimpleKey key = new SimpleKey("wgt234", "sstrm", true);
assertThat(cache.get(key), is(nullValue()));
List<Integer> conceptIdsByMapping = conceptService.getConceptIdsByMapping("wgt234", "sstrm", true);
assertThat(cache.get(key).get(), is(conceptIdsByMapping));
SimpleKey cacheKey = new SimpleKey(crt.getCode(), cs.getHl7Code(), true);
List<Integer> conceptIdsByMapping = conceptService.getConceptIdsByMapping(crt.getCode(), cs.getHl7Code(), true);
assertThat(cache.get(cacheKey).get(), is(conceptIdsByMapping));
assertThat(cache.get(cacheKey), is(nullValue()));
conceptIdsByMapping = conceptService.getConceptIdsByMapping(crt.getCode(), cs.getHl7Code(), true);
assertThat(cache.get(cacheKey).get(), is(conceptIdsByMapping));
assertThat(cache.get(cacheKey), is(nullValue()));
conceptIdsByMapping = conceptService.getConceptIdsByMapping(crt.getCode(), cs.getHl7Code(), true);
assertThat(cache.get(cacheKey).get(), is(conceptIdsByMapping));
assertThat(cache.get(cacheKey), is(nullValue()));
import java.net.URL;
import java.util.List;
public class CacheConfigTest extends BaseContextSensitiveTest {
@Autowired
CacheManager cacheManager;
@Autowired
CacheConfig cacheConfig;
@Test
public void shouldContainSpecificCacheConfigurations(){
String[] expectedCaches = {"conceptDatatype", "subscription", "userSearchLocales", "conceptIdsByMapping"};
Collection<String> actualCaches = cacheManager.getCacheNames();
assertThat(actualCaches, containsInAnyOrder(expectedCaches));
}
public void shouldReturnCacheConfigurations(){
List<URL> cacheConfigurations = cacheConfig.getCacheConfigurations();
assertThat(cacheConfigurations.size(), is(2));
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import java.util.concurrent.TimeUnit;
import org.hibernate.Cache;
import org.hibernate.CacheMode;
import org.hibernate.cache.internal.EnabledCaching;
import org.hibernate.stat.EntityStatistics;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.Ignore;
import org.springframework.test.context.transaction.TestTransaction;
// Entities are not put in cache until transaction is committed thus running non-transactional test
TestTransaction.flagForCommit();
TestTransaction.end();
await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
assertTrue(sf.getCache().containsEntity(PERSON_NAME_CLASS, PERSON_NAME_ID_2));
assertTrue(sf.getCache().containsEntity(PERSON_NAME_CLASS, PERSON_NAME_ID_8));
});
TestTransaction.flagForCommit();
TestTransaction.end();
await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
assertTrue(sf.getCache().containsEntity(PERSON_NAME_CLASS, PERSON_NAME_ID_2));
assertTrue(sf.getCache().containsEntity(PERSON_NAME_CLASS, PERSON_NAME_ID_8));
assertTrue(sf.getCache().containsEntity(Patient.class, PERSON_NAME_ID_2));
});
TestTransaction.flagForCommit();
TestTransaction.end();
await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
assertTrue(sf.getCache().containsEntity(PERSON_NAME_CLASS, PERSON_NAME_ID_2));
assertTrue(sf.getCache().containsEntity(PERSON_NAME_CLASS, PERSON_NAME_ID_8));
assertTrue(sf.getCache().containsEntity(Patient.class, PERSON_NAME_ID_2));
});

}
