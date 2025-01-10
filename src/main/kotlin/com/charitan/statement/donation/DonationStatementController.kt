package com.charitan.statement.donation

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/statement/donation")
@RestController
class DonationStatementController(
    private val service: DonationStatementService,
) {
    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<ByteArray> {
        val file = service.getStatementPdf(id)
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"statement-$id.pdf\"")
            .body(file)
    }
}
