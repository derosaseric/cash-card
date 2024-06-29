package example.cash_card;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for CashCard entities.
 * This interface extends CrudRepository to provide CRUD operations for CashCard entities.
 */
interface CashCardRepository extends CrudRepository<CashCard, Long> {
}
