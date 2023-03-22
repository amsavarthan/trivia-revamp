package quiz.game.trivia.data.local.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import quiz.game.trivia.UserPreference
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreference> {
    override val defaultValue: UserPreference = UserPreference.getDefaultInstance()
        .toBuilder()
        .setIsEnergyAlertEnabled(true)
        .build()

    override suspend fun readFrom(input: InputStream): UserPreference {
        try {
            return UserPreference.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserPreference, output: OutputStream) =
        t.writeTo(output)
}