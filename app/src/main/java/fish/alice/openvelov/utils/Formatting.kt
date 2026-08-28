package fish.alice.openvelov.utils

data class StationName(val name: String, val number: String)

fun extractStationName(name: String): StationName {
    val regex = Regex("^(\\d+)\\s+-\\s+(.*)$")
    val matchResult = regex.find(name)

    return if (matchResult != null) {
        val (stationNumber, stationName) = matchResult.destructured
        StationName(name = stationName, number = stationNumber)
    } else {
        StationName(name = name, number = "")
    }
}