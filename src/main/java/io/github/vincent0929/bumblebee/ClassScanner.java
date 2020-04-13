package io.github.vincent0929.bumblebee;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClassScanner implements ApplicationContextAware, ResourceLoaderAware {

  private ResourcePatternResolver resourcePatternResolver;

  private CachingMetadataReaderFactory cachingMetadataReaderFactory;

  private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
  private static final String PATH_SEPARATOR = "/";

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    init(applicationContext);
  }

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
    this.cachingMetadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
  }

  private void init(ApplicationContext applicationContext) {
    Map<String, ClassProcessor> beanExtensionProcessorMap = applicationContext.getBeansOfType(ClassProcessor.class);
    if (beanExtensionProcessorMap.isEmpty()) {
      return;
    }

    Collection<ClassProcessor> classProcessors = beanExtensionProcessorMap.values();
    List<String> packages = AutoConfigurationPackages.get(applicationContext);
    try {
      for (String basePackage : packages) {
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                                   + ClassUtils.convertClassNameToResourcePath(applicationContext.getEnvironment().resolveRequiredPlaceholders(basePackage))
                                   + PATH_SEPARATOR + DEFAULT_RESOURCE_PATTERN;
        Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
          MetadataReader metadataReader = cachingMetadataReaderFactory.getMetadataReader(resource);
          String className = metadataReader.getClassMetadata().getClassName();
          Class<?> clazz = applicationContext.getClassLoader().loadClass(className);
          classProcessors.forEach(classProcessor -> {
            if (classProcessor.isSupport(clazz)) {
              classProcessor.process(clazz);
            }
          });
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      log.error("scan class failed", e);
      throw new RuntimeException(e);
    }
  }
}
