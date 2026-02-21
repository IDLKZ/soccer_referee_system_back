package kz.kff

import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.*
import kz.kff.core.config.AppEnvironmentConfig
import kz.kff.core.config.StorageConfig
import kz.kff.core.config.SwaggerConfig
import kz.kff.presentation.http.auth.AuthController
import kz.kff.presentation.http.auth_session.AuthSessionController
import kz.kff.presentation.http.file.FileController
import kz.kff.presentation.http.permission.PermissionController
import kz.kff.presentation.http.role.RoleController
import kz.kff.presentation.http.role_permission.RolePermissionController
import kz.kff.presentation.http.shared.HomeController
import kz.kff.presentation.http.user.UserController
import java.nio.file.Paths

fun Application.configureRouting(envConfig: AppEnvironmentConfig, swaggerConfig: SwaggerConfig,storageConfig: StorageConfig) {
    routing {
        //Swagger
        if (swaggerConfig.enabled) {
            route("api.json") { openApi() }
            route(swaggerConfig.path) { swaggerUI("/api.json") }
        }
        //Home
        HomeController().register(this)
        //Role Controller
        RoleController().register(this)
        //Permission Controller
        PermissionController().register(this)
        //Role-Permission Controller
        RolePermissionController.register(this)
        //File Controller
        FileController().register(this)
        //User Controller
        UserController().register(this)
        //Auth Session Controller
        AuthSessionController().register(this)
        //Auth Controller
        AuthController().register(this)

        //Access to Storage Config
        if(storageConfig.useStorage){
            staticFiles(
                storageConfig.accessDir,
                Paths.get(storageConfig.uploadDir).toAbsolutePath().normalize().toFile()
            )
        }
    }
}
