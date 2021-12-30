package com.example.business;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.business.entity.Matrix;
import com.example.persistence.entity.Company;
import com.example.persistence.entity.Contract;
import com.example.persistence.repository.ContractRepository;

@Service
public class ContractService {

	@Autowired
	private ContractRepository contractRepository;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private TraderService traderService;

	@Autowired
	private ConcurrentHashMap<Integer, List<Integer>> tradeMap;

	/**
	 * This function adds the connection between two companies
	 * 
	 * @param source
	 * @param destination
	 */
	public void addConnection(Company source, Company destination) {
		Company sCompany = source;
		Company dCompany = destination;

		Optional<Company> sourceCompany = companyService.getCompanyByName(source.getCompanyName());
		if (sourceCompany.isEmpty()) {
			sCompany = companyService.addCompany(source).get();
			tradeMap.put(sCompany.getCompanyId(), new LinkedList<Integer>());
		} else {
			sCompany = sourceCompany.get();
		}

		Optional<Company> destinationCompany = companyService.getCompanyByName(destination.getCompanyName());
		if (destinationCompany.isEmpty()) {
			dCompany = companyService.addCompany(destination).get();
			tradeMap.put(dCompany.getCompanyId(), new LinkedList<Integer>());
		} else {
			dCompany = destinationCompany.get();
		}

		boolean hasBasicConnection = traderService.hasBasicConnection(sCompany, dCompany);
		if (hasBasicConnection)
			return;

		createContract(sCompany, dCompany);
	}

	@Transactional
	private void createContract(Company sCompany, Company dCompany) {
		Optional<Contract> contractExisting = contractRepository.findByTrader(sCompany);
		if (contractExisting.isPresent()) {
			Contract contract = contractExisting.get();
			if (CollectionUtils.isEmpty(contract.getTradee())) {
				LinkedList<Company> linkedList = new LinkedList<Company>();
				linkedList.add(dCompany);
				contract.setTradee(linkedList);
			} else {
				contract.getTradee().add(dCompany);
			}
			contractRepository.save(contract);

		} else {
			Contract c = new Contract();
			c.setTrader(sCompany);
			if (CollectionUtils.isEmpty(c.getTradee())) {
				LinkedList<Company> linkedList = new LinkedList<Company>();
				linkedList.add(dCompany);
				c.setTradee(linkedList);
			}
			contractRepository.save(c);
		}

		Optional<Contract> tradeeAsTraderExisting = contractRepository.findByTrader(dCompany);
		if (tradeeAsTraderExisting.isPresent()) {
			Contract contractTradee = tradeeAsTraderExisting.get();
			if (CollectionUtils.isEmpty(contractTradee.getTradee())) {
				LinkedList<Company> linkedList = new LinkedList<Company>();
				linkedList.add(sCompany);
				contractTradee.setTradee(linkedList);
			} else {
				contractTradee.getTradee().add(sCompany);
			}
			contractRepository.save(contractTradee);
		} else {
			Contract c = new Contract();
			c.setTrader(dCompany);
			if (CollectionUtils.isEmpty(c.getTradee())) {
				LinkedList<Company> linkedList = new LinkedList<Company>();
				linkedList.add(sCompany);
				c.setTradee(linkedList);
			}
			contractRepository.save(c);
		}

		tradeMap.get(sCompany.getCompanyId()).add(dCompany.getCompanyId());
		tradeMap.get(dCompany.getCompanyId()).add(sCompany.getCompanyId());
	}

	@Transactional
	public void removeDirectConnection(Company source, Company destination) {

		Optional<Company> sourceByName = companyService.getCompanyByName(source.getCompanyName());
		if (sourceByName.isEmpty()) {
			return;
		}

		Optional<Company> destinationByName = companyService.getCompanyByName(destination.getCompanyName());
		if (destinationByName.isEmpty()) {
			return;
		}

		if (!traderService.hasBasicConnection(sourceByName.get(), destinationByName.get()))
			return;

		Integer tradeeId = destinationByName.get().getCompanyId();
		Integer traderId = sourceByName.get().getCompanyId();
		tradeMap.get(traderId).removeIf(k -> Integer.compare(k, tradeeId) == 0);

		Optional<Contract> contract = contractRepository.findByTrader(sourceByName.get());
		contract.get().getTradee().removeIf(k -> Integer.compare(k.getCompanyId(), tradeeId) == 0);

		if (CollectionUtils.isEmpty(contract.get().getTradee())) {
			contractRepository.deleteByTrader(sourceByName.get());
		} else {
			contractRepository.save(contract.get());
		}

		if (CollectionUtils.isEmpty(tradeMap.get(traderId))) {
			tradeMap.remove(traderId);
		}

		tradeMap.get(tradeeId).removeIf(k -> Integer.compare(k, traderId) == 0);
		Optional<Contract> contractReverse = contractRepository.findByTrader(destinationByName.get());
		contractReverse.get().getTradee().removeIf(k -> Integer.compare(k.getCompanyId(), traderId) == 0);

		if (CollectionUtils.isEmpty(contractReverse.get().getTradee())) {
			contractRepository.deleteByTrader(destinationByName.get());
		} else {
			contractRepository.save(contractReverse.get());
		}

		if (CollectionUtils.isEmpty(tradeMap.get(tradeeId))) {
			tradeMap.remove(tradeeId);
		}
	}
}
