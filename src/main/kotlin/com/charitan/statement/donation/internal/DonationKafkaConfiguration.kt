package com.charitan.statement.donation.internal

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate

@Configuration
internal class DonationKafkaConfiguration {
    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Any>,
        replyingKafkaTemplate: ReplyingKafkaTemplate<String, Any, Any>,
    ) = ConcurrentKafkaListenerContainerFactory<String, Any>().apply {
        setReplyTemplate(replyingKafkaTemplate)
        this.consumerFactory = consumerFactory
    }

    @Bean
    fun replyContainer(consumerFactory: ConsumerFactory<String, Any>) =
        ConcurrentKafkaListenerContainerFactory<String, Any>()
            .apply {
                this.consumerFactory = consumerFactory
            }.createContainer("donation.replies")
            .apply {
                isAutoStartup = true
            }

    @Bean
    fun replyingKafkaTemplate(
        producerFactory: ProducerFactory<String, Any>,
        replyContainer: ConcurrentMessageListenerContainer<String, Any>,
    ) = ReplyingKafkaTemplate(producerFactory, replyContainer)
}
