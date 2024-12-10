package utez.edu.mx

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val name: String
)

data class Main(
    val temp: Double, // Temperatura actual
    val temp_min: Double, // Temperatura mínima
    val temp_max: Double // Temperatura máxima
)

data class Weather(
    val description: String, // Descripción del clima (e.g., "clear sky")
    val icon: String // Icono representativo
)
