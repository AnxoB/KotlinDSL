import kotlinx.html.*
import kotlinx.html.stream.createHTML
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val address: Address,
    val birthDate: String,
    val company: Company,
    val email: String,
    val firstname: String,
    val id: Int,
    val lastname: String,
    val login: Login,
    val phone: String,
    val website: String
)
@Serializable
data class Address(
    val city: String,
    val geo: Geo,
    val street: String,
    val suite: String,
    val zipcode: String
)
@Serializable
data class Company(
    val bs: String,
    val catchPhrase: String,
    val name: String
)
@Serializable
data class Login(
    val md5: String,
    val password: String,
    val registered: String,
    val sha1: String,
    val username: String,
    val uuid: String
)
@Serializable
data class Geo(
    val lat: String,
    val lng: String
)

fun main() {

    // Crear cliente HTTP
    val client = HttpClient.newHttpClient()

    // Crear solicitud
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://jsonplaceholder.org/users"))
        .GET()
        .build()

    // Enviar la solicitud con el cliente
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    // Obtener string con datos
    val jsonBody = response.body()

    // Deserializar el JSON a una lista de objetos User
    val users: List<User> = Json.decodeFromString(jsonBody)

    // Crear contenido HTML
    val htmlContent = createHTML().html {
        head {
            title("Lista de Usuarios")
        }
        body {
            h1 { +"Lista de usuarios con dirección y coordenadas:" }
            table {
                attributes["border"] = "1"
                tr {
                    th { +"Nombre" }
                    th { +"Email" }
                    th { +"Dirección" }
                    th { +"Coordenadas" }
                }
                users.forEach { user ->
                    tr {
                        td { +"${user.firstname} ${user.lastname}" }
                        td { +user.email }
                        td { +"${user.address.street}, ${user.address.city}, ${user.address.zipcode}" }
                        td { +"(${user.address.geo.lat}, ${user.address.geo.lng})" }
                    }
                }
            }

            h1 { +"Lista de usuarios ordenados alfabéticamente por nombre:" }
            table {
                attributes["border"] = "1"
                tr {
                    th { +"Nombre" }
                    th { +"Email" }
                    th { +"Dirección" }
                    th { +"Coordenadas" }
                }
                users.sortedBy { it.firstname }.forEach { user ->
                    tr {
                        td { +"${user.firstname} ${user.lastname}" }
                        td { +user.email }
                        td { +"${user.address.street}, ${user.address.city}, ${user.address.zipcode}" }
                        td { +"(${user.address.geo.lat}, ${user.address.geo.lng})" }
                    }
                }
            }

            h1 { +"Usuarios que viven en 789 Maple Street:" }
            table {
                attributes["border"] = "1"
                tr {
                    th { +"Nombre" }
                    th { +"Email" }
                    th { +"Dirección" }
                    th { +"Coordenadas" }
                }
                users.filter { it.address.street == "789 Maple Street" }.forEach { user ->
                    tr {
                        td { +"${user.firstname} ${user.lastname}" }
                        td { +user.email }
                        td { +"${user.address.street}, ${user.address.city}, ${user.address.zipcode}" }
                        td { +"(${user.address.geo.lat}, ${user.address.geo.lng})" }
                    }
                }
            }

            h1 { +"Nombres completos de todos los usuarios:" }
            ul {
                users.map { "${it.firstname} ${it.lastname}" }.forEach { nombre ->
                    li { +nombre }
                }
            }
        }
    }

    // Escribir contenido HTML a un archivo
    val file = File("users.html")
    file.writeText(htmlContent)
}