package com.dataarrangement.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/csv")
public class CsvUpload {
    @PostMapping("/upload")
    public ResponseEntity<String> uploadCSVFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validate if the uploaded file is a CSV
            if (!file.getContentType().equals("text/csv")) {
                return ResponseEntity.badRequest().body("Not a valid input file. Please upload a CSV file.");
            }

            // Process CSV and generate output
            String outputCsv = processCSV(file);

            // Set headers for the response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));  // Set Content-Type to text/csv
            headers.setContentDispositionFormData("attachment", "output.csv");  // Set filename for download

            // Return CSV content in the response with headers
            return new ResponseEntity<>(outputCsv, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing the CSV file.");
        }
    }

    private String processCSV(MultipartFile file) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        StringBuilder outputCsv = new StringBuilder();

        // Read the header line
        String header = reader.readLine();
        outputCsv.append("Domain,BL,DP,ABY,ACR").append(System.lineSeparator());

        // Create a map to store the column index for each keyword
        Map<String, Integer> columnIndexMap = new HashMap<>();
        columnIndexMap.put("Domain", -1);
        columnIndexMap.put("BL", -1);
        columnIndexMap.put("DP", -1);
        columnIndexMap.put("ABY", -1);
        columnIndexMap.put("ACR", -1);

        // Parse the header and find the column index for each keyword
        String[] headerColumns = header.split(",");
        for (int i = 0; i < headerColumns.length; i++) {
            for (String keyword : columnIndexMap.keySet()) {
                if (headerColumns[i].trim().equalsIgnoreCase(keyword)) {
                    columnIndexMap.put(keyword, i);
                }
            }
        }

        // Read and process each line
        String line;
        while ((line = reader.readLine()) != null) {
            String[] columns = line.split(",");
            outputCsv.append(columns[columnIndexMap.get("Domain")]).append(",");
            outputCsv.append(columns[columnIndexMap.get("BL")]).append(",");
            outputCsv.append(columns[columnIndexMap.get("DP")]).append(",");
            outputCsv.append(columns[columnIndexMap.get("ABY")]).append(",");
            outputCsv.append(columns[columnIndexMap.get("ACR")]).append(System.lineSeparator());
        }

        return outputCsv.toString();
    }
}
