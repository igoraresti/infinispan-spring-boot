package test.org.infinispan.spring.starter.embedded;

import static org.assertj.core.api.Assertions.assertThat;

import org.infinispan.eviction.EvictionType;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.spring.starter.embedded.InfinispanEmbeddedAutoConfiguration;
import org.infinispan.spring.starter.embedded.InfinispanEmbeddedCacheManagerAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import test.org.infinispan.spring.starter.embedded.testconfiguration.InfinispanCacheConfigurationBaseTestConfiguration;
import test.org.infinispan.spring.starter.embedded.testconfiguration.InfinispanCacheConfigurationTestConfiguration;

@SpringBootTest(
      classes = {
            InfinispanEmbeddedAutoConfiguration.class,
            InfinispanEmbeddedCacheManagerAutoConfiguration.class,
            InfinispanCacheConfigurationBaseTestConfiguration.class,
            InfinispanCacheConfigurationTestConfiguration.class
      },
      properties = {
            "spring.main.banner-mode=off"
      }
)
public class InfinispanEmbeddedAutoConfigurationIntegrationConfigurationTest {

   @Autowired
   EmbeddedCacheManager manager;

   @Test
   public void testConfiguration() {
      assertThat(manager.getCacheNames()).contains("base-cache", "small-cache", "large-cache");
      assertThat(manager.getCacheConfiguration("base-cache").memory().size()).isEqualTo(500L);
      assertThat(manager.getCacheConfiguration("base-cache").memory().evictionType()).isEqualTo(EvictionType.COUNT);
      assertThat(manager.getCacheConfiguration("small-cache").memory().size()).isEqualTo(1000L);
      assertThat(manager.getCacheConfiguration("small-cache").memory().evictionType()).isEqualTo(EvictionType.COUNT);
      assertThat(manager.getCacheConfiguration("large-cache").memory().size()).isEqualTo(2000L);
      assertThat(manager.getCacheConfiguration("large-cache").memory().evictionType()).isEqualTo(EvictionType.COUNT);
   }
}
