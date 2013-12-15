/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.browserstack;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author meghadityc
 */
public class BrowserStack {
    private static final String ENDPOINT = "http://api.browserstack.com/3";
    private static final String VERSION = "3";
    private static final String WORKER_STRING = "worker/%s";
    private static final String WORKERS_STRING = "workers";
    private static final String STATUS_STRING = "status";
    private static final String VALIDATION_ERROR = "validation error";
    private static final String UNAUTHORIZED_USER = "unauthorized user";
    
    private String mUserName;
    private String mPassword;
    
    public BrowserStack(String username, String password) {
        this.mUserName = username;
        this.mPassword = password;
    }

    public String make_request(String url, String type) {
        switch(type) {
            case "GET":
                url = String.format("%s/%s", ENDPOINT, url);
                return sendGet(url);
            case "POST":
                url = String.format("%s/%s", ENDPOINT, url);
                return sendPost(url);
        }
        return "Malformed request";
    }
    
    private String sendGet(String url) {
        try{
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            String userpass = this.mUserName + ":" + this.mPassword;
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());

            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", basicAuth);

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();


            if (responseCode == 200) {
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            } else if (responseCode == 403 || responseCode == 422) {
                return VALIDATION_ERROR;
            } else if (responseCode == 401) {
                return UNAUTHORIZED_USER; 
            }

            in.close();
            // TODO: It might send an empty string back
            return response.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private String sendPost(String url) {
        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            String userpass = this.mUserName + ":" + this.mPassword;
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Authorization", basicAuth);
            
            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            if (responseCode == 200) {
                while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                }
            } else if (responseCode == 403 || responseCode == 422) {
                return VALIDATION_ERROR;
            } else if (responseCode == 401) {
                return UNAUTHORIZED_USER;
            }
            in.close();
            // TODO: It might send an empty string back
            return response.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public String get_browsers(boolean flat) {
        String url = "browsers";
        if (!flat) {
            url = "browsers?flat=true";
        }
        return this.make_request(url, "GET");
    }
    
    private String delete_worker(String id) {
        // TODO: To be handled in make_request
        // Currently we don't have support for HTTP DELETE
        return this.make_request(String.format(WORKER_STRING, id), "DELETE");
    }
    
    private String get_worker_status(String id) {
        return this.make_request(String.format(WORKER_STRING, id), "GET");
    }
    
    private String get_workers() {
        return this.make_request(WORKERS_STRING, "GET");
    }
    
    private String get_api_status() {
        return this.make_request(STATUS_STRING, "GET");
    }
}
