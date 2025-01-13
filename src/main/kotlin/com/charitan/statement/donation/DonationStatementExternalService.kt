package com.charitan.statement.donation

import ace.charitan.common.dto.donation.DonationDto
import ace.charitan.common.dto.project.ExternalProjectDto

interface DonationStatementExternalService {
    suspend fun getStatementPdf(id: Long): ByteArray

    fun generatePdf(
        donation: DonationDto,
        project: ExternalProjectDto,
    ): ByteArray
}
