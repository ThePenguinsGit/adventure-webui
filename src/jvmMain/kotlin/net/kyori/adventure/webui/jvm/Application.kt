package net.kyori.adventure.webui.jvm

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CachingHeaders
import io.ktor.features.Compression
import io.ktor.features.deflate
import io.ktor.features.gzip
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.content.CachingOptions
import io.ktor.routing.routing

public fun Application.main() {
    // routing
    routing {
        // enable trace routing if in dev mode
        if (developmentMode) {
            trace { route -> log.debug(route.buildText()) }
        }
    }

    install(Compression) {
        gzip()
        deflate()
    }

    install(CachingHeaders) {
        options { outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS, ContentType.Text.JavaScript ->
                    CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 3600))
                ContentType.Image.Any, ContentType.Application.FontWoff ->
                    CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 86400))
                else -> null
            }
        }
    }
}

/** Reads a string value from the `config` block in `application.conf`. */
public fun Application.getConfigString(key: String): String =
    environment.config.property("ktor.config.$key").getString()
