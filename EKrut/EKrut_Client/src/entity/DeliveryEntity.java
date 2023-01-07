package entity;

import java.io.Serializable;

import common.CustomerStatus;
import common.DeliveryStatus;

public class DeliveryEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String estimatedTime;
	private int orderId;
	private String address, region; //change later to AddressEntity
	private DeliveryStatus deliveryStatus;
	private CustomerStatus customerStatus;

	

	/**
	 * For getting delivery entity
	 * @param orderId
	 * @param region
	 * @param customerId
	 * @param address
	 * @param estimatedTime
	 * @param deliveryStatus
	 * @param customerStatus
	 */
	public DeliveryEntity(int orderId, String region,  String address, String estimatedTime,
			DeliveryStatus deliveryStatus, CustomerStatus customerStatus) {
		super();
		this.estimatedTime = estimatedTime;
		this.orderId = orderId;
		this.address = address;
		this.region = region;
		this.deliveryStatus = deliveryStatus;
		this.customerStatus = customerStatus;
	}

	/**
	 * For build new delivery entity
	 * @param region
	 * @param customerId
	 * @param address
	 */
	public DeliveryEntity(String region, String address) {
		this.address=address;
		this.region = region;
		this.deliveryStatus = DeliveryStatus.pendingApproval;
		this.customerStatus= CustomerStatus.NOT_APPROVED;
	}


	public String getEstimatedTime() {
		return estimatedTime;
	}


	public void setEstimatedTime(String estimatedTime) {
		this.estimatedTime = estimatedTime;
	}


	public int getOrderId() {
		return orderId;
	}


	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getRegion() {
		return region;
	}


	public void setRegion(String region) {
		this.region = region;
	}


	public DeliveryStatus getDeliveryStatus() {
		return deliveryStatus;
	}


	public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}


	public CustomerStatus getCustomerStatus() {
		return customerStatus;
	}


	public void setCustomerStatus(CustomerStatus customerStatus) {
		this.customerStatus = customerStatus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + orderId;
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
		DeliveryEntity other = (DeliveryEntity) obj;
		if (orderId != other.orderId)
			return false;
		return true;
	}
	

	
}
