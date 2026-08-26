package fish.alice.openvelov.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

@Serializable
data class StationDto(
    val id: String,
    val number: Int,
    val contractName: String,
    val name: String,
    val open: Boolean,
    val address: String? = null,
    val location: PositionDto,
    val capacity: CapacityDto,
    val availabilities: AvailabilitiesDto,
    val paymentTerminal: Boolean = false,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class CapacityDto(val main: Int, val overflow: Int)

@Serializable
data class AvailabilitiesDto(val main: StandGroupDto, val overflow: StandGroupDto)

@Serializable
data class StandGroupDto(val stands: Int, val bikes: BikeCountsDto)

@Serializable
data class BikeCountsDto(
    val mechanical: Int,
    val electrical: Int,
    val electricalInternalBattery: Int,
    val electricalRemovableBattery: Int,
)

@Serializable
data class PositionDto(
    val latitude: Double,
    val longitude: Double,
)

@Serializable
class BikeDto

interface BikesApi {
    @Headers("Accept: application/vnd.bikes.v2+json")
    @GET("contracts/lyon/bikes")
    suspend fun bikes(@Query("stationNumber") stationNumber: Int): List<BikeDto>
}

interface StationsApi {
    @Headers("Accept: application/vnd.station.v4+json")
    @GET("contracts/lyon/stations")
    suspend fun stations(): List<StationDto>
}
