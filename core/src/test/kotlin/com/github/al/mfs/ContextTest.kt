package com.github.al.mfs

import com.github.al.mfs.io.Splitter
import com.github.al.mfs.pipeline.Collector
import com.github.al.mfs.pipeline.OutputPipeline
import com.github.al.mfs.sender.SequentialSenderOrchestrator
import io.micronaut.context.ApplicationContext
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class ContextTest {

    @Test
    internal fun name() {
        ApplicationContext.run().let { ctx ->
            val splitter = ctx.getBean(Splitter::class.java)
            println(splitter)
        }
    }

    @Test
    internal fun name2() {
        ApplicationContext.run(mapOf("splitter.count.fixed" to "10MB")).let { ctx ->
            val splitter = ctx.getBean(Splitter::class.java)
            println(splitter)
        }
    }

    @Test
    internal fun name3() {
        ApplicationContext.run(
            mapOf(
                "splitter.count.random.lower" to "17MB",
                "splitter.count.random.upper" to "20MB",
            )
        ).let { ctx ->
            val splitter = ctx.getBean(Splitter::class.java)
            println(splitter)
        }
    }

    @Test
    internal fun name4() {
        ApplicationContext.run().let { ctx ->
            val collector = ctx.getBean(Collector::class.java)
            println(collector)
        }
    }

    @Test
    internal fun name5() {
        ApplicationContext.run(mapOf("pipeline.output.zip" to "")).let { ctx ->
            val collector = ctx.getBean(OutputPipeline::class.java)
            println(collector)
        }
    }

    @Test
    internal fun name6() {
        ApplicationContext.run(
            mapOf(
                "ews.url" to "a",
                "ews.user" to "a",
                "ews.pass" to "a"
            )
        ).let { ctx ->
            val collector = ctx.getBean(SequentialSenderOrchestrator::class.java)
            println(collector)
        }
    }
}
