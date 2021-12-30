package com.example.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.persistence.entity.Company;
import com.example.persistence.entity.Contract;

@Repository
public interface ContractRepository extends CrudRepository<Contract, Integer> {

	Optional<Contract> findById(Integer id);

	Optional<Contract> findByTrader(Company trader);
	
	List<Contract> findAll();

	@SuppressWarnings("unchecked")
	Contract save(Contract contract);

	void deleteById(Integer id);
	
	void deleteByTrader(Company trader);

}