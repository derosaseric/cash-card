package example.cash_card;

/**
 * Represents a cash card with an identifier and an amount.
 * Utilizes Java's record feature for immutability and automatic generation of
 * common methods such as getters, constructors, equals(), hashCode() and toString().
 */
record CashCard(Long id, Double amount) {
}
