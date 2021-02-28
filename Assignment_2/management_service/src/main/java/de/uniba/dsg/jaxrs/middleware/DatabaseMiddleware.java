package de.uniba.dsg.jaxrs.middleware;

import com.google.gson.Gson;
import de.uniba.dsg.jaxrs.dto.BottleDto;
import de.uniba.dsg.jaxrs.dto.CrateDto;
import de.uniba.dsg.jaxrs.utils.DatabaseError;
import de.uniba.dsg.jaxrs.utils.Either;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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

    private final String databaseUrl;
    private final int databasePort;

    public DatabaseMiddleware() {
        if(System.getenv("DB_HOST") != null &&
                System.getenv("DB_PORT") != null) {
            this.databaseUrl = System.getenv("DB_HOST");
            this.databasePort = Integer.parseInt(System.getenv("DB_PORT"));
        } else {
            this.databaseUrl = "localhost";
            this.databasePort = 9999;
        }
    }

    // region Create
    public Either<DatabaseError, String> createBottle(BottleDto content) {
        Gson gson = new Gson();
        return connectToDbAndPost("http://" + databaseUrl + ":" + databasePort + "/v1/employee/bottles", gson.toJson(content));
    }

    public Either<DatabaseError, String> createCrate(CrateDto content) {
        Gson gson = new Gson();
        return connectToDbAndPost("http://" + databaseUrl + ":" + databasePort + "/v1/employee/crates", gson.toJson(content));
    }
    // endregion Create

    // region Read
    public Either<DatabaseError, String> getBottles() {
        return connectToDbAndGet("http://" + databaseUrl + ":" + databasePort + "/v1/employee/bottles");
    }

    public Either<DatabaseError, String> getBottleWithId(final int bottleId) {
        return connectToDbAndGet("http://" + databaseUrl + ":" + databasePort + "/v1/employee/bottles/" + bottleId);
    }

    public Either<DatabaseError, String> getCrates() {
        return connectToDbAndGet("http://" + databaseUrl + ":" + databasePort + "/v1/employee/crates");
    }

    public Either<DatabaseError, String> getCrateWithId(final int crateId) {
        return connectToDbAndGet("http://" + databaseUrl + ":" + databasePort + "/v1/employee/crates/" + crateId);
    }
    // endregion Read

    // region Update
    public Either<DatabaseError, String> editBottle(int bottleId, BottleDto content) {
        Gson gson = new Gson();
        return connectToDbAndPatch("http://" + databaseUrl + ":" + databasePort + "/v1/employee/bottles/" + bottleId, gson.toJson(content));
    }

    public Either<DatabaseError, String> editCrate(int crateId, CrateDto content) {
        Gson gson = new Gson();
        return connectToDbAndPatch("http://" + databaseUrl + ":" + databasePort + "/v1/employee/crates/" + crateId, gson.toJson(content));
    }
    // endregion Update

    // region Delete
    public Either<DatabaseError, String> deleteBottleWithId(final int bottleId) {
        return connectToDbAndDelete("http://" + databaseUrl + ":" + databasePort + "/v1/employee/bottles/" + bottleId);
    }

    public Either<DatabaseError, String> deleteCrateWithId(final int crateId) {
        return connectToDbAndDelete("http://" + databaseUrl + ":" + databasePort + "/v1/employee/crates/" + crateId);
    }
    // endregion Delete


    private Either<DatabaseError, String> connectToDbAndPost(String url, String content) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(url);
            request.setEntity(new StringEntity(content));
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            return getDatabaseErrorStringEither(client.execute(request));
        } catch (IOException e) {
            return Either.Left(new DatabaseError(500, "Maybe the database server is not even online"));
        }
    }

    private Either<DatabaseError, String> connectToDbAndGet(String url) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            return getDatabaseErrorStringEither(client.execute(request));
        } catch (IOException e) {
            return Either.Left(new DatabaseError(500, "Maybe the database server is not even online"));
        }
    }

    private Either<DatabaseError, String> connectToDbAndPatch(String url, String content) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPatch request = new HttpPatch(url);
            request.setEntity(new StringEntity(content));
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            return getDatabaseErrorStringEither(client.execute(request));
        } catch (IOException e) {
            return Either.Left(new DatabaseError(500, "Maybe the database server is not even online"));
        }
    }

    private Either<DatabaseError, String> connectToDbAndDelete(String url) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpDelete request = new HttpDelete(url);
            return getDatabaseErrorStringEither(client.execute(request));
        } catch (IOException e) {
            return Either.Left(new DatabaseError(500, "Maybe the database server is not even online"));
        }
    }

    private Either<DatabaseError, String> getDatabaseErrorStringEither(HttpResponse execute) throws IOException {
        HttpResponse httpResp = execute;

        int code = httpResp.getStatusLine().getStatusCode();

        final int SUCCESS = 2;
        if (statusPrefix(code) == SUCCESS) {
            if(httpResp.getEntity() != null) {
                String responseBody = EntityUtils.toString(httpResp.getEntity(), StandardCharsets.UTF_8);
                return Either.Right(responseBody);
            } else {
                return Either.Right("");
            }
        } else {
            return Either.Left(new DatabaseError(code, httpResp.getStatusLine().getReasonPhrase()));
        }
    }

    private int statusPrefix(int code) {
        return code / 100;
    }

}
