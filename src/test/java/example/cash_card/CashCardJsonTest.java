package example.cash_card;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test class verifies the JSON serialization and deserialization of the CashCard class.
 * It uses the Jackson library to convert between Java objects and JSON.
 */
@JsonTest
public class CashCardJsonTest {

    @Autowired
    private JacksonTester<CashCard> json;

    @Autowired
    private JacksonTester<CashCard[]> jsonList;

    private CashCard[] cashCards;

    @BeforeEach
    void setUp() {
        // Initialize an array of CashCard objects for testing
        cashCards = Arrays.array(
                new CashCard(99L, 123.45),
                new CashCard(100L, 1.00),
                new CashCard(101L, 150.00)
        );
    }

    /**
     * Test the serialization of a CashCard object to JSON.
     * This test ensures that the CashCard object is correctly serialized to the expected JSON format.
     *
     * @throws IOException if there is an error during the serialization process.
     */
    @Test
    void cashCardSerializationTest() throws IOException {
        // Create a CashCard object with specific values
        CashCard cashCard = new CashCard(99L, 123.45);

        // Assert that the serialized JSON matches the expected JSON file
        assertThat(json.write(cashCard)).isStrictlyEqualToJson("single.json");

        // Assert that the serialized JSON contains an "id" field with the expected value
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.id").isEqualTo(99);

        // Assert that the serialized JSON contains an "amount" field with the expected value
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount").isEqualTo(123.45);
    }

    /**
     * Test the deserialization of a JSON string to a CashCard object.
     * This test ensures that the JSON string is correctly deserialized into a CashCard object with the expected values.
     *
     * @throws IOException if there is an error during the deserialization process.
     */
    @Test
    void cashCardDeserializationTest() throws IOException {
        // Define a JSON string representing a CashCard object
        String expectedJson = """
                {
                    "id": "99",
                    "amount": "123.45"
                }
                """;

        // Assert that the deserialized CashCard object matches the expected values
        assertThat(json.parse(expectedJson)).isEqualTo(new CashCard(99L, 123.45));
        assertThat(json.parseObject(expectedJson).id()).isEqualTo(99);
        assertThat(json.parseObject(expectedJson).amount()).isEqualTo(123.45);
    }

    /**
     * Test the serialization of a list of CashCard objects to JSON.
     * This test ensures that the list of CardCard objects is correctly serialized to the expected JSON format.
     *
     * @throws IOException if there is an error during the serialization process.
     */
    @Test
    void cashCardListSerializationTest() throws IOException {
        // Assert that the serialized JSON matches the expected JSON file for a list of CashCards
        assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
    }

    /**
     * Test the deserialization of a JSON string to a list of CashCard objects.
     * This test ensures that the JSON string is correctly deserialized into a list of CashCard objects with the expected values.
     *
     * @throws IOException if there is an error during the deserialization process.
     */
    @Test
    void cashCardListDeserializationTest() throws IOException {
        // Define a JSON string representing a list of CashCard objects
        String expected= """
                [
                    { "id": 99, "amount": 123.45},
                    { "id": 100, "amount": 1.00},
                    { "id": 101, "amount": 150.00}
                ]
                """;

        // Assert that the deserialized list of CashCard objects matches the expected values
        assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
    }
}
