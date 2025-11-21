package io.github.kroune.unitTests

import com.kroune.nineMensMorrisLib.gameStartPosition
import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.OnlineGameRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.GameEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.onlineGame.OnlineGameRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OnlineGameRepositoryTest {

    private class MockJwtTokenRepository(private val token: String?) : JwtTokenRepositoryI {
        override fun logout() {}
        override fun getJwtToken(): String? = token
        override suspend fun checkJwtToken() = TODO()
        override fun updateJwtToken(newJwtToken: String) {}
    }

    private class MockOnlineGameRemoteDataSource(
        private val events: List<GameEvent>
    ) : OnlineGameRemoteDataSourceI {
        override fun connect(
            gameId: Long,
            jwtToken: String,
            channelToSendMoves: Flow<Movement>
        ): Flow<GameEvent> {
            return flowOf(*events.toTypedArray())
        }
    }

    @Test
    fun `connect returns flow of game events`() = runTest {
        val gameEvents = listOf(
            GameEvent.Success.GameInfo(isGreen = true, startPosition = gameStartPosition, enemyId = 456L),
            GameEvent.Success.Move(Movement(null, 0)),
            GameEvent.Success.Move(Movement(null, 1))
        )

        val repository = OnlineGameRepositoryImpl(
            jwtTokenRepository = MockJwtTokenRepository("test-token"),
            onlineGameRemoteDataSource = MockOnlineGameRemoteDataSource(gameEvents),
        )

        val movesFlow = flowOf(Movement(null, 5))
        val events = repository.connect(123L, movesFlow).toList()

        assertEquals(3, events.size)
        assertTrue(events[0] is GameEvent.Success.GameInfo)
        assertEquals(true, (events[0] as GameEvent.Success.GameInfo).isGreen)
    }

    @Test
    fun `connect passes jwt token to remote data source`() = runTest {
        val repository = OnlineGameRepositoryImpl(
            jwtTokenRepository = MockJwtTokenRepository("my-jwt-token"),
            onlineGameRemoteDataSource = MockOnlineGameRemoteDataSource(emptyList())
        )

        val movesFlow = flowOf<Movement>()
        val events = repository.connect(456L, movesFlow).toList()

        assertEquals(0, events.size)
    }

    @Test
    fun `connect handles empty event stream`() = runTest {
        val repository = OnlineGameRepositoryImpl(
            jwtTokenRepository = MockJwtTokenRepository("token"),
            onlineGameRemoteDataSource = MockOnlineGameRemoteDataSource(emptyList())
        )

        val events = repository.connect(789L, flowOf()).toList()
        assertEquals(0, events.size)
    }

    @Test
    fun `connect handles game ended event`() = runTest {
        val gameEvents = listOf(
            GameEvent.Success.GameInfo(isGreen = true, startPosition = gameStartPosition, enemyId = 123L),
            GameEvent.Success.GameEnded
        )

        val repository = OnlineGameRepositoryImpl(
            jwtTokenRepository = MockJwtTokenRepository("token"),
            onlineGameRemoteDataSource = MockOnlineGameRemoteDataSource(gameEvents)
        )

        val events = repository.connect(999L, flowOf()).toList()

        assertEquals(2, events.size)
        assertEquals(GameEvent.Success.GameEnded, events[1])
    }

    @Test
    fun `connect handles error events`() = runTest {
        val gameEvents = listOf(
            GameEvent.Success.GameInfo(isGreen = false, startPosition = gameStartPosition, enemyId = 789L),
            GameEvent.Error.NetworkError
        )

        val repository = OnlineGameRepositoryImpl(
            jwtTokenRepository = MockJwtTokenRepository("token"),
            onlineGameRemoteDataSource = MockOnlineGameRemoteDataSource(gameEvents)
        )

        val events = repository.connect(111L, flowOf()).toList()

        assertEquals(2, events.size)
        assertEquals(GameEvent.Error.NetworkError, events[1])
    }

    @Test
    fun `connect handles server error`() = runTest {
        val gameEvents = listOf(
            GameEvent.Error.ServerError
        )

        val repository = OnlineGameRepositoryImpl(
            jwtTokenRepository = MockJwtTokenRepository("token"),
            onlineGameRemoteDataSource = MockOnlineGameRemoteDataSource(gameEvents)
        )

        val events = repository.connect(222L, flowOf()).toList()

        assertEquals(1, events.size)
        assertEquals(GameEvent.Error.ServerError, events[0])
    }
}
