package com.example.PosBit.controller;

import com.example.PosBit.response.ResponeHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PosBit.model.CoudVendor;
import com.example.PosBit.service.CloudVendorService;

@RestController
@RequestMapping("/cloudvendor")
public class CloudVendorController {
	
	CloudVendorService cloudVendorService;
	
	public CloudVendorController(CloudVendorService cloudVendorService) {
		super();
		this.cloudVendorService = cloudVendorService;
	}

	@GetMapping("/{vendorId}")
	public ResponseEntity<Object> getCloudVendorDetails(@PathVariable("vendorId") String vendorId) {
        return ResponeHandler.responseBuilder("Requested vendor details are given here", HttpStatus.OK, cloudVendorService.getCloudVendor(vendorId));
	}
	
//	@GetMapping()
//	public List<CoudVendor> getAllCloudVendorDetails() {
//		return cloudVendorService.getAllCloudVendors();
//	}

    @GetMapping
    public ResponseEntity<Object> getAllCloudVendorDetails() {
        return ResponeHandler.responseBuilder(
                "All cloud vendor details are given here",
                HttpStatus.OK,
                cloudVendorService.getAllCloudVendors()
        );
    }

    @PostMapping
	public String createCoudVendorDetails(@RequestBody CoudVendor cloudVendor) {
		cloudVendorService.createCloudVendor(cloudVendor);
		return "Cloud Vendor created successfully";
	}

	@PutMapping
	public String updateCoudVendorDetails(@RequestBody CoudVendor cloudVendor) {
		cloudVendorService.updateCloudVendor(cloudVendor);
		return "Cloud Vendor updated successfully";
	}
	
	@DeleteMapping("/{vendorId}")
	public String deleteCoudVendorDetails(@PathVariable("vendorId") String vendorId) {
		cloudVendorService.deleteCloudVendor(vendorId);
		return "Cloud Vendor updated successfully";
	}
}
