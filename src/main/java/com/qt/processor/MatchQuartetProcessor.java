package com.qt.processor;

import com.qt.domain.ClinicItem;
import com.qt.domain.MatchedClinic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by asrivastava on 5/9/17.
 */
@Component
public class MatchQuartetProcessor implements ItemProcessor<ClinicItem, MatchedClinic> {

    private static final Logger log = LoggerFactory.getLogger(MatchQuartetProcessor.class);
    Connection conn = null;
    PreparedStatement pstmt= null;

    @Autowired
    @Qualifier("qtDataSource")
    public DataSource qtDataSource;

    public String select_match_sql = "SELECT COUNT(1) as active, ? as id FROM clinic_data WHERE name_1 = ? AND name_2 = ? AND zip = ?";

    @Override
    public MatchedClinic process(final ClinicItem clinicItem) throws Exception {
        MatchedClinic clinic = new MatchedClinic();
        conn = qtDataSource.getConnection();
        try{
            pstmt = conn.prepareStatement(select_match_sql);
            pstmt.setString(1, clinicItem.getHash_id());
            pstmt.setString(2, clinicItem.getName_1());
            pstmt.setString(3, clinicItem.getName_2());
            pstmt.setString(4, clinicItem.getZip());
            log.info(pstmt.toString());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            clinic.setHash_id((String)rs.getObject("id"));
            clinic.setActive((Long)rs.getObject("active"));

        } catch (Exception e) {
            log.error("Error in executing query");
        }finally{
            if(pstmt!=null)
                pstmt.close();
            if(conn!=null)
                conn.close();
      }

        return clinic;
    }



}