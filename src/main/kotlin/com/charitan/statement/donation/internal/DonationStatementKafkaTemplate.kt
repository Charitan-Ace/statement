package com.charitan.statement.donation.internal

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import org.springframework.kafka.support.KafkaHeaders.REPLY_TOPIC

@Configuration
internal class DonationStatementKafkaTemplate {
    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, Any>): KafkaTemplate<String, Any> = KafkaTemplate(producerFactory)

    @Bean
    fun replyingTemplate(
        producerFactory: ProducerFactory<String, Any>,
        repliesContainer: ConcurrentMessageListenerContainer<String, Any>,
    ): ReplyingKafkaTemplate<String, Any, Any> = ReplyingKafkaTemplate(producerFactory, repliesContainer)

    @Bean
    fun repliesContainer(
        containerFactory: ConcurrentKafkaListenerContainerFactory<String, Any>,
    ): ConcurrentMessageListenerContainer<String, Any> {
        val repliesContainer = containerFactory.createContainer(REPLY_TOPIC)
        repliesContainer.containerProperties.setGroupId("statement-service")
        repliesContainer.isAutoStartup = true
        return repliesContainer
    }
}
