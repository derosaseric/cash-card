package example.cash_card;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing cash cards.
 * Provides an endpoint to retrieve cash cards details by ID.
 */
@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    /**
     * Handles GET requests to retrieve cash card by its ID.
     *
     * @param requestedId the ID of the requested cash card
     * @return ResponseEntity containing the cash card if found, or a 404 Not Found status if not found
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {

        // Check if the requested ID matches the predefined cash card ID
        if (requestedId.equals(99L)) {
            // Create a cash card with the predefined ID and amount
            CashCard cashCard = new CashCard(99L, 123.45);

            // Return the cash card wrapped in a 200 OK response
            return ResponseEntity.ok(cashCard);
        }
        else {
            // Return a 404 Not Found response if the cash card is not found
            return ResponseEntity.notFound().build();
        }
    }
}
