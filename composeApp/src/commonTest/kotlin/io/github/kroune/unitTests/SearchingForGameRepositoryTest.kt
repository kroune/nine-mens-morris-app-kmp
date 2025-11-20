package io.github.kroune.unitTests

import io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame.SearchingForGameRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.searchingForGame.SearchingForGameRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SearchingForGameRepositoryTest {

    private class MockJwtTokenRepository(private val token: String?) : JwtTokenRepositoryI {
        override fun logout() {}
        override fun getJwtToken(): String? = token
        override suspend fun checkJwtToken() = throw NotImplementedError()
        override fun updateJwtToken(newJwtToken: String) {}
    }

    private class MockSearchingForGameRemoteDataSource(
        private val events: List<SearchingForGameEvent>
    ) : SearchingForGameRemoteDataSourceI {
        override fun connect(jwtToken: String): Flow<SearchingForGameEvent> {
            return flowOf(*events.toTypedArray())
        }
    }

    @Test
    fun `searchForGame returns flow of searching events`() = runTest {
        val events = listOf(
            SearchingForGameEvent.Success.NewExpectedWaitingTime(expectedWaitingTime = 5000L),
            SearchingForGameEvent.Success.GameFound(gameId = 12345L)
        )

        val repository = SearchingForGameRepositoryImpl(
            searchingForGameRemoteDataSource = MockSearchingForGameRemoteDataSource(events),
            jwtTokenRepository = MockJwtTokenRepository("test-token")
        )

        val result = repository.searchForGame().toList()

        assertEquals(2, result.size)
        assertTrue(result[0] is SearchingForGameEvent.Success.NewExpectedWaitingTime)
        assertEquals(5000L, (result[0] as SearchingForGameEvent.Success.NewExpectedWaitingTime).expectedWaitingTime)
        assertEquals(12345L, (result[1] as SearchingForGameEvent.Success.GameFound).gameId)
    }

    @Test
    fun `searchForGame throws exception when jwt token is null`() {
        val repository = SearchingForGameRepositoryImpl(
            searchingForGameRemoteDataSource = MockSearchingForGameRemoteDataSource(emptyList()),
            jwtTokenRepository = MockJwtTokenRepository(null)
        )

        assertFailsWith<IllegalArgumentException> {
            repository.searchForGame()
        }
    }

    @Test
    fun `searchForGame handles empty event stream`() = runTest {
        val repository = SearchingForGameRepositoryImpl(
            searchingForGameRemoteDataSource = MockSearchingForGameRemoteDataSource(emptyList()),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val events = repository.searchForGame().toList()
        assertEquals(0, events.size)
    }

    @Test
    fun `searchForGame handles game found event`() = runTest {
        val events = listOf(
            SearchingForGameEvent.Success.GameFound(gameId = 99999L)
        )

        val repository = SearchingForGameRepositoryImpl(
            searchingForGameRemoteDataSource = MockSearchingForGameRemoteDataSource(events),
            jwtTokenRepository = MockJwtTokenRepository("valid-token")
        )

        val result = repository.searchForGame().toList()

        assertEquals(1, result.size)
        assertEquals(99999L, (result[0] as SearchingForGameEvent.Success.GameFound).gameId)
    }

    @Test
    fun `searchForGame handles expected waiting time updates`() = runTest {
        val events = listOf(
            SearchingForGameEvent.Success.NewExpectedWaitingTime(expectedWaitingTime = 1000L),
            SearchingForGameEvent.Success.NewExpectedWaitingTime(expectedWaitingTime = 2000L),
            SearchingForGameEvent.Success.NewExpectedWaitingTime(expectedWaitingTime = 3000L)
        )

        val repository = SearchingForGameRepositoryImpl(
            searchingForGameRemoteDataSource = MockSearchingForGameRemoteDataSource(events),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.searchForGame().toList()

        assertEquals(3, result.size)
        result.forEachIndexed { index, event ->
            assertTrue(event is SearchingForGameEvent.Success.NewExpectedWaitingTime)
            assertEquals((index + 1) * 1000L, (event as SearchingForGameEvent.Success.NewExpectedWaitingTime).expectedWaitingTime)
        }
    }

    @Test
    fun `searchForGame handles network error event`() = runTest {
        val events = listOf(
            SearchingForGameEvent.Success.NewExpectedWaitingTime(expectedWaitingTime = 5000L),
            SearchingForGameEvent.Error.NetworkError
        )

        val repository = SearchingForGameRepositoryImpl(
            searchingForGameRemoteDataSource = MockSearchingForGameRemoteDataSource(events),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.searchForGame().toList()

        assertEquals(2, result.size)
        assertTrue(result[1] is SearchingForGameEvent.Error.NetworkError)
    }

    @Test
    fun `searchForGame handles server error event`() = runTest {
        val events = listOf(
            SearchingForGameEvent.Error.ServerError
        )

        val repository = SearchingForGameRepositoryImpl(
            searchingForGameRemoteDataSource = MockSearchingForGameRemoteDataSource(events),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.searchForGame().toList()

        assertEquals(1, result.size)
        assertTrue(result[0] is SearchingForGameEvent.Error.ServerError)
    }

    @Test
    fun `searchForGame works with different game ids`() = runTest {
        val events = listOf(
            SearchingForGameEvent.Success.GameFound(gameId = 1L),
            SearchingForGameEvent.Success.GameFound(gameId = 1000000L)
        )

        val repository = SearchingForGameRepositoryImpl(
            searchingForGameRemoteDataSource = MockSearchingForGameRemoteDataSource(events),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.searchForGame().toList()

        assertEquals(2, result.size)
        assertEquals(1L, (result[0] as SearchingForGameEvent.Success.GameFound).gameId)
        assertEquals(1000000L, (result[1] as SearchingForGameEvent.Success.GameFound).gameId)
    }
}

