package example.cash_card;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository interface for CashCard entities.
 * This interface extends CrudRepository to provide CRUD operations and
 * PagingAndSortingRepository to support pagination and sorting for CashCard entities.
 */
interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
}
