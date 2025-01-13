package com.charitan.statement.donation.internal

import ace.charitan.common.dto.donation.DonationDto
import ace.charitan.common.dto.donation.GetDonationByIdDto
import ace.charitan.common.dto.project.ExternalProjectDto
import ace.charitan.common.dto.project.GetProjectByIdDto
import kotlinx.coroutines.future.await
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
internal class DonationProducerService(
    private val replyingKafkaTemplate: ReplyingKafkaTemplate<String, Any, Any>,
) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private suspend fun sendReplying(
        topic: DonationTopic,
        data: Any,
    ): Any {
        if (!replyingKafkaTemplate.waitForAssignment(Duration.ofSeconds(10))) {
            error("Template container hasn't been initialize")
        }

        val record: ProducerRecord<String, Any> = ProducerRecord(topic.topic, data)
        val request = replyingKafkaTemplate.sendAndReceive(record, Duration.ofSeconds(10))

        val send = request.sendFuture.await()
        logger.info("Sent replying future request to ${topic.topic}, metadata ${send.recordMetadata}")

        val result = request.await()
        logger.info("Request to $topic has been replied, value size ${result.serializedValueSize()}")

        return result.value()
    }

    suspend fun sendReplying(getDonationByIdDto: GetDonationByIdDto) =
        sendReplying(DonationTopic.DONATION_GET_ID, getDonationByIdDto) as DonationDto

    suspend fun sendReplying(getProjectByIdDto: GetProjectByIdDto) =
        sendReplying(DonationTopic.PROJECT_GET_ID, getProjectByIdDto) as ExternalProjectDto
}
