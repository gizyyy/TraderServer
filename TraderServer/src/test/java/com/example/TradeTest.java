package com.example;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.application.TradeResource;
import com.example.business.CompanyService;
import com.example.business.ContractService;
import com.example.business.TraderService;

@AutoConfigureMockMvc
@SpringBootTest(classes = { CompanyService.class, TradeResource.class })
public class TradeTest {

	@Autowired
	public MockMvc mockMvc;

	@MockBean
	public CompanyService companyService;

	@MockBean
	private TraderService traderService;

	@MockBean
	private ContractService contractService;

	@Test
	public void shouldBeAbleToCalculateDirectRelation() throws Exception {
		Mockito.when(companyService.getCompanies()).thenReturn(null);
		mockMvc.perform(MockMvcRequestBuilders.get("/trade/companies"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

}
