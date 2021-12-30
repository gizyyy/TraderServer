package com.example.business.entity;

import java.util.List;

import com.example.persistence.entity.Company;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Matrix {

	private Company company;
	private List<Company> sleeves;

}
