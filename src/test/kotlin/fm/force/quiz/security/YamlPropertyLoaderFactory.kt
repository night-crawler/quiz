package io.kotlintest.provided.fm.force.quiz.security

import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.core.env.PropertySource
import org.springframework.core.io.support.DefaultPropertySourceFactory
import org.springframework.core.io.support.EncodedResource

class YamlPropertyLoaderFactory : DefaultPropertySourceFactory() {
    override fun createPropertySource(name: String?, resource: EncodedResource): PropertySource<*> {
        return YamlPropertySourceLoader().load(resource.resource.filename, resource.resource)[0]
    }
}
