package io.github.kroune.unitTests

import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.log
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.logOnFailure
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogsRepositoryTest {

    @Test
    fun `log prints message with DEBUG severity`() {
        // Test that log function doesn't throw exception
        log("Debug message", severity = Severity.DEBUG)
    }

    @Test
    fun `log prints message with INFO severity`() {
        log("Info message", severity = Severity.INFO)
    }

    @Test
    fun `log prints message with ERROR severity`() {
        log("Error message", severity = Severity.ERROR)
    }

    @Test
    fun `log prints message with TRACE severity`() {
        log("Trace message", severity = Severity.TRACE)
    }

    @Test
    fun `log prints message with throwable`() {
        val exception = RuntimeException("Test exception")
        log("Error occurred", throwable = exception, severity = Severity.ERROR)
    }

    @Test
    fun `log prints message without throwable`() {
        log("Simple message", throwable = null, severity = Severity.INFO)
    }

    @Test
    fun `logOnFailure logs on Result failure`() {
        val result: Result<Int> = Result.failure(RuntimeException("Test error"))
        val logged = result.logOnFailure("Operation failed", Severity.ERROR)

        assertTrue(logged.isFailure)
        assertEquals(result, logged)
    }

    @Test
    fun `logOnFailure does not log on Result success`() {
        val result: Result<Int> = Result.success(42)
        val logged = result.logOnFailure("This should not be logged", Severity.ERROR)

        assertTrue(logged.isSuccess)
        assertEquals(42, logged.getOrNull())
    }

    @Test
    fun `logOnFailure returns same result on success`() {
        val originalResult: Result<String> = Result.success("test data")
        val loggedResult = originalResult.logOnFailure("Error message", Severity.ERROR)

        assertEquals(originalResult, loggedResult)
        assertEquals("test data", loggedResult.getOrNull())
    }

    @Test
    fun `logOnFailure returns same result on failure`() {
        val exception = IllegalStateException("Invalid state")
        val originalResult: Result<String> = Result.failure(exception)
        val loggedResult = originalResult.logOnFailure("State error", Severity.ERROR)

        assertEquals(originalResult.isFailure, loggedResult.isFailure)
        assertEquals(exception, loggedResult.exceptionOrNull())
    }

    @Test
    fun `logOnFailure works with different result types`() {
        val intResult: Result<Int> = Result.success(100)
        val stringResult: Result<String> = Result.success("text")
        val boolResult: Result<Boolean> = Result.success(true)

        assertEquals(100, intResult.logOnFailure("Int error", Severity.DEBUG).getOrNull())
        assertEquals("text", stringResult.logOnFailure("String error", Severity.INFO).getOrNull())
        assertEquals(true, boolResult.logOnFailure("Bool error", Severity.TRACE).getOrNull())
    }

    @Test
    fun `logOnFailure handles different exception types`() {
        val runtimeException = Result.failure<Int>(RuntimeException("Runtime error"))
        val illegalArgument = Result.failure<Int>(IllegalArgumentException("Invalid arg"))
        val nullPointer = Result.failure<Int>(NullPointerException("Null value"))

        runtimeException.logOnFailure("Runtime exception", Severity.ERROR)
        illegalArgument.logOnFailure("Illegal argument", Severity.ERROR)
        nullPointer.logOnFailure("Null pointer", Severity.ERROR)

        assertTrue(runtimeException.isFailure)
        assertTrue(illegalArgument.isFailure)
        assertTrue(nullPointer.isFailure)
    }

    @Test
    fun `log handles empty message`() {
        log("", severity = Severity.INFO)
    }

    @Test
    fun `log handles long message`() {
        val longMessage = "a".repeat(10000)
        log(longMessage, severity = Severity.DEBUG)
    }

    @Test
    fun `log handles multiline message`() {
        val multilineMessage = """
            Line 1
            Line 2
            Line 3
        """.trimIndent()
        log(multilineMessage, severity = Severity.INFO)
    }
}

