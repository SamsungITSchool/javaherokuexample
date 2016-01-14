/**
 * Created by raiym on 1/13/16.
 */

import com.google.gson.Gson;
import model.Sql2oUser;
import model.User;
import net.CustomResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.sql2o.Sql2o;
import org.sql2o.converters.UUIDConverter;
import org.sql2o.quirks.PostgresQuirks;
import spark.Spark;

import java.util.UUID;

import static spark.Spark.get;
import static spark.Spark.post;


public class SparkHerokuApp {
    private static String dbHost = "ec2-107-21-223-110.compute-1.amazonaws.com";
    private static String dbPort = "5432";
    private static String dbName = "d9drs0g01eqeir";
    private static String dbUsername = "xdmfdolmqushkf";
    private static String dbPassword = "iBwpLgt1wIhrSZa5cPi7FIW_Op";
    private static Gson gson;

    public static void main(String[] args) {
        gson = new Gson();
        Spark.staticFileLocation("/public");

        Sql2o sql2o = new Sql2o("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName + "?sslmode=require",
                dbUsername, dbPassword, new PostgresQuirks() {
            {
                // make sure we use default UUID converter.
                converters.put(UUID.class, new UUIDConverter());
            }
        });
        Sql2oUser sql2oUser = new Sql2oUser(sql2o);

        post("api/signup", (req, res) -> {
            User user = gson.fromJson(req.body(), User.class);
            if (user == null) {
                return gson.toJson(new CustomResponse<>(1, "Invalid parameters are set. Failed to parse user data.", null));
            }
            if (!user.isDataValid()) {
                return gson.toJson(new CustomResponse<>(1, "Invalid email/password. Please check your input.", null));
            }
            User foundUser = sql2oUser.findUserByEmail(user.email);
            if (foundUser != null) {
                return gson.toJson(new CustomResponse<>(1, "User already exists.", null));
            }
            user.token = RandomStringUtils.randomAlphanumeric(45);
            sql2oUser.createUser(user);
            return gson.toJson(new CustomResponse<>(0, "Sign Up successful", null));
        });
        post("api/login", (req, res) -> {
            User user = gson.fromJson(req.body(), User.class);
            if (user == null) {
                return gson.toJson(new CustomResponse<>(1, "Invalid parameters are set. Failed to parse user data.", null));
            }
            if (!user.isDataValid()) {
                return gson.toJson(new CustomResponse<>(1, "Invalid email/password. Please check your input.", null));
            }
            User foundUser = sql2oUser.findUserByEmail(user.email);
            if (foundUser == null) {
                return gson.toJson(new CustomResponse<>(1, "No user with this login and password.", null));
            }
            System.out.println("Candidate password: " + foundUser.isPasswordValid(user.password));

            if (!foundUser.isPasswordValid(user.password)) {
                return gson.toJson(new CustomResponse<>(1, "Password is wrong.", null));
            }
            return gson.toJson(new CustomResponse<>(0, "Login successful", foundUser));
        });

        get("api/getAnswer", (req, res) -> {
            if (req.queryParams("token") == null) {
                return gson.toJson(new CustomResponse<>(1, "Invalid user input.", null));
            }
            String token = req.queryParams("token");
            System.out.println("Token: " + token);
            User user = sql2oUser.findByToken(token);
            if (user == null) {
                return gson.toJson(new CustomResponse<>(1, "Only a believer pass. You will suffer the punishment of God, stranger...", null));
            }
            return gson.toJson(new CustomResponse<>(1, "Answer: Life is about playing your best hand, with the cards you are dealt.", null));
        });
    }
}
