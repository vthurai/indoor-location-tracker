package com.main.config;

import com.google.api.client.util.Strings;
import com.google.common.base.Preconditions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SelectStatement {

    private String selectQuery;
    
    public SelectStatement(String projectId, String datasetId, String tableId) {
        this.selectQuery = String.format("SELECT * FROM `%s.%s.%s`", projectId, datasetId, tableId);
    }
    
    public String select(String startDate, String finishDate) {
        if (!Strings.isNullOrEmpty(finishDate)) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(startDate),
                    "Start date must be populated if Finish date is populated'");
            return String.format("%s WHERE timecollected >= '%s' AND timecollected <= '%s'", 
                    selectQuery, startDate, finishDate);
        } else if (!Strings.isNullOrEmpty(startDate)) {
            return String.format("%s WHERE timecollected >= '%s'", selectQuery, startDate);
        } else {
            return selectQuery;
        }
        
    }
}
