package io.github.alstn113.app.distributedlock.pubsub

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.Topic
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter

@Configuration
class RedisPubSubConfig {

    @Bean
    fun lockNotificationListenerAdapter(
        subscriber: LockNotificationSubscriber,
    ): MessageListenerAdapter {
        return MessageListenerAdapter(subscriber)
    }

    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
        lockNotificationListenerAdapter: MessageListenerAdapter,
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)

        val topic: Topic = PatternTopic(LockNotificationSubscriber.NOTIFY_PATTERN)
        container.addMessageListener(lockNotificationListenerAdapter, topic)

        return container
    }
}