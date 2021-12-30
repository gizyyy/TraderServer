package com.example.persistence.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.domain.AbstractAggregateRoot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "contract")
public class Contract extends AbstractAggregateRoot<Contract> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "contract_id")
	private Integer contractId;

	@OneToOne(targetEntity = Company.class, cascade = CascadeType.MERGE)
	@JoinColumn(name = "trader_id")
	private Company trader;

	@ManyToMany(cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinTable(name = "contract_tradees", joinColumns = { @JoinColumn(name = "contract_id") }, inverseJoinColumns = {
			@JoinColumn(name = "company_id") })
	private List<Company> tradee;

}