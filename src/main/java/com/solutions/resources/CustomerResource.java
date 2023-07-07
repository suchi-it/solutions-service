package com.solutions.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.solutions.model.CreateCustomerRequest;
import com.solutions.model.UpdateCustomerRequest;
import com.solutions.service1.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/solutions/customers")
public class CustomerResource
{
	@Autowired
	CustomerService customerService;
	private static final Logger LOGGER=LoggerFactory.getLogger(CustomerResource.class);
	@PostMapping("/createcustomer")
public ResponseEntity<?> createCustomer(@RequestBody CreateCustomerRequest request) 
	{
		LOGGER.info("data record is sucessfully created ");
		return new ResponseEntity<String>(customerService.createCustomer(request),HttpStatus.BAD_REQUEST);
	}
	@GetMapping("/getcustomers")
	public ResponseEntity<?> getCustomers(@RequestParam(required = false) String searchInput) 
	{
		LOGGER.info("get all the records");
		return this.customerService.getCustomers(searchInput);
	}
	@GetMapping("/getcustomer")
	public ResponseEntity<?> getCustomer(@RequestParam String clientname)
	{
		LOGGER.info("get one record");
		return this.customerService.getCustomer(clientname);
    }
	@PutMapping("/updatecustomer")
	public ResponseEntity<?> updateCustomer(@RequestBody UpdateCustomerRequest request)
	{
		LOGGER.info("data record is sucessfully updated");
		return this.customerService.updateCustomer(request);
	}
	
	@DeleteMapping("/deletecustomer")
	public ResponseEntity<?> deleteCustomer(@RequestParam String clientname) 
	{
		LOGGER.info("data record is sucessfully deleted");
		return this.customerService.deleteCustomer(clientname);
	}
}
