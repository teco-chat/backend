package chat.teco.tecochat.support.test

import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.Spec
import io.kotest.core.test.isRootTest

fun Spec.afterRootTest(f: AfterTest) {
    afterTest {
        val (testcase) = it
        if (testcase.isRootTest()) f(it)
    }
}
