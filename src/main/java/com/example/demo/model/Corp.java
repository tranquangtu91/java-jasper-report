package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Corp {
    private Long id;
    private String lookupCode;
    private String businessRegNumber;
    private String idCardNumber;
    private String corpName;
    private String address;
    private String status;
    private Date lookupUpdatedTime;
    private String taxCode;
    private String vat;
    private List<TaxType> taxType;
    private List<Corp> governing;
}
