/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package liquibase.ext.datatype.core;

import liquibase.database.Database;
import liquibase.database.core.MySQLDatabase;
import liquibase.datatype.DatabaseDataType;
import liquibase.datatype.core.BooleanType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MySQL (and MariaDB) represent boolean variables as TINYINT(1). Liquibase changed the representation of boolean
 * types in these databases to BIT(1) or BIT as of Liquibase version 3. This custom type ensures that TINYINT(1) is 
 * used instead.
 * 
 * @since 2.4
 */
public class MySQLBooleanType extends BooleanType {
	
	private static final Logger log = LoggerFactory.getLogger(MySQLBooleanType.class);
	
	@Override
	public DatabaseDataType toDatabaseDataType(Database database) {
		if (database instanceof MySQLDatabase) {
			DatabaseDataType result = new DatabaseDataType("TINYINT", 1);
			
			log.debug("boolean type for MySQL is '{}' ", result.getType());
			
			return result;
		}
		
		log.debug("delegating the choice of boolean type for database '{}' to super class of MySQLBooleanType",
		    database.getDatabaseProductName());
		
		return super.toDatabaseDataType(database);
	}


<dependency>
    <groupId>org.infinispan</groupId>
    <artifactId>infinispan-hibernate-cache-v53</artifactId>
    <version>${infinispanVersion}</version>
</dependency>

<infinispanVersion>13.0.22.Final</infinispanVersion>



<dependency>
<groupId>org.infinispan</groupId>
<artifactId>infinispan-spring5-embedded</artifactId>
</dependency>
<groupId>org.infinispan</groupId>
<artifactId>infinispan-hibernate-cache-v53</artifactId>


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
</dependency>
<groupId>org.infinispan</groupId>
<artifactId>infinispan-hibernate-cache-v53</artifactId>
<version>${infinispanVersion}</version>
<infinispanVersion>13.0.22.Final</infinispanVersion>

	
	@Override
	public int getPriority() {
		return super.getPriority() + 1;
	}
	@Override
	public String getName() {
    	return "mysql-boolean";
}

}
