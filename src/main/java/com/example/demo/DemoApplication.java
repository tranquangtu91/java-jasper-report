package com.example.demo;

import com.example.demo.model.Corp;
import com.example.demo.model.TaxType;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws IOException, JRException, ParseException {
		SpringApplication.run(DemoApplication.class, args);
//		InputStream reportStream = new ClassPathResource("templates/corp.jrxml").getInputStream();
//		JasperReport report = JasperCompileManager.compileReport(reportStream);
//		List<TaxType> taxTypes = new ArrayList<>();
//		taxTypes.addAll(List.of(
//				new TaxType("VAT", "Value Added Tax"),
//				new TaxType("CIT", "Corporate Income Tax"),
//				new TaxType("PIT", "Personal Income Tax")
//		));
//		List<Corp> governing = new ArrayList<>();
//		governing.add(new Corp(2L, "GLC123", "GBR456789", null, "Tổng công ty XYZ", "Tòa nhà PQR, Đường UVW, Quận Ba Đình, Hà Nội", "ACTIVE", null, "0123456789", "10%", null, null));
//		String dateString = "01/01/2021";
//		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//		Date lookupUpdatedTime = formatter.parse(dateString);
//		JRBeanCollectionDataSource taxTypeDataSet = new JRBeanCollectionDataSource(taxTypes);
//		JRBeanCollectionDataSource governingDataSet = new JRBeanCollectionDataSource(governing);
//		Map<String, Object> parameters = new HashMap<>();
//		parameters.put("lookupCode", "123456");
//		parameters.put("lookupName", "John Doe");
//		parameters.put("businessRegNumber", "1234567890");
//		parameters.put("lookupUpdatedTime", lookupUpdatedTime);
//		parameters.put("address", "123 Main St, Anytown, USA");
//		parameters.put("idCardNumber", "1234567890");
//		parameters.put("status", "ACTIVE");
//		parameters.put("taxTypeDataSet", taxTypeDataSet);
//		parameters.put("governing", governingDataSet);
//
//		JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
//
//		JasperExportManager.exportReportToPdfFile(jasperPrint, "D:\\jasperworkplace\\MyReports\\test.pdf");
//		System.out.println("Report generated successfully");
	}

}
