package example.cash_card;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * REST controller for managing cash cards.
 * Provides an endpoint to retrieve cash cards details by ID.
 */
@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    /**
     * Constructs a new CashCardController with the specified repository.
     *
     * @param cashCardRepository the repository used to manage cash cards
     */
    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    /**
     * Handles GET requests to retrieve cash card by its ID.
     *
     * @param requestedId the ID of the requested cash card
     * @return ResponseEntity containing the cash card if found, or a 404 Not Found status if not found
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {

        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);

        if (cashCardOptional.isPresent()) {
            // Return the found CashCard with a 200 OK status
            return ResponseEntity.ok(cashCardOptional.get());
        }
        else {
            // Return a 404 Not Found response if the cash card is not found
            return ResponseEntity.notFound().build();
        }
    }
}
