
package com.advanced.tatoc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;


public class AdvancedTatoc {

	public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException, IOException, JSONException {
		WebDriver driver= new FirefoxDriver();
		//starting off
		driver.get("http://10.0.1.86");
		WebElement t= driver.findElement(By.xpath("//a[@href='/tatoc']"));
		t.click();
		driver.findElement(By.xpath("//a[@href='/tatoc/advanced']")).click();
		Actions action = new Actions(driver);
		WebElement we = driver.findElement(By.className("menutitle"));
		action.moveToElement(we).moveToElement(driver.findElement(By.className("menutitle"))).click().build().perform();
		
		driver.findElement(By.xpath("//span[contains(text(),'Go Next')]")).click();
		//task_2
		String sym = driver.findElement(By.xpath("//div[@id='symboldisplay']")).getText();
		Class.forName("com.mysql.jdbc.Driver"); 
		Connection con = DriverManager.getConnection("jdbc:mysql://10.0.1.86:3306/tatoc", "tatocuser", "tatoc01" );
		PreparedStatement w = con.prepareStatement("SELECT * from credentials ");
		ResultSet rs= w.executeQuery();
		ArrayList<String> Name = new ArrayList<String>();
		while (rs.next()) {
		    Name.add(rs.getString("Name"));
		}
		PreparedStatement p = con.prepareStatement("SELECT * from credentials");
		ResultSet rp= p.executeQuery();
		ArrayList<String> Passkey = new ArrayList<String>();
		while (rp.next()) {
		    Passkey.add(rp.getString("Passkey"));
		}
		int x= sym.charAt(0)-65;
	    driver.findElement(By.id("name")).sendKeys(Name.get(x));
	    driver.findElement(By.id("passkey")).sendKeys(Passkey.get(x));
	    driver.findElement(By.id("submit")).click();
	    
	    //task 3
	    JavascriptExecutor js = (JavascriptExecutor) driver;  
	    Thread.sleep(4000);
	    js.executeScript("player.playMovie();");
	    String f= (String) js.executeScript("player.getTotalTime().toString()");
	    System.out.println(f);
	    //System.out.println(js.executeScript("player.getTotalTime();")); 
	    Thread.sleep(26000);
	    driver.findElement(By.xpath("//a[@href='#']")).click();
	    
    
	    //task 4
	    String sessid = driver.findElement(By.id("session_id")).getText();
        sessid = sessid.substring(12,sessid.length());
        String Resturl = "http://10.0.1.86/tatoc/advanced/rest/service/token/"+sessid;

        URL url = new URL(Resturl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String res=new String(response);
        
        JSONObject obj=new JSONObject(res);
        res=(String) obj.get("token");
        
        URL url1 = new URL("http://10.0.1.86/tatoc/advanced/rest/service/register");
        HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
        

        conn1.setRequestMethod("POST");
        
        conn1.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "id="+sessid+"&signature="+res+"&allow_access=1";
        
        conn1.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(conn1.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = conn1.getResponseCode();
        
        conn1.disconnect();
        driver.findElement(By.cssSelector(".page a")).click();
        
        //Part 5
        driver.findElement(By.linkText("Download File")).click();
        Thread.sleep(5000);
        BufferedReader br = null;
        String strng=null, sCurrentLine;
        try 
        {
            int i=0;
            br = new BufferedReader(new FileReader("file_handle_test.dat"));
            while ((sCurrentLine = br.readLine()) != null) 
            {
                if(i==2)
                    strng = sCurrentLine;
                i++;
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        strng = strng.substring(11,strng.length());
        driver.findElement(By.id("signature")).sendKeys(strng);
        driver.findElement(By.className("submit")).click();
	    
	    
	   
	   
	
	}
	    
	
}


