package kz.kff.core.di.application

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.config.SchemaOverwriteModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.swagger.v3.oas.models.media.Schema
import kz.kff.core.config.SwaggerConfig

fun Application.configureSwaggerLocal(swaggerConfig: SwaggerConfig){
    install(OpenApi) {
        info {
            title = swaggerConfig.title
            description = swaggerConfig.description
            version = swaggerConfig.version
        }
        schemas {
            generator = SchemaGenerator.reflection {
                overwrite(SchemaGenerator.TypeOverwrites.File())
                overwrite(SchemaOverwriteModule(
                    identifier = "kotlinx.datetime.LocalDateTime",
                    schema = {
                        Schema<Any>().also {
                            it.types = setOf("string")
                            it.format = "date-time"
                            it.example = "2025-01-15T10:30:00"
                        }
                    }
                ))
                overwrite(SchemaOverwriteModule(
                    identifier = "kotlinx.datetime.LocalDate",
                    schema = {
                        Schema<Any>().also {
                            it.types = setOf("string")
                            it.format = "date"
                            it.example = "2025-01-15"
                        }
                    }
                ))
            }
        }
    }
}