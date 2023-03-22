package quiz.game.trivia.data.local.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import quiz.game.trivia.GamePreference
import quiz.game.trivia.domain.DEFAULT_QUESTIONS_COUNT
import quiz.game.trivia.domain.MAX_ENERGY
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamePreferenceSerializer @Inject constructor() : Serializer<GamePreference> {
    override val defaultValue: GamePreference = GamePreference.getDefaultInstance()
        .toBuilder()
        .setEnergy(MAX_ENERGY)
        .setQuestionsCount(DEFAULT_QUESTIONS_COUNT)
        .build()

    override suspend fun readFrom(input: InputStream): GamePreference {
        try {
            return GamePreference.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: GamePreference, output: OutputStream) =
        t.writeTo(output)
}