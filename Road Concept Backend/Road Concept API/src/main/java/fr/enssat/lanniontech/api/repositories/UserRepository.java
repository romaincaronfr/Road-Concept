package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.exceptions.database.DatabaseOperationException;
import fr.enssat.lanniontech.api.exceptions.database.SQLUnexpectedException;
import fr.enssat.lanniontech.api.repositories.connectors.SQLDatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository extends AbstractRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);

    private static final String INSERT = "INSERT INTO final_user(email, password, first_name, last_name, type) VALUES (?, ?, ?, ?, ?) RETURNING id";
    private static final String SELECT_FROM_ID = "SELECT email, password, first_name, last_name, type FROM final_user WHERE id = ?";
    private static final String SELECT_FROM_EMAIL = "SELECT id, password, first_name, last_name, type FROM final_user WHERE email = ?";
    private static final String SELECT_FROM_EMAIL_AND_PASSWORD = "SELECT id, first_name, last_name, type FROM final_user WHERE email = ? AND password = ?";

    //  private static final String SELECT_ALL = "SELECT id_application, uuid_application, name, visible_deployment, visible_dashboard, visible_quick_editor, id_description FROM application WHERE id_tenant = ? ORDER BY name";
    //  private static final String SELECT_FROM_UUID = "SELECT id_application, id_tenant, name, visible_deployment, visible_dashboard, visible_quick_editor, id_description FROM application WHERE uuid_application = ?::uuid";
    //  private static final String SELECT_FROM_NAME = "SELECT id_application, uuid_application, visible_deployment, visible_dashboard, visible_quick_editor, id_description FROM application WHERE id_tenant = ? AND name = ?";
    //  private static final String SELECT_DEPLOYED_CONFIGURATIONS_COUNT = "SELECT COUNT(*) FROM application NATURAL INNER JOIN service NATURAL INNER JOIN configuration WHERE id_application = ? AND reset_date IS NOT NULL";

    // ===========
    // CONSTRUCTOR
    // ===========

    // ======
    // CREATE
    // ======

    public User create(String email, String lastName, String firstName, String password, UserType type) throws DatabaseOperationException {
        try (PreparedStatement statement = SQLDatabaseConnector.getConnection().prepareStatement(INSERT)) {
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
//    public void updateDescription(Application application, @Nullable DescriptionData descriptionData) throws ErrorResponseException {
//        DescriptionRepository.createOrUpdate(getContext(), application, descriptionData);
//    }
//
//    // ===
//    // GET
//    // ===
//
//    public EntityCollection<Application> getAll(Tenant tenant) throws ErrorResponseException {
//        try (PreparedStatement statement = getConnection().prepareStatement(SELECT_ALL)) {
//            statement.setId(tenant);
//
//            try (ResultSet result = statement.executeQuery(new NoCheckAlgorithm())) {
//                EntityCollection<Application> collection = new EntityCollectionBase<>(Application.class);
//
//                while (result.next()) {
//                    Application application = new Application(result.getInt());
//                    application.setUUID(result.getUUID());
//                    application.setTenantId(tenant.getId());
//                    application.setName(result.getString());
//                    application.setVisibleDeployment(result.getBoolean());
//                    application.setVisibleDashboard(result.getBoolean());
//                    application.setVisibleQuickEditor(result.getBoolean());
//                    queryDescription(application, result.getInt());
//                    result.checkColumnCount();
//
//                    if (canAccessApplication(tenant.getName(), application.getName(), false)) {
//                        collection.add(application);
//                    }
//                }
//
//                return collection;
//            }
//
//        } catch (SQLException e) {
//            throw new UnexpectedSQLErrorException(e);
//        }
//    }

    public User getFromId(int id) throws SQLUnexpectedException {
        try (PreparedStatement statement = SQLDatabaseConnector.getConnection().prepareStatement(SELECT_FROM_ID)) {
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
        } catch (SQLException e) {
            throw processBasicSQLException(e, User.class);
        }
    }

    public User getFromEmail(String email) throws SQLUnexpectedException {
        try (PreparedStatement statement = SQLDatabaseConnector.getConnection().prepareStatement(SELECT_FROM_EMAIL)) {
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
        } catch (SQLException e) {
            throw processBasicSQLException(e, User.class);
        }
    }

//    /**
//     * This method does not check whether the operator can access the returned
//     * application. To do that, we would need to have the tenant, but the latter
//     * is precisely computed using the returned application.
//     */
//    public Application getFromUUID(UUID uuid) throws ErrorResponseException {
//        try (PreparedStatement statement = getConnection().prepareStatement(SELECT_FROM_UUID)) {
//            statement.setUUID(uuid);
//
//            try (ResultSet result = statement.executeQuery(new CheckExistingAlgorithm(Application.class))) {
//                Application application = new Application(result.getInt());
//                application.setUUID(uuid);
//                application.setTenantId(result.getInt());
//                application.setName(result.getString());
//                application.setVisibleDeployment(result.getBoolean());
//                application.setVisibleDashboard(result.getBoolean());
//                application.setVisibleQuickEditor(result.getBoolean());
//                queryDescription(application, result.getInt());
//                result.checkColumnCount();
//
//                return application;
//            }
//
//        } catch (SQLException e) {
//            throw new UnexpectedSQLErrorException(e);
//        }
//    }
//
//    public Application getFromName(Tenant tenant, String name) throws ErrorResponseException {
//        try (PreparedStatement statement = getConnection().prepareStatement(SELECT_FROM_NAME)) {
//            statement.setId(tenant);
//            statement.setString(name);
//
//            try (ResultSet result = statement.executeQuery(new CheckExistingAlgorithm(Application.class))) {
//                Application application = new Application(result.getInt());
//                application.setUUID(result.getUUID());
//                application.setTenantId(tenant.getId());
//                application.setName(name);
//                application.setVisibleDeployment(result.getBoolean());
//                application.setVisibleDashboard(result.getBoolean());
//                application.setVisibleQuickEditor(result.getBoolean());
//                queryDescription(application, result.getInt());
//                result.checkColumnCount();
//
//                canAccessApplication(tenant.getName(), name, true);
//
//                return application;
//            }
//
//        } catch (SQLException e) {
//            throw new UnexpectedSQLErrorException(e);
//        }
//    }
//
//    public void getDeployedConfigurationsCount(Application application) throws ErrorResponseException {
//        try (PreparedStatement statement = getConnection().prepareStatement(SELECT_DEPLOYED_CONFIGURATIONS_COUNT)) {
//            statement.setId(application);
//
//            try (ResultSet result = statement.executeQuery(new CheckSingleAlgorithm())) {
//                application.setDeployedConfigurations(result.getInt());
//            }
//
//        } catch (SQLException e) {
//            throw new UnexpectedSQLErrorException(e);
//        }
//    }
//
//    // =============
//    // GET OR CREATE
//    // =============
//
//    public Application getOrCreate(Tenant tenant, String name, boolean visibleDeployment, boolean visibleDashboard, boolean visibleQuickEditor, DataSource source) throws ErrorResponseException {
//        try {
//            return getFromName(tenant, name);
//        } catch (ResourceNotFoundException e) {
//            CommonsExceptions.unused(e);
//            return create(tenant, name, visibleDeployment, visibleDashboard, visibleQuickEditor, source);
//        }
//    }
//
//    // ==================
//    // SEARCH FILE SYSTEM
//    // ==================
//
//    public EntityCollection<Application> searchFileSystem(Tenant tenant, SearchMode searchMode) throws ErrorResponseException {
//        EntityCollection<Application> collection = new EntityCollectionBase<>(Application.class);
//        File parent = getFile(tenant.getName());
//        File[] content = parent.listFiles(); // @@@ check taht the tenant exists on the file system
//
//        if (content != null) {
//            for (File file : content) {
//                if (file.isDirectory()) {
//                    try {
//                        String fileName = file.getName();
//                        assert fileName != null;
//                        Application application = getOrCreate(tenant, fileName, true, true, false, DataSource.FILE_SYSTEM);
//                        getDeployedConfigurationsCount(application);
//                        collection.add(application);
//                    } catch (ErrorResponseException e) {
//                        // Maybe:
//                        // - The application exists but can not be accessed by the operator.
//                        // - The application does not exist but can not be created, because its name is invalid.
//                        CommonsExceptions.unused(e);
//                        getConnection().rollback();
//                    }
//                    getConnection().commit();
//                }
//            }
//
//            if (searchMode == SearchMode.RECURSIVE) {
//                VersionRepository versionRepository = new VersionRepository(getContext());
//                for (Application application : collection) {
//                    assert application != null;
//                    versionRepository.searchFileSystem(tenant, application, searchMode);
//                }
//            }
//        }
//
//        tenant.setApplications(collection);
//        return collection;
//    }
//
//    // ======
//    // DELETE
//    // ======
//
//    public Count delete(Application application) throws ErrorResponseException {
//        return delete(Application.class, application);
//    }
}
