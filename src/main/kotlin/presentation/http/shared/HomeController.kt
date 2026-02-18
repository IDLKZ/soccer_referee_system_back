package kz.kff.presentation.http.shared

import io.github.smiley4.ktoropenapi.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import kz.kff.core.shared.utils.api_response.ApiResponseHelper.success
import kz.kff.domain.datasource.filter.okWrapped
import org.koin.core.component.KoinComponent

class HomeController : KoinComponent {
    fun register(route: Route) {
        route.route("/") {
            get({
                tags("Home")
                summary = "Проверка работоспособности"
                description = "Проверить что API запущен и работает"
                response {
                    okWrapped<String>()
                }
            }) {
                call.success(
                    data = "It works!",
                    code = 200,
                )
            }
        }
    }
}
