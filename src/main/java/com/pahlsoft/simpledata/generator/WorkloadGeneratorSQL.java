package com.pahlsoft.simpledata.generator;

import com.pahlsoft.simpledata.clients.ClickHouseClientUtil;
import com.pahlsoft.simpledata.model.Workload;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WorkloadGeneratorSQL {
    WorkloadGeneratorSQL() {
        throw new IllegalArgumentException("WorkloadGeneratorSQL Class");
    }

    private static final Faker faker = new Faker(new Locale("en-US"));
    static Logger log = LoggerFactory.getLogger(ClickHouseClientUtil.class);

    public static String buildBulkRecord(Workload workload) {
        StringBuilder bulkRecord = new StringBuilder();
         bulkRecord.append("INSERT INTO ").append(workload.getDatabaseName()).append(".").append(workload.getTableName()).append(" ");
         bulkRecord.append(parseWorkload(workload, false));
        for (int bulkCount = 1; bulkCount < workload.getBackendBulkQueueDepth(); bulkCount++) {
            bulkRecord.append(",").append(parseWorkload(workload, true));
        }
        return bulkRecord.toString();
    }


    public static String buildSingleRecord(Workload workload) {
        StringBuilder singleRecord = new StringBuilder();
        singleRecord.append("INSERT INTO ").append(workload.getDatabaseName()).append(".").append(workload.getTableName()).append(" ");
        singleRecord.append(parseWorkload(workload, false));
        log.debug("SQL INSERT STATEMENT: " + singleRecord);
        return singleRecord.toString();
    }

    public static String buildCreateTableStatement(Workload workload) throws Exception {
        String tableCreateSQL = new String("CREATE TABLE ");
        String orderByFields = "ORDER BY (";

        // Append Database name and table name
        tableCreateSQL = tableCreateSQL + workload.getDatabaseName() + "." +workload.getTableName() + "\n";
        tableCreateSQL += "(\n";

        // Go through each field in the workload
        Iterator iterator = workload.getFields().iterator();
        Map<String, String> field;

        while (iterator.hasNext()) {
            field = (Map<String, String>) iterator.next();

            switch (field.get("type")) {
                case "double":
                    tableCreateSQL += field.get("name") + " Float64";
                    orderByFields = checkOrderbyFields(field, orderByFields);
                    break;
                case "float":
                    tableCreateSQL += field.get("name") + " Float32";
                    orderByFields = checkOrderbyFields(field, orderByFields);
                    break;
                case "boolean":
                    tableCreateSQL += field.get("name") + " Bool";
                    orderByFields = checkOrderbyFields(field, orderByFields);
                    break;
                case "full_name":
                case "last_name":
                case "first_name":
                case "full_address":
                case "street_address":
                case "city":
                case "country":
                case "country_code":
                case "state":
                case "phone_number":
                case "credit_card_number":
                case "ssn":
                case "product_name":
                case "group":
                case "uuid":
                case "path":
                case "hostname":
                case "appname":
                case "url":
                case "random_string_from_list":
                case "mac_address":
                case "email":
                case "domain":
                case "hash":
                case "random_cn_fact":
                case "random_got_character":
                case "random_occupation":
                case "iban":
                case "team_name":
                case "constant_string" :
                case "timezone":
                case "zipcode":
                    tableCreateSQL += field.get("name") + " String";
                    orderByFields = checkOrderbyFields(field, orderByFields);
                    break;
                case "int":
                case "random_integer_from_list":
                    tableCreateSQL += field.get("name") + " Int64";
                    orderByFields = checkOrderbyFields(field, orderByFields);
                    break;
                case "geo_point":
                    tableCreateSQL += field.get("name") + "Point";
                    orderByFields = checkOrderbyFields(field, orderByFields);
                    break;
                case "ipv4":
                    tableCreateSQL += field.get("name") + "IPv4";
                    orderByFields = checkOrderbyFields(field, orderByFields);
                    break;
                case "date":
                case "record_creation_time":
                case "timestamp":
                    tableCreateSQL += field.get("name") + "Date";
                    orderByFields = checkOrderbyFields(field, orderByFields);
                    break;
                default:
                    break;
            }

            if (iterator.hasNext()) {
                tableCreateSQL += ", \n";
            }

        }
        tableCreateSQL += "\n)\n";

        // Assign Engine
        tableCreateSQL += "ENGINE = " + workload.getBackendEngine() + "\n";
        // Close Order By Fields
        orderByFields += ")\n";
        // Attach Order By (As per Jake Vernon at Clickhouse)
        tableCreateSQL += orderByFields;

        return tableCreateSQL;
    }

    private static String checkOrderbyFields(Map<String, String> field, String primaryKeys) {
        if (field.get("primary_key") != null) {
            primaryKeys += field.get("name");
        }
        return primaryKeys;
    }

    private static String getRandomString(String[] listOfThings) {
        return listOfThings[getRandomInteger(0,listOfThings.length - 1)];
    }

    private static String getRandomValues(String[] listOfThings) {
        return listOfThings[getRandomInteger(0,listOfThings.length - 1)];
    }

    private static synchronized int getRandomInteger(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;

    }

    private static String escapeInternalApostrophes(String text) {
        if (text == null || text.length() <= 2) {
            return text;
        }

        String middle = text.substring(1, text.length() - 1);
        middle = middle.replaceAll("'", "\\\\'");

        return text.charAt(0) + middle + text.charAt(text.length() - 1);
    }

    private static String parseWorkload(Workload workload, boolean returnValuesOnly) {

        StringBuilder returnData = new StringBuilder();
        StringBuilder fieldNames = new StringBuilder();
        StringBuilder fieldValues = new StringBuilder();

        // Go through each field in the Workload
        Iterator iterator = workload.getFields().iterator();
        Map<String,String> field;

        while (iterator.hasNext()) {
            field = (Map<String,String>) iterator.next();
            switch(field.get("type")) {
                case "int":
                    if (field.get("range") != null) {
                        String[] range = field.get("range").split(",");
                        fieldNames.append(field.get("name"));
                        fieldValues.append(faker.number().numberBetween(Integer.valueOf(range[0]),Integer.valueOf(range[1])));
                    } else {
                        fieldNames.append(field.get("name"));
                        fieldValues.append(faker.number().numberBetween(0,65336));
                    }
                    break;
                case "float":
                    if (field.get("range") != null) {
                        String[] range = field.get("range").split(",");
                        fieldNames.append(field.get("name"));
                        fieldValues.append(faker.number().randomDouble(2,Integer.valueOf(range[0]),Integer.valueOf(range[1])));
                    } else {
                        fieldNames.append(field.get("name"));
                        fieldValues.append(faker.number().randomDouble(2,0,65336));
                    }
                    break;
                case "double":
                    if (field.get("range") != null) {
                        String[] range = field.get("range").split(",");
                        fieldNames.append(field.get("name"));
                        fieldValues.append(faker.number().randomDouble(0,Integer.valueOf(range[0]),Integer.valueOf(range[1])));
                    } else {
                        fieldNames.append(field.get("name"));
                        fieldValues.append(faker.number().randomDouble(0,1,1000000000));
                    }
                    break;
                case "boolean":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(faker.bool().bool());
                    break;
                case "full_name":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(escapeInternalApostrophes(faker.name().fullName())).append("' ");
                    break;
                case "first_name":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.name().firstName()).append("' ");
                    break;
                case "last_name":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(escapeInternalApostrophes(faker.name().lastName())).append("' ");
                    break;
                case "full_address":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(escapeInternalApostrophes(faker.address().fullAddress())).append("' ");
                    break;
                case "street_address":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(escapeInternalApostrophes(faker.address().streetAddress())).append("' ");
                    break;
                case "city":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.address().cityName()).append("' ");
                    break;
                case "country":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.address().country()).append("' ");
                    break;
                case "country_code":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.address().countryCode()).append("' ");
                    break;
                case "state":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.address().stateAbbr()).append("' ");
                    break;
                case "zipcode":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.address().zipCode()).append("' ");
                    break;
                case "geo_point":
                    fieldNames.append(field.get("name"));
                    fieldValues.append("(").append(faker.address().longitude()).append(",").append(faker.address().latitude()).append(")");
                    break;
                case "phone_number":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.phoneNumber().cellPhone()).append("' ");
                    break;
                case "credit_card_number":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.business().creditCardNumber()).append("' ");
                    break;
                case "ssn":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.idNumber().ssnValid()).append("' ");
                    break;
                case "product_name":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.commerce().productName()).append("' ");
                    break;
                case "group":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.commerce().department()).append("' ");
                    break;
                case "uuid":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(UUID.randomUUID());
                    break;
                case "path":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.file().fileName()).append("' ");
                    break;
                case "hostname":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.ancient().god()).append("' ");
                    break;
                case "appname":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.app().name()).append("' ");
                    break;
                case "url":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.internet().url()).append("' ");
                    break;
                case "random_string_from_list":
                    if (field.get("custom_list") != null) {
                        String[] range = field.get("custom_list").split(",");
                        fieldNames.append(field.get("name"));
                        fieldValues.append(" '").append(escapeInternalApostrophes(getRandomString(range))).append("' ");
                    } else {
                        log.error("Improper Mapping Definition");
                        System.exit(1);
                    }
                    break;
                case "random_integer_from_list":
                    if (field.get("custom_list") != null) {
                        String[] range = field.get("custom_list").split(",");
                        fieldNames.append(field.get("name"));
                        fieldValues.append(Integer.valueOf(getRandomValues(range)));
                    } else {
                        log.error("Improper Mapping Definition");
                        System.exit(1);
                    }
                    break;
                case "random_float_from_list":
                    if (field.get("custom_list") != null) {
                        String[] range = field.get("custom_list").split(",");
                        fieldNames.append(field.get("name"));
                        fieldValues.append(Float.valueOf(getRandomValues(range)));
                    } else {
                        log.error("Improper Mapping Definition");
                        System.exit(1);
                    }
                    break;
                case "random_long_from_list":
                    if (field.get("custom_list") != null) {
                        String[] range = field.get("custom_list").split(",");
                        fieldNames.append(field.get("name"));
                        fieldValues.append(Long.valueOf(getRandomValues(range)));
                    } else {
                        log.error("Improper Mapping Definition");
                        System.exit(1);
                    }
                    break;
                case "ipv4":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.internet().ipV4Address()).append("' ");
                    break;
                case "mac_address":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.internet().macAddress()).append("' ");
                    break;
                case "email":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.internet().emailAddress()).append("' ");
                    break;
                case "domain":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.internet().domainName()).append("' ");
                    break;
                case "hash":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.hashing().sha512()).append("' ");
                    break;
                case "random_cn_fact":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(escapeInternalApostrophes(faker.chuckNorris().fact())).append("' ");
                    break;
                case "random_got_character":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.gameOfThrones().character()).append("' ");
                    break;
                case "random_occupation":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.job().title()).append("' ");
                    break;
                case "iban":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.finance().iban()).append("' ");
                    break;
                case "team_name":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.team().name()).append("' ");
                    break;
                case "constant_string" :
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(escapeInternalApostrophes(field.get("value"))).append("' ");
                    break;
                case "date":
                case "timestamp":
                case "record_creation_time":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(new Date());
                    break;
                case "timezone":
                    fieldNames.append(field.get("name"));
                    fieldValues.append(" '").append(faker.address().timeZone()).append("' ");
                    break;
                default:
                    System.out.println("Warning: undetermined Type:" + field.get("name"));
                    log.info("Warning: undetermined type {} in workload. SDG may not function properly.",field.get("name"));
                    System.exit(1);
                    break;
            }

            if (iterator.hasNext()) {
                fieldNames.append(",");
                fieldValues.append(",");
            }
        }

        if (returnValuesOnly) {
            returnData.append("(").append(fieldValues).append(")");
        } else {
            returnData.append("(").append(fieldNames).append(")").append(" VALUES ").append("(").append(fieldValues).append(")");
        }
        return returnData.toString();
    }

}
