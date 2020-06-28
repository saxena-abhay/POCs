package com.apriori.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apriori.demo.Configuration;
import com.apriori.demo.modal.GroceryData;
import com.apriori.demo.repository.GroceryDataRepository;
import com.apriori.demo.util.AprioriUtil;

@RestController
@RequestMapping("/api")
public class GroceryDataController {

	@Autowired
	GroceryDataRepository groceryDataRepository;
	
	@Autowired
	Configuration configuration;
	
	@GetMapping("/data")
	public ResponseEntity<List<GroceryData>> getAllData() {
		// TODO Auto-generated method stub
		
		List<GroceryData> dataList = new ArrayList<GroceryData>();
		//groceryDataRepository.findAll().forEach(dataList::add);
		
		dataList = groceryDataRepository.findAll();		
		
		return  new ResponseEntity<>(dataList, HttpStatus.OK);
		
	}
	
	@GetMapping("/trends")
	public ResponseEntity<String> getFreqItems() {
		// TODO Auto-generated method stub
		
		List<GroceryData> dataList = new ArrayList<GroceryData>();
		//groceryDataRepository.findAll().forEach(dataList::add);
		
		dataList = groceryDataRepository.findAll();
		String freqItems = "";
		try {
			freqItems  = AprioriUtil.process(dataList,configuration.getSupport_val());
			return  new ResponseEntity<>(freqItems, HttpStatus.OK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return  new ResponseEntity<>(freqItems, HttpStatus.EXPECTATION_FAILED);
		}
		
		
	}

}
