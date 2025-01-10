package com.charitan.statement.donation

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class DonationStatementService {
    fun getStatementPdf(id: Long): ByteArray = generatePdf()

    fun generatePdf(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val document = PDDocument()
        val page = PDPage()

        document.addPage(page)

        PDPageContentStream(document, page).run {
            setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 28F)
            beginText()
            newLineAtOffset(24F, page.mediaBox.height - 52F)
            showText("Donation Statement")
            endText()
            close()
        }

        document.save(outputStream)
        document.close()
        return outputStream.toByteArray()
    }
}
