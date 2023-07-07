package com.solutions.service1;

import org.springframework.http.ResponseEntity;
import com.solutions.model.CreateCustomerRequest;
import com.solutions.model.UpdateCustomerRequest;

public interface CustomerService {
	public String createCustomer(CreateCustomerRequest request);

	ResponseEntity<?> getCustomers(String searchInput);

	ResponseEntity<?> getCustomer(String clientName);

	ResponseEntity<?> updateCustomer(UpdateCustomerRequest request);

	ResponseEntity<?> deleteCustomer(String clientName);

}
