package com.example.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.business.CompanyService;
import com.example.business.ContractService;
import com.example.business.TraderService;
import com.example.business.entity.Matrix;
import com.example.persistence.entity.Company;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/trade")
@OpenAPIDefinition(info = @Info(title = "Trade API", description = "A trade api for companies, those want to trade with each other", contact = @Contact(name = "Gizem", email = "gizemyilmaz1988@gmail.com")))
public class TradeResource {

	public static final class Constants {
		public static final String COMPANY = "/companies/{id}";
		public static final String COMPANIES = "/companies";
		public static final String CONTRACTS = "/contracts";
		public static final String CONTRACTS_BETWEEN = "/contracts/{trader}/{tradee}";
		public static final String SLEEVES = "/sleeves/{trader}/{tradee}";
		public static final String MATRIX = "/matrix";
	}

	@Autowired
	private TraderService traderService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private ContractService contractService;

	@Operation(summary = "List all registered companies", responses = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved") })
	@GetMapping(Constants.COMPANIES)
	public ResponseEntity<Flux<Company>> getCompanies() {
		return ResponseEntity.ok(Flux.fromIterable(companyService.getCompanies()));
	}

	@Operation(summary = "Register a company", responses = {
			@ApiResponse(responseCode = "200", description = "Successfully registered") })
	@PostMapping(Constants.COMPANIES)
	public ResponseEntity<Company> addCompany(@RequestBody Company company) {
		Optional<Company> added = companyService.addCompany(company);
		return Optional.ofNullable(added).map(c -> ResponseEntity.ok().body(c.get())).get();
	}

	@Operation(summary = "Brings the matrix", responses = {
			@ApiResponse(responseCode = "200", description = "Successfull") })
	@GetMapping(Constants.MATRIX)
	public ResponseEntity<Flux<Matrix>> getMatrix() {
		return Optional.ofNullable(traderService.getMatrix())
				.map(list -> ResponseEntity.ok().body(Flux.fromIterable(list))).get();
	}

	@Operation(summary = "Gets a certain registered company with name", responses = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved"),
			@ApiResponse(responseCode = "404", description = "Company not found") })
	@GetMapping(Constants.COMPANY)
	public ResponseEntity<Company> getCompany(@PathVariable(name = "name") String name) {
		return Optional.ofNullable(companyService.getCompanyByName(name))
				.map(company -> ResponseEntity.ok().body(company.get()))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Operation(summary = "Checks if 2 companies can trade with each other", responses = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved"),
			@ApiResponse(responseCode = "404", description = "Both or one of the companies not found") })
	@GetMapping(Constants.CONTRACTS_BETWEEN)
	public ResponseEntity<Boolean> canTrade(@PathVariable(name = "trader") String traderName,
			@PathVariable(name = "tradee") String tradeeName,
			@RequestParam(required = false, defaultValue = "false") boolean directTrade) {

		Optional<Company> trader = companyService.getCompanyByName(traderName);
		if (traderName.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Optional<Company> tradee = companyService.getCompanyByName(tradeeName);
		if (tradee.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		boolean result;
		if (directTrade) {
			result = traderService.hasBasicConnection(trader.get(), tradee.get());
		} else {
			result = traderService.hasPath(trader.get().getCompanyId(), tradee.get().getCompanyId(),
					new ArrayList<Integer>());
		}
		return ResponseEntity.ok().body(result);

	}

	@Operation(summary = "Shows sleeves", responses = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved"),
			@ApiResponse(responseCode = "404", description = "Both or one of the companies not found") })
	@GetMapping(Constants.SLEEVES)
	public ResponseEntity<List<Set<String>>> showSleeves(@PathVariable(name = "trader") String traderName,
			@PathVariable(name = "tradee") String tradeeName) {

		Optional<Company> trader = companyService.getCompanyByName(traderName);
		if (traderName.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Optional<Company> tradee = companyService.getCompanyByName(tradeeName);
		if (tradee.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		List<Set<String>> returnList = traderService.calculateSleeves(trader.get().getCompanyId(),
				tradee.get().getCompanyId());

		return ResponseEntity.ok().body(returnList);

	}

	@Operation(summary = "Add a direct trade between two companies, if company is not regostered, method first registers the company", responses = {
			@ApiResponse(responseCode = "200", description = "Successfully added."),
			@ApiResponse(responseCode = "400", description = "Company input error") })
	@SuppressWarnings("rawtypes")
	@PostMapping(Constants.CONTRACTS)
	public ResponseEntity addTrade(@RequestBody List<Company> companies) {

		if (CollectionUtils.isEmpty(companies) || companies.size() < 2) {
			return ResponseEntity.badRequest().build();
		}
		contractService.addConnection(companies.get(0), companies.get(1));
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Removes a direct trade between two companies", responses = {
			@ApiResponse(responseCode = "204", description = "Successfully deleted."),
			@ApiResponse(responseCode = "400", description = "Company input error") })
	@SuppressWarnings("rawtypes")
	@DeleteMapping(Constants.CONTRACTS)
	public ResponseEntity removeTrade(@RequestBody List<Company> companies) {

		if (CollectionUtils.isEmpty(companies) || companies.size() < 2) {
			return ResponseEntity.badRequest().build();
		}
		contractService.removeDirectConnection(companies.get(0), companies.get(1));
		return ResponseEntity.noContent().build();
	}

}