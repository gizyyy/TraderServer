package com.example.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.business.entity.Matrix;
import com.example.persistence.entity.Company;
import com.example.persistence.entity.Contract;
import com.example.persistence.repository.CompanyRepository;
import com.example.persistence.repository.ContractRepository;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Supplier;

@Service
public class TraderService {

	@Autowired
	private ContractRepository contractRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private ConcurrentHashMap<Integer, List<Integer>> tradeMap;

	@PostConstruct
	public void fillMap() {
		List<Contract> allContracts = contractRepository.findAll();
		if (CollectionUtils.isEmpty(allContracts)) {
			return;
		}

		allContracts.stream().forEach(c -> {
			if (!tradeMap.containsKey(c.getTrader().getCompanyId())) {
				tradeMap.keySet().add(c.getTrader().getCompanyId());
			}

			c.getTradee().forEach(k -> {
				tradeMap.get(c.getTrader().getCompanyId()).add(k.getCompanyId());
			});
		});
	}

	public List<Matrix> getMatrix() {
		List<Matrix> matrix = new ArrayList<Matrix>();
		tradeMap.keySet().stream().forEach(i -> {
			Optional<Company> c = companyRepository.findByCompanyId(i);
			Matrix m = new Matrix();
			m.setCompany(c.get());
			m.setSleeves(new ArrayList<Company>());
			tradeMap.get(i).stream().map(k -> companyRepository.findByCompanyId(k))
					.forEach(j -> m.getSleeves().add(j.get()));

		});
		return matrix;
	}

	public boolean hasCompanyInTrade(Company s) {
		if (tradeMap.containsKey(s.getCompanyId())) {
			return true;
		}
		return false;
	}

	/**
	 * This function gives whether a basic connection is present or not.
	 * 
	 * @param s
	 * @param d
	 * @return
	 */
	public boolean hasBasicConnection(Company s, Company d) {
		if (Objects.isNull(tradeMap.get(s.getCompanyId())))
			return false;

		if (tradeMap.get(s.getCompanyId()).contains(d.getCompanyId())) {
			return true;
		}
		return false;
	}

	/**
	 * This function gives whether a complicated connection is present or not.
	 * 
	 * @param s
	 * @param d
	 * @param visitedList
	 * @return
	 */
	public boolean hasPath(Integer s, Integer d, List<Integer> visitedList) {
		if (tradeMap.keySet().contains(s)) {
			visitedList.add(s);
			Integer start = s;
			List<Integer> list = tradeMap.get(start);
			if (list.stream().anyMatch((x -> x.equals(d)))) {
				return true;
			} else {
				for (Integer t : list) {
					if (visitedList.contains(t))
						continue;
					return hasPath(t, d, visitedList);
				}
				return false;
			}
		}
		return false;
	}

	/**
	 * Calculate sleeves.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Set<String>> calculateSleeves(Integer start, Integer end) {
		LinkedList<Integer> visited = new LinkedList<>();
		visited.add(start);
		List<Set<String>> returnList = new ArrayList<Set<String>>();
		depthFirst(end, visited, returnList);
		return returnList;
	}

	private void depthFirst(Integer tradeeId, LinkedList<Integer> visitedList, List<Set<String>> returnList) {
		List<Integer> nodes = getNodes(visitedList.getLast());
		for (Integer node : nodes) {
			if (visitedList.contains(node)) {
				continue;
			}
			if (node.equals(tradeeId)) {
				visitedList.add(node);
				printPath(visitedList, returnList);
				visitedList.removeLast();
				break;
			}
		}
		for (Integer node : nodes) {
			if (visitedList.contains(node) || node.equals(tradeeId)) {
				continue;
			}
			visitedList.addLast(node);
			depthFirst(tradeeId, visitedList, returnList);
			visitedList.removeLast();
		}
	}

	private void printPath(List<Integer> visited, List<Set<String>> returnList) {
		Set<String> coming = new HashSet<String>();
		for (Integer node : visited) {
			Optional<Company> findByCompanyId = companyRepository.findByCompanyId(node);
			coming.add(findByCompanyId.get().getCompanyName());
		}
		returnList.add(coming);
	}

	private List<Integer> getNodes(Integer last) {
		List<Integer> tradees = tradeMap.get(last);
		if (CollectionUtils.isEmpty(tradees)) {
			return new ArrayList<>();
		}
		return tradees;
	}
}
