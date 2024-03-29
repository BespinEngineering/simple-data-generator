package com.pahlsoft.simpledata.generator;


import com.pahlsoft.simpledata.model.Workload;
import net.datafaker.Faker;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.*;

public class WorkloadGeneratorJSON {

    WorkloadGeneratorJSON() {
        throw new IllegalStateException("WorkloadGeneratorJSON class");
    }

    private static final Faker faker = new Faker(new Locale("en-US"));

    @SuppressWarnings("unchecked")
    public static Map<String, Object> buildDocument(Workload workload) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("record_creation_time", new Date());
        // Go through each field in the workload
        Iterator iterator = workload.getFields().iterator();
        Map<String,String> field;

        while (iterator.hasNext()) {
            field = (Map<String,String>) iterator.next();
            switch(field.get("type")){
                case "empty":
                    jsonMap.put(field.get("name"),"");
                    break;
                case "int":
                    if (field.get("range") != null) {
                        String[] range = field.get("range").split(",");
                        jsonMap.put(field.get("name"),faker.number().numberBetween(Integer.valueOf(range[0]),Integer.valueOf(range[1])));
                    } else {
                        jsonMap.put(field.get("name"),faker.number().numberBetween(0,65336));
                    }
                    break;
                case "float":
                    if (field.get("range") != null) {
                        String[] range = field.get("range").split(",");
                        jsonMap.put(field.get("name"),faker.number().randomDouble(2,Integer.valueOf(range[0]),Integer.valueOf(range[1])));
                    } else {
                        jsonMap.put(field.get("name"),faker.number().randomDouble(2,0,65336));
                    }
                    break;
                case "double":
                    if (field.get("range") != null) {
                        String[] range = field.get("range").split(",");
                        jsonMap.put(field.get("name"),faker.number().randomDouble(0,Integer.valueOf(range[0]),Integer.valueOf(range[1])));
                    } else {
                        jsonMap.put(field.get("name"),faker.number().randomDouble(0,1,1000000000));
                    }
                    break;
                case "boolean":
                    jsonMap.put(field.get("name"),faker.bool().bool());
                    break;
                case "full_name":
                    jsonMap.put(field.get("name"),faker.name().fullName());
                     break;
                case "first_name":
                    jsonMap.put(field.get("name"),faker.name().firstName());
                    break;
                case "last_name":
                    jsonMap.put(field.get("name"),faker.name().lastName());
                    break;
                case "full_address":
                    jsonMap.put(field.get("name"),faker.address().fullAddress());
                    break;
                case "street_address":
                    jsonMap.put(field.get("name"),faker.address().streetAddress());
                    break;
                case "city":
                    jsonMap.put(field.get("name"),faker.address().cityName());
                    break;
                case "country":
                    jsonMap.put(field.get("name"),faker.address().country());
                    break;
                case "country_code":
                    jsonMap.put(field.get("name"),faker.address().countryCode());
                    break;
                case "state":
                    jsonMap.put(field.get("name"),faker.address().stateAbbr());
                    break;
                case "zipcode":
                    jsonMap.put(field.get("name"),faker.address().zipCode());
                    break;
                case "geo_point":
                    jsonMap.put("geo_point","{\"lon:\" " + faker.address().longitude() + " }, {\"lat:\" " + faker.address().longitude() + "}");
                    break;
                case "phone_number":
                    jsonMap.put(field.get("name"),faker.phoneNumber().cellPhone());
                    break;
                case "credit_card_number":
                    jsonMap.put(field.get("name"),faker.business().creditCardNumber());
                    break;
                case "ssn":
                    jsonMap.put(field.get("name"), faker.idNumber().ssnValid());
                    break;
                case "product_name":
                    jsonMap.put(field.get("name"),faker.commerce().productName());
                    break;
                case "group":
                    jsonMap.put(field.get("name"),faker.commerce().department());
                    break;
                case "uuid":
                    jsonMap.put(field.get("name"),UUID.randomUUID().toString());
                    break;
                case "path":
                    jsonMap.put(field.get("name"),faker.file().fileName());
                    break;
                case "hostname":
                    jsonMap.put(field.get("name"),faker.ancient().god());
                    break;
                case "appname":
                    jsonMap.put(field.get("name"),faker.app().name());
                    break;
                case "url":
                    jsonMap.put(field.get("name"),faker.internet().url());
                    break;
                case "random_string_from_list":
                    if (field.get("custom_list") != null) {
                        String[] range = field.get("custom_list").split(",");

                        jsonMap.put(field.get("name"),getRandomString(range));
                    } else {
                        System.out.println("Improper Mapping Definition");
                    }
                    break;
                case "random_integer_from_list":
                    if (field.get("custom_list") != null) {
                        String[] range = field.get("custom_list").split(",");
                        jsonMap.put(field.get("name"),Integer.valueOf(getRandomValues(range)));
                    } else {
                        System.out.println("Improper Mapping Definition");
                    }
                    break;
                case "random_float_from_list":
                    if (field.get("custom_list") != null) {
                        String[] range = field.get("custom_list").split(",");

                        jsonMap.put(field.get("name"),Float.valueOf(getRandomValues(range)));
                    } else {
                        System.out.println("Improper Mapping Definition");
                    }
                    break;
                case "random_long_from_list":
                    if (field.get("custom_list") != null) {
                        String[] range = field.get("custom_list").split(",");

                        jsonMap.put(field.get("name"),Long.valueOf(getRandomValues(range)));
                    } else {
                        System.out.println("Improper Mapping Definition");
                    }
                    break;
                case "ipv4":
                    jsonMap.put(field.get("name"),faker.internet().ipV4Address());
                    break;
                case "mac_address":
                    jsonMap.put(field.get("name"),faker.internet().macAddress());
                    break;
                case "email":
                    jsonMap.put(field.get("name"),faker.internet().emailAddress());
                    break;
                case "domain":
                    jsonMap.put(field.get("name"),faker.internet().domainName());
                    break;
                case "hash":
                    jsonMap.put(field.get("name"),faker.hashing().sha512());
                    break;
                case "random_cn_fact":
                    jsonMap.put(field.get("name"),faker.chuckNorris().fact());
                    break;
                case "random_got_character":
                    jsonMap.put(field.get("name"),faker.gameOfThrones().character());
                    break;
                case "random_occupation":
                    jsonMap.put(field.get("name"),faker.job().title());
                    break;
                case "iban":
                    jsonMap.put(field.get("name"),faker.finance().iban());
                    break;
                case "team_name":
                    jsonMap.put(field.get("name"),faker.team().name());
                    break;
                case "constant_string" :
                    jsonMap.put(field.get("name"),field.get("value"));
                    break;
                case "date":
                case "timestamp":
                case "record_creation_time":
                    jsonMap.put(field.get("name"),new Date());
                    break;
                case "timezone":
                    jsonMap.put(field.get("name"),faker.address().timeZone());
                    break;
                default:
                    jsonMap.put("unmapped type","unmapped type");
                    System.out.println("Warning: unmapped Type: " + field.get("name"));
                    break;
            }

        }

        return jsonMap;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject buildMapping(Workload workload) throws JSONException {
        JSONObject mappingsObject = new JSONObject();
        JSONObject propertiesObject = new JSONObject();

        // Go through each field in the workload
        Iterator iterator = workload.getFields().iterator();
        Map<String, String> field;

        while (iterator.hasNext()) {
            field = (Map<String,String>) iterator.next();

            switch(field.get("type")){
                case "double":
                    Map<String,String> doubleField = new HashMap<>();
                    doubleField.put("type","double");
                    propertiesObject.put(field.get("name"),doubleField);
                    break;
                case "float":
                case "random_float_from_list":
                    Map<String,String> floatField = new HashMap<>();
                    floatField.put("type","float");
                    propertiesObject.put(field.get("name"),floatField);
                    break;
                case "random_long_from_list":
                    Map<String,String> longField = new HashMap<>();
                    longField.put("type","long");
                    propertiesObject.put(field.get("name"),longField);
                    break;
                case "boolean":
                    Map<String,String> booleanField = new HashMap<>();
                    booleanField.put("type","boolean");
                    propertiesObject.put(field.get("name"),booleanField);
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
                    Map<String,String> fullNameField = new HashMap<>();
                    fullNameField.put("type","text");
                    propertiesObject.put(field.get("name"),fullNameField);
                    break;
                case "int":
                case "random_integer_from_list":
                    Map<String,String> integerField = new HashMap<>();
                    integerField.put("type","integer");
                    propertiesObject.put(field.get("name"),integerField);
                    break;
                case "geo_point":
                    Map<String,String> geoPointField = new HashMap<>();
                    geoPointField.put("type","geo_point");
                    propertiesObject.put(field.get("name"),geoPointField);
                    break;
                case "ipv4":
                    Map<String,String> ipField = new HashMap<>();
                    ipField.put("type","ip");
                    propertiesObject.put(field.get("name"),ipField);
                    break;
                case "date":
                case "record_creation_time":
                case "timestamp":
                    Map<String,String> dateField = new HashMap<>();
                    dateField.put("type","date");
                    propertiesObject.put(field.get("name"),dateField);
                    break;
                default:
                    Map<String,String> keywordField = new HashMap<>();
                    keywordField.put("type","keyword");
                    propertiesObject.put(field.get("name"),keywordField);
                    break;
            }
        }

        mappingsObject.put("properties",propertiesObject);
        return mappingsObject;
    }


    public static JSONObject buildSettings(Workload workload) throws JSONException {
        JSONObject indexObject = new JSONObject();
        JSONObject settingsObject = new JSONObject();

        indexObject.put("number_of_shards", workload.getPrimaryShardCount());
        indexObject.put("number_of_replicas",workload.getReplicaShardCount());

        settingsObject.put("index",indexObject);

        return settingsObject;
    }

    private static String getRandomString(String[] listOfThings) {
        return listOfThings[getRandomInteger(0,listOfThings.length - 1)];
    }

    private static String getRandomValues(String[] listOfThings) {
        return listOfThings[getRandomInteger(0,listOfThings.length - 1)];
    }

    private static synchronized int getRandomInteger(int min, int max) {
        SecureRandom rand = new SecureRandom();
        return rand.nextInt((max - min) + 1) + min;

    }


}
