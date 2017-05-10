package com.qt.processor;

import com.qt.domain.ClinicItem;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by asrivastava on 5/9/17.
 */
@Component
public class ClinicItemProcessor implements ItemProcessor<ClinicItem, ClinicItem> {

    private static final Logger log = LoggerFactory.getLogger(ClinicItemProcessor.class);

    /**
     * This function is used to generate a unique MD5 hash using the lowercase of
     * name_1, name_2 and zipcode. This will ensure better matches when Quartet clinics
     * are being checked for mapping.
     * @param clinicItem
     * @return
     * @throws Exception
     */
    @Override
    public ClinicItem process(final ClinicItem clinicItem) throws Exception {
        clinicItem.setHash_id(generateIdFromFields(clinicItem.getName_1(), clinicItem.getName_2(), clinicItem.getZip()));
        log.info("Adding id to "+ clinicItem.getId());
        return clinicItem;
    }

    private static String generateIdFromFields(String name1, String name2, String zip) {
        return DigestUtils.md5Hex(name1.toLowerCase()+name2.toLowerCase()+zip.toLowerCase());
    }

}
