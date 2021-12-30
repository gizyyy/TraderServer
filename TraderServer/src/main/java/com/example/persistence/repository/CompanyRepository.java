package com.example.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.persistence.entity.Company;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Integer> {

    Optional<Company> findByCompanyId(Integer id);
    
    Optional<Company> findByCompanyName(String companyName);
    
    List<Company> findAll();
    
    @SuppressWarnings("unchecked")
	Company save(Company company);
    
    void deleteById(Integer id);
    
} 