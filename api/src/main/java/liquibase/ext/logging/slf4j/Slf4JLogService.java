/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package liquibase.ext.logging.slf4j;

import java.util.HashMap;
import java.util.Map;

import liquibase.logging.Logger;
import liquibase.logging.core.AbstractLogService;

/**
 * An implementation of {@link liquibase.logging.LogService} to use SLF4J for Liquibase logging
 * 
 * @since 2.5.1, 2.6.0
 */
public class Slf4JLogService extends AbstractLogService {
	
	private final Map<Class<?>, Slf4JLogger> loggers = new HashMap<>();
	
	@Override
	public int getPriority() {
		return PRIORITY_SPECIALIZED;
	}
	
	@Override
	public Logger getLog(Class clazz) {
		return loggers.computeIfAbsent(clazz, c -> new Slf4JLogger(c, getFilter()));
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
caches:
subscription:
configuration: "entity"
memory:
maxCount: 1000
conceptDatatype:
configuration: "entity"
<dependency>
<groupId>org.infinispan</groupId>
<artifactId>infinispan-spring5-embedded</artifactId>
<version>${infinispanVersion}</version>

}
