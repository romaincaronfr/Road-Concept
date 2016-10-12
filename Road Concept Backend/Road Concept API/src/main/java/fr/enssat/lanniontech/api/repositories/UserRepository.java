package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.exceptions.database.DatabaseOperationException;
import fr.enssat.lanniontech.api.repositories.connectors.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository extends AbstractRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);

    private static final String INSERT = "INSERT INTO final_user(email, password, first_name, last_name, type) VALUES (?, ?, ?, ?, ?) RETURNING id";
    private static final String SELECT_FROM_ID = "SELECT email, password, first_name, last_name, type FROM final_user WHERE id = ?";
    private static final String SELECT_FROM_EMAIL = "SELECT id, password, first_name, last_name, type FROM final_user WHERE email = ?";
    private static final String SELECT_ALL = "SELECT id, email, password, first_name, last_name, type FROM final_user";

    // ======
    // CREATE
    // ======

    public User create(String email, String lastName, String firstName, String password, UserType type) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
                statement.setString(1, email);
                statement.setString(2, password);
                statement.setString(3, firstName);
                statement.setString(4, lastName);
                statement.setInt(5, type.getJsonID());

                try (ResultSet result = statement.executeQuery()) { //TODO: What if email already exists ?
                    result.next(); // Has exactly one row
                    User user = new User();
                    user.setId(result.getInt("id"));
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setType(type);
                    return user;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, User.class);
        }
    }

    // ======
    // UPDATE
    // ======

    //    public void updateName(Application application, String name) throws ErrorResponseException {
    //        updateStringField(Application.class, application, "name", name);
    //    }
    //

    // ===
    // GET
    // ===

    public List<User> getAll() throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL)) {
                try (ResultSet result = statement.executeQuery()) {
                    List<User> users = new ArrayList<>();

                    while (result.next()) {
                        User user = new User();
                        user.setId(result.getInt("id"));
                        user.setEmail(result.getString("email"));
                        user.setPassword(result.getString("password"));
                        user.setLastName(result.getString("last_name"));
                        user.setFirstName(result.getString("first_name"));
                        user.setType(UserType.forValue(result.getInt("type")));

                        users.add(user);
                    }
                    return users;
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, User.class);
        }
    }

    public User getFromId(int id) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_FROM_ID)) {
                statement.setInt(1, id);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        User user = new User();
                        user.setId(id);
                        user.setEmail(result.getString("email"));
                        user.setPassword(result.getString("password"));
                        user.setFirstName(result.getString("first_name"));
                        user.setLastName(result.getString("last_name"));
                        user.setType(UserType.forValue(result.getInt("type")));
                        return user;
                    }
                    return null; // No row
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, User.class);
        }
    }

    public User getFromEmail(String email) throws DatabaseOperationException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_FROM_EMAIL)) {
                statement.setString(1, email);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        User user = new User();
                        user.setId(result.getInt("id"));
                        user.setEmail(email);
                        user.setPassword(result.getString("password"));
                        user.setFirstName(result.getString("first_name"));
                        user.setLastName(result.getString("last_name"));
                        user.setType(UserType.forValue(result.getInt("type")));
                        return user;
                    }
                    return null; // No row
                }
            }
        } catch (SQLException e) {
            throw processBasicSQLException(e, User.class);
        }
    }

    // ======
    // DELETE
    // ======

    public int delete(User user) throws DatabaseOperationException {
        return delete("final_user", user);
    }
}
