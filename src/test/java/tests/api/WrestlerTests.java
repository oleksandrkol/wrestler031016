package tests.api;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import common.FileReader;
import common.PropertiesLoader;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class WrestlerTests {

    private Properties properties = new PropertiesLoader().loadProperties();
    private String LOGIN_URL = properties.getProperty("LOGIN_URL");
    private String CREATE_URL = properties.getProperty("CREATE_URL");
    private String DELETE_URL = properties.getProperty("DELETE_URL");
    private String READ_URL = properties.getProperty("READ_URL");
    private String UPDATE_URL = properties.getProperty("UPDATE_URL");


    private FileReader fileReader = new FileReader();
    private int wrestlerId;
    private String cookie;


    @Before
    public void login () {
        RestAssured.registerParser("text/html", Parser.JSON);
        String body = fileReader.fileToString("login.json");

        cookie =
        given().
                contentType("application/json").
                body(body).
        when().
                post(LOGIN_URL).
        then().
                extract().cookie("PHPSESSID");
    }

    @Test
    public void createWrestler() {
        String body = fileReader.fileToString("create.json");

        wrestlerId =
        given().
                contentType(ContentType.JSON).
                cookie("PHPSESSID", cookie).
                body(body).
        when().
                post(CREATE_URL).
        then().
                statusCode(200).
                body("result", equalTo(true)).
                extract().path("id");
    }

    @Test
    public void deleteWrestler () {
        createWrestler();

        given().
                contentType(ContentType.JSON).
                cookie("PHPSESSID", cookie).
        when().
                get(DELETE_URL + wrestlerId).
        then().
                statusCode(200).
                body("result", equalTo(true));
    }

    @Test
    public void readWrestler () {
        createWrestler();
        String template = fileReader.fileToString("read.template.json");
        String expectedBody = template.replaceAll("ID_PLACEHOLDER", String.valueOf(wrestlerId));

        given().
                contentType(ContentType.JSON).
                cookie("PHPSESSID", cookie).
        when().
                get(READ_URL + wrestlerId).
        then().
                statusCode(200).
                body(equalTo(expectedBody));
    }

    @Test
    public void updateWrestler () {
        createWrestler();
        String template = fileReader.fileToString("update.template.json");
        String body = template.replaceAll("ID_PLACEHOLDER", String.valueOf(wrestlerId));

        given().
                contentType(ContentType.JSON).
                cookie("PHPSESSID", cookie).
                body(body).
        when().
                post(UPDATE_URL).
        then().
                statusCode(200).
                body("result", equalTo(true));
    }

}