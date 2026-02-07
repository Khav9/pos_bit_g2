package com.example.PosBit.service;

import java.util.List;

import com.example.PosBit.model.CoudVendor;


public interface CloudVendorService {
	public String createCloudVendor(CoudVendor cloudVendor);
	public String updateCloudVendor(CoudVendor cloudVendor);
	public String deleteCloudVendor(String cloudVendorId);
	public CoudVendor getCloudVendor(String cloudVendorId);
	public List<CoudVendor> getAllCloudVendors();

}
