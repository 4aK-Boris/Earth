package ru.mpei.earth

import org.junit.Test

import org.junit.Assert.*
import kotlin.math.PI
import kotlin.math.cos

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val equationSystem = EquationSystem()
        val result = equationSystem.solve(
            speed = 10f,
            angle = 45f,
            height = 0f,
            windSpeed = 0f,
            c = 0.1f,
            m = 1f,
            s = 0.01f
        )
        println(result.last())
    }
}