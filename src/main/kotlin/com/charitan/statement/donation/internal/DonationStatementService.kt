package com.charitan.statement.donation.internal

import ace.charitan.common.dto.donation.DonationDto
import ace.charitan.common.dto.donation.GetDonationByIdDto
import ace.charitan.common.dto.project.ExternalProjectDto
import ace.charitan.common.dto.project.GetProjectByIdDto
import com.charitan.statement.donation.DonationStatementExternalService
import com.lowagie.text.Document
import com.lowagie.text.FontFactory
import com.lowagie.text.Image
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.util.Currency

@Service
internal class DonationStatementService(
    @Value("classpath:logo.png")
    private val resource: Resource,
    private val producerService: DonationProducerService,
) : DonationStatementExternalService {
    override suspend fun getStatementPdf(id: Long): ByteArray {
        val donation = producerService.sendReplying(GetDonationByIdDto(id))
        val project = producerService.sendReplying(GetProjectByIdDto(donation.projectId))

        return generatePdf(donation, project)
    }

    override fun generatePdf(
        donation: DonationDto,
        project: ExternalProjectDto,
    ): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val document = Document()
        val logo = Image.getInstance(resource.contentAsByteArray)
        val format =
            NumberFormat.getInstance().apply {
                maximumFractionDigits = 2
                currency = Currency.getInstance("USD")
            }

        PdfWriter.getInstance(document, outputStream)

        document.open()

        document.apply {
            addTitle("${project.title} Statement")
            pageSize = PageSize.A4
            add(logo)
            add(
                Paragraph(
                    "Donation Statement",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24f, Color(0x00, 0x00, 0x00)),
                ),
            )
            add(Paragraph(project.title, FontFactory.getFont(FontFactory.HELVETICA_BOLD)))
            add(Paragraph(project.description))
            add(Paragraph())
            add(Paragraph("Status: ${project.statusType}"))
            add(Paragraph("Message: ${donation.message}"))
            add(Paragraph("Amount: \$${format.format(donation.amount)}"))
            newPage()
        }

        document.close()
        return outputStream.toByteArray()
    }
}
