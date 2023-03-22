package quiz.game.trivia.data.local.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import quiz.game.trivia.SessionTokenPreference
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionTokenPreferenceSerializer @Inject constructor() : Serializer<SessionTokenPreference> {
    override val defaultValue: SessionTokenPreference = SessionTokenPreference.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): SessionTokenPreference {
        try {
            return SessionTokenPreference.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: SessionTokenPreference, output: OutputStream) =
        t.writeTo(output)
}