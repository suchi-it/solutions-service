package com.solutions.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.solutions.constants.CustomerConstants;
import com.solutions.model.Comment;
import com.solutions.model.CreateCustomerRequest;
import com.solutions.model.Customer;
import com.solutions.model.UpdateCustomerRequest;
import com.solutions.service1.CustomerService;
import org.springframework.data.mongodb.core.query.Query;

@Service
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	private MongoTemplate mongoTemplate;
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

	public String createCustomer(CreateCustomerRequest request) {
		if (StringUtils.isEmpty(request.getClientName())) {
			return "Client name cannot be empty";
		}
		if (StringUtils.isEmpty(request.getEmailId())) {
			return "Email ID cannot be empty";
		}
		if (StringUtils.isEmpty(request.getContactNumber())) {
			return "Contact number cannot be empty";
		}
		Criteria criteria = new Criteria();
		criteria.orOperator(
				Criteria.where("clientName").regex(
						Pattern.compile(request.getClientName(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("emailId")
						.regex(Pattern.compile(request.getEmailId(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("contactNumber").regex(
						Pattern.compile(request.getContactNumber(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
		Query query = new Query(criteria);
		Customer customer = this.mongoTemplate.findOne(query, Customer.class);
		if (customer == null) {
			customer = new Customer();
			BeanUtils.copyProperties(request, customer);
			List<Comment> finalComments = new ArrayList<>();
			for (Comment comments : request.getComments()) {
				comments.setCreatedAt(new Date());
				finalComments.add(comments);
			}
			customer.setComments(finalComments);
			customer.setStatus(CustomerConstants.PENDING);
			customer.setCreatedAt(new Date(System.currentTimeMillis()));
			this.mongoTemplate.insert(customer);
			LOGGER.info("customer sucessfully created with clientname" + customer.getClientName());
			return "Customer successfully created with clientName: " + customer.getClientName();
		} else {
			LOGGER.info("Customer already exist");
			return "Customer already exist";
		}

	}

	public ResponseEntity<?> getCustomers(String searchInput) {
		if (StringUtils.isEmpty(searchInput)) {
			return new ResponseEntity<>("Search input cannot be empty", HttpStatus.BAD_REQUEST);
		}
		Query query = new Query();
		if (StringUtils.isNotEmpty(searchInput)) {
			query = this.getSearchQuery(searchInput);
		}
		List<Customer> customers = this.mongoTemplate.find(query, Customer.class);
		if (!CollectionUtils.isEmpty(customers)) {
			return new ResponseEntity<>(customers, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}
	}

	private Query getSearchQuery(String searchInput) {
		Query query = new Query();
		List<Criteria> criterias = new LinkedList<>();
		Criteria searchCriteria = new Criteria();
		searchCriteria.orOperator(
				Criteria.where("clientName")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("contactNumber")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("emailId")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("industry")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("interested")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("demoStatus")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("dueDate")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("Comment.comments")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("Comment.createdBy")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
		if (!CollectionUtils.isEmpty(criterias)) {
			Criteria criteria = new Criteria();
			criteria.andOperator(criterias.stream().toArray(Criteria[]::new));
			query.addCriteria(criteria);
		}
		return query;
	}

	public ResponseEntity<?> getCustomer(String clientName) {
		if (StringUtils.isEmpty(clientName)) {
			return new ResponseEntity<>("Client name cannot be empty", HttpStatus.BAD_REQUEST);
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("clientName").is(clientName));
		Customer customer = this.mongoTemplate.findOne(query, Customer.class);
		if (customer != null) {
			return new ResponseEntity<>(customer, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new Customer(), HttpStatus.OK);
		}
	}

	public ResponseEntity<?> updateCustomer(UpdateCustomerRequest request) {
		if (StringUtils.isEmpty(request.getClientName())) {
			return new ResponseEntity<>("Client name cannot be empty", HttpStatus.BAD_REQUEST);
		}

		Query query = new Query();
		query.addCriteria(Criteria.where("clientName").is(request.getClientName()));
		Customer customer = this.mongoTemplate.findOne(query, Customer.class);

		if (customer != null) {
			if (StringUtils.isNotEmpty(request.getClientName())) {
				customer.setClientName(request.getClientName());
			}
			if (request.getContactNumber() != null) {
				customer.setContactNumber(request.getContactNumber());
			}
			if (StringUtils.isNotEmpty(request.getEmailId())) {
				customer.setEmailId(request.getEmailId());
			}
			if (StringUtils.isNotEmpty(request.getIndustry())) {
				customer.setIndustry(request.getIndustry());
			}
			if (request.getDueDate() != null) {
				customer.setDueDate(request.getDueDate());
			}
			if (StringUtils.isNotEmpty(request.getDemoStatus())) {
				customer.setDemoStatus(request.getDemoStatus());
			}
			if (StringUtils.isNotEmpty(request.getInterested())) {
				customer.setInterested(request.getInterested());
			}
			if (request.getComments() != null) {
				List<Comment> comments = customer.getComments();
				if (CollectionUtils.isEmpty(comments)) {
					comments = new ArrayList<>();
				}
				Comment comment = request.getComments();
				comment.setCreatedAt(new Date());
				comments.add(comment);
				customer.setComments(comments);
			}

			this.mongoTemplate.save(customer);
			LOGGER.info("Customer " + customer.getClientName() + " is successfully updated");
			return new ResponseEntity<>("Customer " + customer.getClientName() + " is successfully updated",
					HttpStatus.OK);
		} else {
			LOGGER.error("No Customer found with clientname- " + request.getClientName());
			return new ResponseEntity<>("No Customer found with clientname- " + request.getClientName(),
					HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<?> deleteCustomer(String clientName) {
		if (StringUtils.isEmpty(clientName)) {
			return new ResponseEntity<>("Client name cannot be empty", HttpStatus.BAD_REQUEST);
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("clientName").is(clientName));
		Customer customer = this.mongoTemplate.findOne(query, Customer.class);
		if (customer != null) {
			customer.setStatus("INACTIVE");
			this.mongoTemplate.save(customer);
			LOGGER.info("Customer " + clientName + " is successfully deleted");
			return new ResponseEntity<>("Customer " + clientName + " is successfully deleted", HttpStatus.OK);
		} else {
			LOGGER.error("No Customer found with clientname-" + clientName);
			return new ResponseEntity<>("No Customer found with clientname-" + clientName, HttpStatus.NOT_FOUND);
		}
	}

}
