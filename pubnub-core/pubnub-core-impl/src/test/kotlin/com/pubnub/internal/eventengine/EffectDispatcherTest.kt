package com.pubnub.internal.eventengine

import com.pubnub.internal.subscribe.eventengine.effect.SubscribeEffectInvocation
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasKey
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledExecutorService

class EffectDispatcherTest {
    private val executorService: ScheduledExecutorService = mockk()

    internal sealed class TestEffectInvocation(override val type: EffectInvocationType) : EffectInvocation {
        override val id: String = this::class.java.simpleName
    }

    internal object TestEffect :
        TestEffectInvocation(Managed)

    internal object ImmediateEndingTestEffect :
        TestEffectInvocation(Managed)

    internal object CancelTestEffect : TestEffectInvocation(Cancel(TestEffect::class.java.simpleName))

    internal class EffectHandlerFactoryImpl : EffectFactory<TestEffectInvocation> {
        override fun create(effectInvocation: TestEffectInvocation): ManagedEffect {
            return object : ManagedEffect {
                override fun runEffect() {
                    when (effectInvocation) {
                        is ImmediateEndingTestEffect -> {
                        }

                        else -> {
                        }
                    }
                }

                override fun cancel() {
                }
            }
        }
    }

    @Test
    fun managedEffectIsNotEvictedTillCancelled() {
        // given
        val managedEffects = ConcurrentHashMap<String, ManagedEffect>()
        val effectDispatcher =
            EffectDispatcher(
                effectFactory = EffectHandlerFactoryImpl(),
                managedEffects = managedEffects,
                effectSource = QueueSinkSource(),
                executorService = executorService,
            )

        // when
        effectDispatcher.dispatch(TestEffect)

        // then
        assertThat(managedEffects, hasKey(TestEffect.id))
    }

    @Test
    fun managedEffectIsEvictedAfterCancel() {
        // given
        val managedEffects = ConcurrentHashMap<String, ManagedEffect>()
        val effectDispatcher =
            EffectDispatcher(
                effectFactory = EffectHandlerFactoryImpl(),
                managedEffects = managedEffects,
                effectSource = QueueSinkSource(),
                executorService = executorService,
            )

        // when
        effectDispatcher.dispatch(TestEffect)
        effectDispatcher.dispatch(CancelTestEffect)

        // then
        assertThat(managedEffects, not(hasKey(TestEffect.id)))
    }

    @Test
    fun canCancelEvictedEffect() {
        // given
        val managedEffects = ConcurrentHashMap<String, ManagedEffect>()
        val effectDispatcher =
            EffectDispatcher(
                effectFactory = EffectHandlerFactoryImpl(),
                managedEffects = managedEffects,
                effectSource = QueueSinkSource(),
                executorService = executorService,
            )

        // when
        effectDispatcher.dispatch(TestEffect)
        effectDispatcher.dispatch(CancelTestEffect)
        effectDispatcher.dispatch(CancelTestEffect)

        // then
        assertThat(managedEffects, not(hasKey(TestEffect.id)))
    }

    @Test
    fun puttingEffectWithSameIdCancelsTheFirstOne() {
        // given
        val managedEffects = ConcurrentHashMap<String, ManagedEffect>()
        val effectHandlerFactory = EffectHandlerFactoryImpl()
        val managedEffect = spyk(effectHandlerFactory.create(TestEffect))
        managedEffects[TestEffect.id] = managedEffect
        val effectDispatcher =
            EffectDispatcher(
                effectFactory = effectHandlerFactory,
                managedEffects = managedEffects,
                effectSource = QueueSinkSource(),
                executorService = executorService,
            )

        // when
        effectDispatcher.dispatch(TestEffect)

        // then
        verify(exactly = 1) { managedEffect.cancel() }
        assertThat(managedEffects, hasKey(TestEffect.id))
    }

    @Test
    fun `can handle NonManaged effect`() {
        // given
        val managedEffects = ConcurrentHashMap<String, ManagedEffect>()
        val emitMessagesInvocation: SubscribeEffectInvocation.EmitMessages =
            SubscribeEffectInvocation.EmitMessages(listOf())
        val effectFactory: EffectFactory<SubscribeEffectInvocation> = mockk()
        val effectDispatcher =
            EffectDispatcher(
                effectFactory = effectFactory,
                managedEffects = managedEffects,
                effectSource = QueueSinkSource(),
                executorService = executorService,
            )
        val effect: Effect = mockk()
        every { effect.runEffect() } returns Unit
        every { effectFactory.create(emitMessagesInvocation) } returns effect

        // when
        effectDispatcher.dispatch(emitMessagesInvocation)

        // then
        verify { effectFactory.create(emitMessagesInvocation) }
        verify { effect.runEffect() }
    }
}
