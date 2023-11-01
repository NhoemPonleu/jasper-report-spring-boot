package com.codingboot.controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.codingboot.service.serviceImpl.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.codingboot.entity.Product;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoice")
@RequiredArgsConstructor
public class InvoiceController {
	private final ProductService productService;

	@GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> downloadInvoice() throws JRException, IOException {
		List<Product> products = productService.getAllProduct();

		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(products);

		float totalPrice = 0;
		for (Product product : products) {
			totalPrice += product.getPrice();
		}

		String totalPriceString = String.valueOf(totalPrice);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("total", totalPriceString + "$");

		JasperReport compileReport = JasperCompileManager
				.compileReport(new FileInputStream("src/main/resources/invoice.jrxml"));

		JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, parameters, beanCollectionDataSource);

		byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");
		String destinationFolderPath = "C:\\Users\\Ponleu\\Desktop\\reports";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
		String formattedDateTime = LocalDateTime.now().format(formatter);
		String fileName = "invoice_" + formattedDateTime + ".pdf";
		Path filePath = Path.of(destinationFolderPath, fileName);
		Files.createDirectories(filePath.getParent());
		Files.copy(new ByteArrayInputStream(data), filePath, StandardCopyOption.REPLACE_EXISTING);

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
	}
}