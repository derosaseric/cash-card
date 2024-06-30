package example.cash_card;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Repository interface for CashCard entities.
 * This interface extends CrudRepository to provide CRUD operations and
 * PagingAndSortingRepository to support pagination and sorting for CashCard entities.
 */
interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {

    /**
     * Finds a CashCard by its ID and owner.
     *
     * @param id the ID of the CashCard
     * @param owner the owner of the CashCard
     * @return the CashCard entity if found, otherwise null
     */
    CashCard findByIdAndOwner(Long id, String owner);

    /**
     * Finds a page of CashCards by their owners.
     *
     * @param owner the owner of the CashCards
     * @param pageRequest the pagination and sorting information
     * @return a page of CashCards owned by the specified owner
     */
    Page<CashCard> findByOwner(String owner, PageRequest pageRequest);
}
