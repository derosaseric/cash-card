package example.cash_card;

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
        assertThat(json.write(cashCard)).isStrictlyEqualToJson("expected.json");

        // Assert that the serialized JSON contains an "id" field with the expected value
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(99);

        // Assert that the serialized JSON contains an "amount" field with the expected value
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount")
                .isEqualTo(123.45);
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
}
