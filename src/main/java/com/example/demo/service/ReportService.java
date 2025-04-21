package com.example.demo.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    //Định nghĩa đường dẫn mặc định cho các file template .jrxml
    private static final String DEFAULT_TEMPLATE_PATH = "templates";

    //Sử dụng ResourceLoader của Spring để tải các resource từ classpath hoặc file system.
    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Hàm chính để tạo báo cáo PDF từ dữ liệu object và template tùy chọn.
     *
     * @param reportData   Dữ liệu báo cáo dưới dạng Object (có thể chứa các trường đơn và List).
     * @param templateName Tên file template .jrxml (ví dụ: company_report.jrxml).
     * @return Mảng byte của file PDF đã được tạo.
     * @throws Exception
     */
    public byte[] generatePdfReport(Object reportData, String templateName) throws Exception {
        return generatePdfReport(reportData, templateName, null); // Sử dụng đường dẫn template mặc định
    }

    /**
     * Hàm chính để tạo báo cáo PDF từ dữ liệu object và template tùy chọn, có thể chỉ định đường dẫn template.
     *
     * @param reportData    Dữ liệu báo cáo dưới dạng Object (có thể chứa các trường đơn và List).
     * @param templateName  Tên file template .jrxml (ví dụ: company_report.jrxml).
     * @param customTemplatePath Đường dẫn tùy chỉnh đến thư mục chứa template (nếu null sẽ dùng đường dẫn mặc định).
     * @return Mảng byte của file PDF đã được tạo.
     * @throws Exception
     */
    public byte[] generatePdfReport(Object reportData, String templateName, String customTemplatePath) throws Exception {
        InputStream reportStream = loadTemplate(templateName, customTemplatePath);
        JasperReport report = compileReport(reportStream);
        Map<String, Object> parameters = convertObjectToParameters(reportData);
        JasperPrint jasperPrint = fillReport(report, parameters);
        return exportToPdfBytes(jasperPrint);
    }

    /**
     * Lưu báo cáo PDF vào một thư mục chỉ định trên PC.
     *
     * @param reportData    Dữ liệu báo cáo dưới dạng Object.
     * @param templateName  Tên file template .jrxml.
     * @param outputPath    Đường dẫn thư mục để lưu file PDF.
     * @param outputFileName Tên file PDF sau khi lưu (ví dụ: company_report_2025.pdf).
     * @throws Exception
     */
    public String savePdfReport(Object reportData, String templateName, String outputPath, String outputFileName) throws Exception {
        return savePdfReport(reportData, templateName, null, outputPath, outputFileName);
    }

    /**
     * Lưu báo cáo PDF vào một thư mục chỉ định trên PC, có thể chỉ định đường dẫn template.
     *
     * @param reportData    Dữ liệu báo cáo dưới dạng Object.
     * @param templateName  Tên file template .jrxml.
     * @param customTemplatePath Đường dẫn tùy chỉnh đến thư mục chứa template.
     * @param outputPath    Đường dẫn thư mục để lưu file PDF.
     * @param outputFileName Tên file PDF sau khi lưu.
     * @throws Exception
     */
    public String savePdfReport(Object reportData, String templateName, String customTemplatePath, String outputPath, String outputFileName) throws Exception {
        InputStream reportStream = loadTemplate(templateName, customTemplatePath);
        JasperReport report = compileReport(reportStream);
        Map<String, Object> parameters = convertObjectToParameters(reportData);
        JasperPrint jasperPrint = fillReport(report, parameters);
        String filePath = exportToPdfFile(jasperPrint, outputPath, outputFileName);
        return filePath;
    }

    /**
     * Tải template .jrxml từ classpath hoặc đường dẫn tùy chỉnh.
     *
     * @param templateName       Tên file template.
     * @param customTemplatePath Đường dẫn tùy chỉnh (nếu có).
     * @return InputStream của file template.
     * @throws Exception
     */
    private InputStream loadTemplate(String templateName, String customTemplatePath) throws Exception {
        String path = (customTemplatePath != null && !customTemplatePath.isEmpty())
                ? customTemplatePath + "/" + templateName
                : DEFAULT_TEMPLATE_PATH + "/" + templateName;

        Resource resource = null;
        try {
            if (new File(path).isAbsolute()) { // Kiểm tra đường dẫn tuyệt đối
                resource = resourceLoader.getResource("file:" + path);
            } else {
                resource = resourceLoader.getResource("classpath:" + path);
            }

            if (resource == null || !resource.exists()) {
                throw new FileNotFoundException("Không tìm thấy template: " + path);
            }
            return resource.getInputStream();
        } catch (Exception e) {
            throw new Exception("Lỗi khi tải template: " + e.getMessage(), e);
        }
    }

    /**
     * Biên dịch file .jrxml thành đối tượng JasperReport.
     *
     * @param reportStream InputStream của file .jrxml.
     * @return Đối tượng JasperReport đã được biên dịch.
     * @throws JRException
     */
    private JasperReport compileReport(InputStream reportStream) throws JRException {
        return JasperCompileManager.compileReport(reportStream);
    }

    /**
     * Chuyển đổi một Object bất kỳ thành Map<String, Object> để truyền vào JasperReports.
     * Hàm này sẽ sử dụng Reflection để lấy tên trường và giá trị tương ứng.
     * Đối với các trường là List, nó sẽ được truyền trực tiếp.
     *
     * @param data Object dữ liệu.
     * @return Map chứa tên trường và giá trị.
     */
    private Map<String, Object> convertObjectToParameters(Object data) {
        Map<String, Object> parameters = new HashMap<>();
        if (data != null) {
            try {
                for (Field field : data.getClass().getDeclaredFields()) {
                    field.setAccessible(true); // Cho phép truy cập các trường private
                    Object fieldValue = field.get(data);
                    if (fieldValue instanceof List) {
                        parameters.put(field.getName() + "DataSet", new JRBeanCollectionDataSource((List<?>)fieldValue));
                    } else {
                        parameters.put(field.getName(), fieldValue);
                    }
                }
                // Xử lý các phương thức getter (nếu có) để lấy dữ liệu phức tạp hơn
                // getMethods(): Lấy tất cả phương thưc trong class, gồm cả những phương thức kế thừa từ superclass
                for (Method method : data.getClass().getMethods()) {
                    // Chỉ lấy các phương thức getter hoặc phương thức k có tham số(cũng chính là đăc trưng của hàm getter)
                    if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                        String key = method.getName().substring(3); // Loại bỏ "get"
                        key = key.substring(0, 1).toLowerCase() + key.substring(1); // Chuyển chữ cái đầu thành thường
                        Object methodValue = method.invoke(data);
                        if(methodValue instanceof List) {
                            parameters.put(key + "DataSet", new JRBeanCollectionDataSource((List<?>) methodValue));
                        } else {
                            parameters.put(key, methodValue);
                        }
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                // Log lỗi hoặc xử lý tùy theo yêu cầu
                e.printStackTrace();
            }
        }
        return parameters;
    }

    /**
     * Đổ dữ liệu vào report đã biên dịch.
     * Đối với các List trong parameters, JasperReports sẽ tự động hiểu và sử dụng làm datasource cho các subreport hoặc table.
     *
     * @param report     Đối tượng JasperReport đã biên dịch.
     * @param parameters Map chứa dữ liệu.
     * @return Đối tượng JasperPrint đã được đổ dữ liệu.
     * @throws JRException
     */
    private JasperPrint fillReport(JasperReport report, Map<String, Object> parameters) throws JRException {
        // Nếu có List trong parameters, JasperReports sẽ tự động coi nó như một CollectionDataSource
        return JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
    }

    /**
     * Xuất đối tượng JasperPrint thành mảng byte PDF để preview trên trình duyệt.
     *
     * @param jasperPrint Đối tượng JasperPrint đã được đổ dữ liệu.
     * @return Mảng byte của file PDF.
     * @throws JRException
     */
    private byte[] exportToPdfBytes(JasperPrint jasperPrint) throws JRException {
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    /**
     * Xuất đối tượng JasperPrint thành file PDF và lưu vào đường dẫn chỉ định.
     *
     * @param jasperPrint    Đối tượng JasperPrint đã được đổ dữ liệu.
     * @param outputPath     Đường dẫn thư mục để lưu file.
     * @param outputFileName Tên file PDF.
     * @throws JRException
     * @throws java.io.IOException
     */
    private String exportToPdfFile(JasperPrint jasperPrint, String outputPath, String outputFileName) throws JRException, IOException {
        String finalOutputPath;
        String finalOutputFileName;

        // Xác định đường dẫn đầu ra
        if (isNullOrEmpty(outputPath)) {
            finalOutputPath = "C:/pdfReport/"; // Đường dẫn mặc định, thêm dấu "/" để dễ xử lý
            File defaultDir = new File(finalOutputPath);
            if (!defaultDir.exists()) {
                defaultDir.mkdirs();
            }
        } else {
            File outputDir = new File(outputPath);
            if (outputDir.exists() && !outputDir.isDirectory()) {
                throw new IOException("Đường dẫn thư mục đầu ra không hợp lệ: " + outputPath + " không phải là một thư mục.");
            }
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            finalOutputPath = outputPath.endsWith(File.separator) ? outputPath : outputPath + File.separator;
        }
        // Xác định tên file đầu ra
        String baseFileName;
        String extension = ".pdf";

        if (isNullOrEmpty(outputFileName)) {
            baseFileName = "corpDetalReport";
        } else {
            if (outputFileName.toLowerCase().endsWith(extension)) {
                baseFileName = outputFileName.substring(0, outputFileName.length() - extension.length());
            } else {
                baseFileName = outputFileName;
            }
        }
        finalOutputFileName = generateUniqueFileName(finalOutputPath, baseFileName, extension);
        String filePath = finalOutputPath + finalOutputFileName;
        JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
        return filePath;
    }

    public byte[] generateUserReport(String username) throws Exception {
        // Load .jrxml
        InputStream reportStream = new ClassPathResource("templates/name.jrxml").getInputStream();

        // Compile
        JasperReport report = JasperCompileManager.compileReport(reportStream);

        // Params
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", username);

        // Fill report (no DB)
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

        // Export PDF (OpenPDF được JasperReports sử dụng nội bộ nếu có mặt)
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private String generateUniqueFileName(String directory, String baseName, String extension) {
        String fileName = baseName + extension;
        File file = new File(directory, fileName);
        int counter = 1;
        while (file.exists()) {
            fileName = baseName + counter + extension;
            file = new File(directory, fileName);
            counter++;
        }
        return fileName;
    }

}