package example.cash_card;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

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
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/99", String.class);

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
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/1000", String.class);

		// Verify that the HTTP status code is 404 Not Found
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		// Verify that the response body is blank
		assertThat(response.getBody()).isBlank();
	}

	/**
	 * Test to verify that a new CashCard can be created.
	 */
	@Test
	@DirtiesContext
	void shouldCreateANewCashCard() {
		CashCard newCashCard = new CashCard(null, 250.00, null);

		// Send a POST request to create the new CashCard and store the response
		ResponseEntity<Void> createResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.postForEntity("/cashcards", newCashCard, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Retrieve the location of the newly created CashCard
		URI locationOfNewCashCard = createResponse.getHeaders().getLocation();

		// Send a GET request to the location of the newly created CashCard and store the response
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity(locationOfNewCashCard, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

		// Extract and verify thr fields in the JSON response
		Number id = documentContext.read("$.id");
		assertThat(id).isNotNull();

		Double amount = documentContext.read("$.amount");
		assertThat(amount).isEqualTo(250.00);
	}

	/**
	 * Test to verify that all CashCards are returned when a list is required.
	 */
	@Test
	void shouldReturnAllCashCardsWhenListIsRequested() {
		// Send a GET request to the /cashcards endpoints and store the response
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		// Extract and verify the number of CashCards in the JSON response
		int cashCardCount = documentContext.read("$.length()");
		assertThat(cashCardCount).isEqualTo(3);

		// Extract and verify the fields in the JSON response
		JSONArray ids = documentContext.read("$..id");
		assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

		JSONArray amounts = documentContext.read("$..amount");
		assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.00, 150.00);
	}

	/**
	 * Test to verify that a paginated list of CashCards if returned.
	 */
	@Test
	void shouldReturnAPageOfCashCards() {
		// Send a GET request to the /cashcards endpoint with pagination parameters and store the response
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards?page=0&size=1", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		// Extract and verify the number of CashCards in the page
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
	}

	/**
	 * Test to verify that a sorted page of CashCards is returned.
	 */
	@Test
	void shouldReturnASortedPageOfCashCards() {
		// Send a GET request to the /cashcards endpoint with pagination and sorting parameters and store the response
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		// Extract and verify the number of CashCards in the page
		JSONArray read = documentContext.read("$[*]");
		assertThat(read.size()).isEqualTo(1);

		// Verify that the amount of the first CashCard in the sorted page is correct
		double amount = documentContext.read("$[0].amount");
		assertThat(amount).isEqualTo(150.00);
	}

	/**
	 * Test to verify that a sorted page of CashCards is returned with default pagination and sorting parameters.
	 */
	@Test
	void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
		// Send a GET request to the /cashcards endpoint and store the response
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		// Extract and verify the number of CashCards in the page
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(3);

		// Verify that the amounts of the CashCards in the page are in ascending order
		JSONArray amounts = documentContext.read("$..amount");
		assertThat(amounts).containsExactly(1.00, 123.45, 150.00);
	}

	/**
	 * Test to verify that a CashCard is not returned when using bad credentials.
	 */
	@Test
	void shouldNotReturnACashCardWhenUsingBadCredentials() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("BAD-USER", "abc123")
				.getForEntity("/cashcards/99", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		response = restTemplate
				.withBasicAuth("sarah1", "BAD-PASSWORD")
				.getForEntity("/cashcards/99", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Test to verify that users who do not own any CashCards are rejected.
	 */
	@Test
	void shouldRejectUsersWhoAreNotCardOwners() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("hank-owns-no-cards", "def456")
				.getForEntity("/cashcards/99", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	/**
	 * Test to verify that access to CashCards is not allowed if they do not own them.
	 */
	@Test
	void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/102", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Test to verify that an existing CashCard can be updated.
	 */
	@Test
	@DirtiesContext
	void shouldUpdateAnExistingCashCard() {
		CashCard cashCardUpdate = new CashCard(null, 19.99, null);

		// Send a PUT request to update the CashCard and store the response
		HttpEntity<CashCard> request = new HttpEntity<>(cashCardUpdate);
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/99", HttpMethod.PUT, request, Void.class);

		// Verify that the HTTP status code is 204 No Content
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// Send a GET request to retrieve the updated CashCard and store the response
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/99", String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Double amount = documentContext.read("$.amount");

		// Verify that the 'id' and 'amount' fields are correct
		assertThat(id).isEqualTo(99);
		assertThat(amount).isEqualTo(19.99);
	}

	/**
	 * Test to verify that an unknown CashCard cannot be updated.
	 */
	@Test
	void shouldNotUpdateACashCardThatDoesNotExist() {
		CashCard unknownCard = new CashCard(null, 19.99, null);
		HttpEntity<CashCard> request = new HttpEntity<>(unknownCard);

		// Send a PUT request to update a non-existent CashCard and store the response
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/99999", HttpMethod.PUT, request, Void.class);

		// Verify that the HTTP status code is 404 Not Found
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Test to verify that a CashCard owned by someone else cannot be updated.
	 */
	@Test
	void shouldNotUpdateACashCardThatIsOwnedBySomeoneElse() {
		CashCard kumarsCard = new CashCard(null, 888.88, null);
		HttpEntity<CashCard> request = new HttpEntity<>(kumarsCard);

		// Send a PUT request to update a CashCash owned by someone else and store the response
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/102", HttpMethod.PUT, request, Void.class);

		// Verify that the HTTP status code is 404 Not Found
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Test to verify that an existing CashCard can be deleted.
	 */
	@Test
	@DirtiesContext
	void shouldDeleteAnExistingCashCard() {
		// Send a DELETE request to remove the CashCard with ID 99 and store the response
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/99", HttpMethod.DELETE, null, Void.class);

		// Verify that the HTTP status code response is 204 No Content
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// Send a GET request to verify that the CashCard was successfully deleted
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/99", String.class);

		// Verify that the HTTP status code response is 404 Not Found
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Test to verify that attempting to delete a non-existent CashCard returns 404 Not Found.
	 */
	@Test
	void shouldNotDeleteACashCardThatDoesNotExist() {
		// Send a DELETE request for a non-existent CashCard and store the response
		ResponseEntity<Void> deleteResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/99999", HttpMethod.DELETE, null, Void.class);

		// Verify that the HTTP status code response is 404 Not Found
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Test to verify that a user cannot delete a CashCash they do not own.
	 */
	@Test
	void shouldNotAllowDeletionOfCashCardTheyDoNotOwn() {
		// Send a DELETE request for a CashCard not owned by the user and store the response
		ResponseEntity<Void> deleteResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/102", HttpMethod.DELETE, null, Void.class);

		// Verify that the HTTP status code response is 404 Not Found
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		// Send a GET request to verify that the CashCard still exists
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("kumar2", "xyz789")
				.getForEntity("/cashcards/102", String.class);

		// Verify that the HTTP status code response is 200 OK
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}
