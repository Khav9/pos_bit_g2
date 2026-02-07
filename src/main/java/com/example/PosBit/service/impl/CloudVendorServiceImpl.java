package com.example.PosBit.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PosBit.exception.CloudVendorNotFoundException;
import com.example.PosBit.model.CoudVendor;
import com.example.PosBit.repository.CloudVendorRepository;
import com.example.PosBit.service.CloudVendorService;


@Service
public class CloudVendorServiceImpl implements CloudVendorService {

	CloudVendorRepository cloudVendorRepository;
	
	public CloudVendorServiceImpl(CloudVendorRepository cloudVendorRepository) {
		this.cloudVendorRepository = cloudVendorRepository;
	}

	@Override
	public String createCloudVendor(CoudVendor cloudVendor) {
		// TODO Auto-generated method stub
		cloudVendorRepository.save(cloudVendor);
		return "Success";
	}

	@Override
	public String updateCloudVendor(CoudVendor cloudVendor) {
		// TODO Auto-generated method stub
		cloudVendorRepository.save(cloudVendor);
		return "Success";
	}

	@Override
	public String deleteCloudVendor(String cloudVendorId) {
		// TODO Auto-generated method stub
		cloudVendorRepository.deleteById(cloudVendorId);
		return "Success";
	}

	@Override
	public CoudVendor getCloudVendor(String cloudVendorId) {
		// TODO Auto-generated method stub
		if (cloudVendorRepository.findById(cloudVendorId).isEmpty())
			throw new CloudVendorNotFoundException("Cloud Venodr does not exist");
		return cloudVendorRepository.findById(cloudVendorId).get();
	}

	@Override
	public List<CoudVendor> getAllCloudVendors() {
		// TODO Auto-generated method stub
		return cloudVendorRepository.findAll();
	}

}
