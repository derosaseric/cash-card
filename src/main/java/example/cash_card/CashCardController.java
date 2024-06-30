package example.cash_card;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.security.Principal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing cash cards.
 * Provides an endpoint to create, retrieve by ID, and list cash cards.
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

        Optional<CashCard> cashCardOptional = Optional.ofNullable(cashCardRepository.findByIdAndOwner(requestedId, principal.getName()));

        if (cashCardOptional.isPresent()) {
            // Return the found CashCard with a 200 OK status
            return ResponseEntity.ok(cashCardOptional.get());
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
}
