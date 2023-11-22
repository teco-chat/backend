package chat.teco.tecochat.ui

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {

    @GetMapping("/")
    fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("HELLO WORLD!")
    }
}
