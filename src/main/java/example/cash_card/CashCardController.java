package example.cash_card;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.security.Principal;
import java.net.URI;
import java.util.List;

/**
 * REST controller for managing cash cards.
 * Provides an endpoint to create, retrieve by ID, update, delete, and list cash cards.
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
     * Retrieves a cash card by its ID and ensures it belongs to the authenticated user.
     *
     * @param requestedId the ID of the requested cash card
     * @param principal the authenticated user's principal
     * @return the CashCard if found, otherwise null
     */
    private CashCard findCashCard(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    /**
     * Handles POST requests to create a new cash card.
     * Associates the created cash card with the currently authenticated user.
     *
     * @param newCashCardRequest the details of the cash card to be created
     * @param ucb the UriComponentsBuilder to build the URI of the created resource
     * @param principal the authenticated user's principal
     * @return ResponseEntity with the location of the created cash card
     */
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());

        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);

        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();

        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    /**
     * Handles GET requests to retrieve cash card by its ID.
     * Ensures the retrieved cash card belongs to the currently authenticated user.
     *
     * @param requestedId the ID of the requested cash card
     * @param principal the authenticated user's principal
     * @return ResponseEntity containing the cash card if found, or a 404 Not Found status if not found
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);

        if (cashCard != null) {
            // Return the found CashCard with a 200 OK status
            return ResponseEntity.ok(cashCard);
        }
        else {
            // Return a 404 Not Found response if the cash card is not found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Handles GET requests to retrieve all cash cards with pagination and sorting.
     * Only retrieves cash cards that belong to the currently authenticated user.
     *
     * @param pageable the pagination and sorting information
     * @param principal the authenticated user's principal
     * @return ResponseEntity containing a list of cash cards
     */
    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
            ));

        return ResponseEntity.ok(page.getContent());
    }

    /**
     * Handles PUT requests to update an existing cash card.
     * Ensures the updated cash card belongs to the authenticated user.
     *
     * @param requestedId the ID of the cash card to update
     * @param cashCardUpdate the updated details of the cash card
     * @param principal the authenticated user's principal
     * @return ResponseEntity with no content if the update is successful, or a 404 Not Found status if not found
     */
    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);

        if (cashCard != null) {
            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Handles DELETE requests to delete a cash card by its ID.
     * Ensures the cash card to be deleted belongs to the authenticated user.
     *
     * @param id the ID of the cash card to delete
     * @param principal the authenticated user's principal
     * @return ResponseEntity with no content if the deletion is successful, or a 404 Not Found status if not found
     */
    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
        if (cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            cashCardRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
