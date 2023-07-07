package com.ServiceImpl.Test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.solutions.model.Comment;
import com.solutions.model.CreateCustomerRequest;
import com.solutions.model.Customer;
import com.solutions.model.UpdateCustomerRequest;
import com.solutions.service.impl.CustomerServiceImpl;
import java.util.ArrayList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerServiceImplTest {

	@Mock
	private MongoTemplate mongoTemplate;

	@InjectMocks
	private CustomerServiceImpl customerService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testCreateCustomer_Successful() {
		CreateCustomerRequest request = new CreateCustomerRequest();
		request.setClientName("pavan");
		request.setEmailId("pavansai@gmail.com");
		request.setContactNumber("1234567890");
		request.setComments(new ArrayList<>());
		String result = customerService.createCustomer(request);
		verify(mongoTemplate, times(1)).insert(any(Customer.class));
		assertEquals("Customer successfully created with clientName: pavan", result);
	}

	@Test
	public void testCreateCustomer_EmptyClientName() {
		CreateCustomerRequest request = new CreateCustomerRequest();
		request.setClientName("");
		request.setEmailId("pavansai@gmail.com");
		request.setContactNumber("1234567890");
		request.setComments(new ArrayList<>());
		String result = customerService.createCustomer(request);
		assertEquals("Client name cannot be empty", result);
		verify(mongoTemplate, never()).insert(any(Customer.class));
	}

	@Test
	public void testGetCustomers_EmptySearchInput() {
		String searchInput = "";
		ResponseEntity<?> response = customerService.getCustomers(searchInput);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Search input cannot be empty", response.getBody());
		verify(mongoTemplate, never()).find(any(), eq(Customer.class));
	}

	@Test
	public void testGetCustomer_EmptyClientName() {
		String clientName = "";
		ResponseEntity<?> response = customerService.getCustomer(clientName);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Client name cannot be empty", response.getBody());
		verify(mongoTemplate, never()).findOne(any(), eq(Customer.class));
	}

	@Test
	public void testUpdateCustomer_Successful() {
		// Arrange
		UpdateCustomerRequest request = new UpdateCustomerRequest();
		request.setClientName("pavan");
		request.setEmailId("pavansai@gmail.com");
		request.setContactNumber("9603314888");
		request.setComments(new Comment());
		Customer existingCustomer = new Customer();
		existingCustomer.setClientName("pavan");
		when(mongoTemplate.findOne(any(Query.class), eq(Customer.class))).thenReturn(existingCustomer);
		ResponseEntity<?> response = customerService.updateCustomer(request);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Customer pavan is successfully updated", response.getBody());
		verify(mongoTemplate, times(1)).save(any(Customer.class));
	}

	@Test
	public void testDeleteCustomer_Successful() {
		String clientName = "pavan";
		Customer existingCustomer = new Customer();
		existingCustomer.setClientName("pavan");
		when(mongoTemplate.findOne(any(Query.class), eq(Customer.class))).thenReturn(existingCustomer);
		ResponseEntity<?> response = customerService.deleteCustomer(clientName);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Customer pavan is successfully deleted", response.getBody());
		verify(mongoTemplate, times(1)).save(any(Customer.class));
	}

}
