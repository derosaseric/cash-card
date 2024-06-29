package example.cash_card;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.net.URI;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Integration tests for the CashCard application.
 * These tests verify the behavior of the REST API endpoints.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	/**
	 * Test to verify that a CashCard with ID 99 is returned correctly when it exists.
	 */
	@Test
	void shouldReturnACashCardWhenDataIsSaved() {
		// Send a GET request to the /cashcards/99 endpoint and store the response
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);

		// Verify that the HTTP status code is 200 OK
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Parse the JSON response body
		DocumentContext documentContext = JsonPath.parse(response.getBody());

		// Extract and verify the 'id' field in the JSON response
		Number id = documentContext.read("$.id");
		assertThat(id).isEqualTo(99);

		// Extract and verify the 'amount' field in the JSON response
		Double amount = documentContext.read("$.amount");
		assertThat(amount).isEqualTo(123.45);
	}

	/**
	 * Test to verify that a 404 Not Found status is returned when a CashCard with an unknown ID is requested.
	 */
	@Test
	void shouldNotReturnACashCardWithAnUnknownId() {
		//Send a GET request to the /cashcards/1000 endpoint and store the response
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);

		// Verify that the HTTP status code is 404 Not Found
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		// Verify that the response body is blank
		assertThat(response.getBody()).isBlank();
	}

	@Test
	void shouldCreateANewCashCard() {
		CashCard newCashCard = new CashCard(null, 250.00);

		// Send a POST request to create the new CashCard and store the response
		ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cashcards", newCashCard, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Retrieve the location of the newly created CashCard
		URI locationOfNewCashCard = createResponse.getHeaders().getLocation();

		// Send a GET request to the location of the newly created CashCard and store the response
		ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCashCard, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

		// Extract and verify thr fields in the JSON response
		Number id = documentContext.read("$.id");
		assertThat(id).isNotNull();

		Double amount = documentContext.read("$.amount");
		assertThat(amount).isEqualTo(250.00);
	}
}
