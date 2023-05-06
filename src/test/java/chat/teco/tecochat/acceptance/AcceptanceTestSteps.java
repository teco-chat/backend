package chat.teco.tecochat.acceptance;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import java.util.Base64;

public class AcceptanceTestSteps {

    public static RequestSpecification given() {
        return RestAssured
                .given().log().all()
                .contentType(APPLICATION_JSON_VALUE);
    }

    public static RequestSpecification given(final String name) {
        return RestAssured
                .given().log().all()
                .header(new Header("name", encode(name)))
                .contentType(APPLICATION_JSON_VALUE);
    }

    private static String encode(final String name) {
        return new String(Base64.getEncoder().encode(name.getBytes()));
    }
}
