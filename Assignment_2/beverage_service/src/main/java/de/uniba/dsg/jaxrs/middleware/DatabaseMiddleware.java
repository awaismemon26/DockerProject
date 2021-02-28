package de.uniba.dsg.jaxrs.middleware;

import de.uniba.dsg.jaxrs.utils.DatabaseError;
import de.uniba.dsg.jaxrs.utils.Either;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// The idea for Either usage is following:
// - When we get the result successfully, the use the "Right" constructor to signal success and put the data there
// - When the database failed to execute the query, we put the HTTP status code in the "Left" constructor and just
//   forward this error code
//
// This way, we can be more lazy in the controller implementation about error handling :)
public class DatabaseMiddleware {

    private String databaseUrl;
    private int databasePort;

    public DatabaseMiddleware() {
        this.databaseUrl = "localhost";
        this.databasePort = 9999;

        if(System.getenv("DB_HOST") != null) this.databaseUrl = System.getenv("DB_HOST");
        if(System.getenv("DB_PORT") != null) this.databasePort = Integer.parseInt(System.getenv("DB_PORT"));
    }

    public Either<DatabaseError, String> getBottles() {
        return connectToDb("http://" + databaseUrl + ":" + databasePort + "/v1/customer/bottles");
    }

    public Either<DatabaseError, String> getBottleWithId(final int bottleId) {
        return connectToDb("http://" + databaseUrl + ":" + databasePort + "/v1/customer/bottles/" + bottleId);
    }

    public Either<DatabaseError, String> getCrates() {
        return connectToDb("http://" + databaseUrl + ":" + databasePort + "/v1/customer/crates");
    }

    public Either<DatabaseError, String> getCrateWithId(final int crateId) {
        return connectToDb("http://" + databaseUrl + ":" + databasePort + "/v1/customer/crates/" + crateId);
    }

    private Either<DatabaseError, String> connectToDb(String url) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet response = new HttpGet(url);
            HttpResponse httpResp = client.execute(response);

            int code = httpResp.getStatusLine().getStatusCode();

            final int SUCCESS = 2;
            if(statusPrefix(code) == SUCCESS) {
                String responseBody = EntityUtils.toString(httpResp.getEntity(), StandardCharsets.UTF_8);
                return Either.Right(responseBody);
            } else {
                return Either.Left(new DatabaseError(code, httpResp.getStatusLine().getReasonPhrase()));
            }
        } catch (IOException e) {
            return Either.Left(new DatabaseError(500, "Maybe the database server is not even online"));
        }
    }

    private int statusPrefix(int code) {
        return code / 100;
    }
}
