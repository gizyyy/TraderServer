package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.business.CompanyService;
import com.example.business.ContractService;
import com.example.business.TraderConfig;
import com.example.business.TraderService;
import com.example.persistence.entity.Company;
import com.example.persistence.entity.Contract;
import com.example.persistence.repository.CompanyRepository;
import com.example.persistence.repository.ContractRepository;

@SpringBootTest(classes = { CompanyService.class, ContractService.class, TraderService.class, CompanyRepository.class,
		ContractRepository.class, TraderConfig.class, Company.class, Contract.class })
public class TradeTest2 {

	@Autowired
	@InjectMocks
	public CompanyService companyService;

	@Autowired
	@InjectMocks
	public ContractService contractService;

	@Autowired
	@InjectMocks
	public TraderService traderService;

	@Autowired
	public ConcurrentHashMap<Integer, List<Integer>> tradeMap;

	@MockBean
	public CompanyRepository companyRepository;

	@MockBean
	public ContractRepository contractRepository;

	@Autowired
	public List<Company> companies = new ArrayList<Company>();

	@BeforeEach
	void setup() {
		companies.add(new Company(1, "A"));
		companies.add(new Company(2, "B"));
		companies.add(new Company(3, "C"));
		companies.add(new Company(4, "D"));
		companies.add(new Company(5, "E"));
	}

	@Test
	public void shouldBeAbleToCalculateDirectRelation() {
		companies.stream().forEach(c -> {
			Mockito.when(companyRepository.save(c)).thenReturn(c);
		});
		contractService.addConnection(new Company(1, "A"), new Company(2, "B"));
		assertTrue(traderService.hasBasicConnection(new Company(1, "A"), new Company(2, "B")));
	}

	@Test
	public void shouldBeAbleToCalculateIndirectRelation() {
		companies.stream().forEach(c -> {
			Mockito.when(companyRepository.save(c)).thenReturn(c);
		});
		contractService.addConnection(new Company(1, "A"), new Company(2, "B"));
		contractService.addConnection(new Company(2, "B"), new Company(3, "C"));
		contractService.addConnection(new Company(3, "C"), new Company(4, "D"));
		assertTrue(traderService.hasPath(1, 4, new ArrayList<Integer>()));
	}

	@Test
	public void shouldBeAbleToCalculateRelation() {
		companies.stream().forEach(c -> {
			Mockito.when(companyRepository.save(c)).thenReturn(c);
		});
		contractService.addConnection(new Company(1, "A"), new Company(2, "B"));
		contractService.addConnection(new Company(2, "B"), new Company(3, "C"));
		contractService.addConnection(new Company(5, "E"), new Company(4, "D"));
		assertFalse(traderService.hasPath(1, 5, new ArrayList<Integer>()));
	}

	@Test
	public void shouldReturnAddedCompany() {
		companies.stream().forEach(c -> {
			Mockito.when(companyRepository.save(c)).thenReturn(c);
			Mockito.when(companyRepository.findByCompanyName(c.getCompanyName())).thenReturn(Optional.ofNullable(c));
		});
		companyService.addCompany(new Company(1, "A"));
		assertEquals(companyService.getCompanyByName("A").get(), new Company(1, "A"));
	}

	@Test
	public void shouldAddTheContractOnTradeeSide() {
		companies.stream().forEach(c -> {
			Mockito.when(companyRepository.save(c)).thenReturn(c);

		});
		contractService.addConnection(new Company(1, "A"), new Company(2, "B"));
		assertTrue(tradeMap.containsKey(2));
	}

	@Test
	public void shoulRemoveTheContractOnTradeeSide() {
		companies.stream().forEach(c -> {
			Mockito.when(companyRepository.save(c)).thenReturn(c);
		});
		contractService.addConnection(new Company(1, "A"), new Company(2, "B"));
		companies.stream().forEach(c -> {
			Mockito.when(companyRepository.findByCompanyName(c.getCompanyName())).thenReturn(Optional.ofNullable(c));
		});
		List<Company> tradeeList= new ArrayList<Company>();
		tradeeList.add(new Company(2, "B"));
		Mockito.when(contractRepository.findByTrader(new Company(1, "A")))
				.thenReturn(Optional.ofNullable(new Contract(1, new Company(1, "A"), tradeeList)));
		
		List<Company> tradeeList2= new ArrayList<Company>();
		tradeeList2.add(new Company(1, "A"));
		Mockito.when(contractRepository.findByTrader(new Company(2, "B")))
				.thenReturn(Optional.ofNullable(new Contract(2, new Company(2, "B"), tradeeList2)));
		
		contractService.removeDirectConnection(new Company(1, "A"), new Company(2, "B"));
		assertFalse(tradeMap.containsKey(2));
	}

}
