/**
 * 
 */
package com.apriori.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.apriori.demo.modal.GroceryData;

/**
 * @author abhay
 *
 */
public interface GroceryDataRepository extends MongoRepository<GroceryData, String> {	
	//public List<GroceryData> findAllGroceryData();
}
