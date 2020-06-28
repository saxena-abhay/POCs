/**
 * 
 */
package com.apriori.demo.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author abhay
 *
 */
@Document(collection = "GroceryData")
public class GroceryData {
	  @Id
	  private String id;
	  
	  @Field("Member_Number")
	  private String memberNumber;
	  
	  @Field("Date")
	  private String date;
	  
	  @Field("Item_Description")
	  private String itemDescription;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the memberNumber
	 */
	public String getMemberNumber() {
		return memberNumber;
	}
	/**
	 * @param memberNumber the memberNumber to set
	 */
	public void setMemberNumber(String memberNumber) {
		this.memberNumber = memberNumber;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the itemDescription
	 */
	public String getItemDescription() {
		return itemDescription;
	}
	/**
	 * @param itemDescription the itemDescription to set
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	@Override
	public String toString() {
		return "GroceryData [id=" + id + ", memberNumber=" + memberNumber + ", date=" + date + ", itemDescription="
				+ itemDescription + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((memberNumber == null) ? 0 : memberNumber.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroceryData other = (GroceryData) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (memberNumber == null) {
			if (other.memberNumber != null)
				return false;
		} else if (!memberNumber.equals(other.memberNumber))
			return false;
		return true;
	}
	
	public String getKey() {
		return new StringBuffer().append(getMemberNumber()).append(getDate()).toString();
	}
	  
	  
}
