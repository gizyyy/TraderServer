package com.example.business;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.persistence.entity.Company;
import com.example.persistence.repository.CompanyRepository;

@Service
public class CompanyService {

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private ConcurrentHashMap<Integer, List<Integer>> tradeMap;

	@Transactional(value = TxType.REQUIRES_NEW)
	public Optional<Company> addCompany(Company s) {
		Optional<Company> company = companyRepository.findByCompanyName(s.getCompanyName());
		if (company.isPresent())
			return company;

		Company saved = companyRepository.save(s);
		tradeMap.put(saved.getCompanyId(), new LinkedList<Integer>());
		return Optional.of(saved);
	}

	public Optional<Company> getCompanyByName(String name) {
		return companyRepository.findByCompanyName(name);
	}

	public List<Company> getCompanies() {
		return companyRepository.findAll();
	}

}
