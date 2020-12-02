package ru.iteco.project.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.iteco.project.exception.InvalidUserRoleException;
import ru.iteco.project.exception.InvalidUserStatusException;
import ru.iteco.project.model.User;
import ru.iteco.project.model.UserRole;
import ru.iteco.project.model.UserStatus;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Класс реализующий функционал доступа к данным о пользователях
 */
@Repository
@PropertySource(value = {"classpath:errors.properties"})
public class UserDAOImpl implements UserDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final UserRoleDAOImpl userRoleDAO;
    private final UserStatusDAOImpl userStatusDAO;


    @Value("${errors.user.status.invalid}")
    private String userStatusIsInvalidMessage;

    @Value("${errors.user.role.invalid}")
    private String userRoleIsInvalidMessage;


    public UserDAOImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                       SimpleJdbcInsert simpleJdbcInsert, UserRoleDAOImpl userRoleDAO, UserStatusDAOImpl userStatusDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = simpleJdbcInsert;
        this.userRoleDAO = userRoleDAO;
        this.userStatusDAO = userStatusDAO;
        simpleJdbcInsert.withTableName("users");
    }


    @Override
    public User save(User user) {
        user.setId(UUID.randomUUID());
        addUser(user);
        return user;
    }


    @Override
    public User deleteByPK(UUID id) {
        User userByPK = getByPK(id);
        if (userByPK != null) {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            namedParameterJdbcTemplate.update("DELETE FROM users WHERE id=:id", namedParameters);
        }
        return userByPK;
    }


    @Override
    public User update(User ob) {
        updateUser(ob);
        return ob;
    }


    @Override
    public User delete(User ob) {
        return deleteByPK(ob.getId());
    }


    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users", new UserRowMapper());
    }


    @Override
    public Collection<User> addAll(Collection<User> obs) {
        batchAddUsers(obs);
        return obs;
    }


    @Override
    public User getByPK(UUID key) {
        List<User> userList = jdbcTemplate.query("SELECT * FROM users WHERE id = ?",
                new Object[]{key},
                new UserRowMapper()
        );
        return userList.isEmpty() ? null : userList.get(0);
    }


    @Override
    public Optional<User> findUserById(UUID uuid) {
        return Optional.ofNullable(getByPK(uuid));
    }


    @Override
    public Optional<User> findUserByLogin(String login) {
        List<User> userList = jdbcTemplate.query(
                "SELECT * FROM users WHERE users.login=?",
                new Object[]{login},
                new UserRowMapper()
        );
        return userList.isEmpty() ? Optional.empty() : Optional.ofNullable(userList.get(0));
    }


    @Override
    public List<User> getAllUsersByRole(UserRole role) {
        return jdbcTemplate.query(
                "SELECT * FROM users,user_roles WHERE users.role_id=user_roles.id and user_roles.value=?",
                new Object[]{role.getValue()},
                new UserRowMapper()
        );
    }


    @Override
    public List<User> getAllUsersByStatus(UserStatus userStatus) {
        return jdbcTemplate.query(
                "SELECT * FROM users,user_statuses WHERE users.status_id=user_statuses.id and user_statuses.value=?",
                new Object[]{userStatus.getValue()},
                new UserRowMapper()
        );
    }

    @Override
    public boolean userWithIdIsExist(UUID id) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM users WHERE id = ?", new Object[]{id}, Integer.class);
        return (count != null) && count > 0;
    }


    @Override
    public Optional<User> updateUserStatus(User user, UserStatus.UserStatusEnum userStatusEnum) {
        Optional<UserStatus> userStatusByValue = userStatusDAO.findUserStatusByValue(userStatusEnum.name());
        UserStatus userStatus = userStatusByValue
                .orElseThrow(() -> new InvalidUserStatusException(userStatusIsInvalidMessage));
        user.setUserStatus(userStatus);
        return Optional.ofNullable(update(user));
    }


    @Override
    public boolean loginExist(String login) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM users WHERE login = ?", new Object[]{login}, Integer.class);
        return (count != null) && count > 0;
    }


    @Override
    public boolean emailExist(String email) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM users WHERE email = ?", new Object[]{email}, Integer.class);
        return (count != null) && count > 0;
    }


    /**
     * Метод добавляет пользователя в БД при помощи шаблона simpleJdbcInsert
     *
     * @param user - объект пользователя
     * @return - количество затронутых строк, возвращаемое драйвером JDBC
     */
    public int addUser(final User user) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", user.getId());
        parameters.put("first_name", user.getFirstName());
        parameters.put("last_name", user.getLastName());
        parameters.put("second_name", user.getSecondName());
        parameters.put("login", user.getLogin());
        parameters.put("password", user.getPassword());
        parameters.put("email", user.getEmail());
        parameters.put("phone_number", user.getPhoneNumber());
        parameters.put("role_id", user.getRole().getId());
        parameters.put("status_id", user.getUserStatus().getId());
        parameters.put("wallet", user.getWallet());
        return simpleJdbcInsert.execute(parameters);
    }


    /**
     * Метод обновляет запись пользователя в БД при помощи шаблона namedParameterJdbcTemplate
     *
     * @param user - объект пользователя
     * @return количество затронутых строк
     */
    public int updateUser(final User user) {
        String sql = "UPDATE users SET first_name=:first_name," +
                " last_name=:last_name," +
                " second_name=:second_name," +
                " login=:login," +
                " password=:password," +
                " email=:email," +
                " phone_number=:phone_number," +
                " role_id=:role_id," +
                " status_id=:status_id," +
                " wallet=:wallet " +
                "WHERE id=:id";

        return namedParameterJdbcTemplate.update(sql, prepareSqlParameterSource(user));
    }


    /**
     * Метод пакетного создания списка пользователей при помощи namedParameterJdbcTemplate
     *
     * @param users - список сущностей пользователей
     * @return - массив, содержащий количество строк, затронутых каждым обновлением в пакете
     */
    public int[] batchAddUsers(final Collection<User> users) {
        String sql = "INSERT INTO users VALUES (:id, " +
                ":first_name, " +
                ":last_name, " +
                ":second_name, " +
                ":login, " +
                ":password, " +
                ":email, " +
                ":phone_number, " +
                ":role_id, " +
                ":status_id, " +
                ":wallet)";

        return namedParameterJdbcTemplate.batchUpdate(sql, prepareSqlParameterSourceMass(users));
    }


    private SqlParameterSource[] prepareSqlParameterSourceMass(final Collection<User> users){
        return users.stream().map(this::prepareSqlParameterSource).toArray(SqlParameterSource[]::new);
    }


    private SqlParameterSource prepareSqlParameterSource(final User user) {
        return new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("first_name", user.getFirstName())
                .addValue("last_name", user.getLastName())
                .addValue("second_name", user.getSecondName())
                .addValue("login", user.getLogin())
                .addValue("password", user.getPassword())
                .addValue("email", user.getEmail())
                .addValue("phone_number", user.getPhoneNumber())
                .addValue("role_id", user.getRole().getId())
                .addValue("status_id", user.getUserStatus().getId())
                .addValue("wallet", user.getWallet());
    }

    /**
     * Класс предназначен для маппинга записи/строки из бд на бизнес сущность User
     */
    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            final User user = new User();
            user.setId(UUID.fromString(rs.getString("id")));
            user.setFirstName(rs.getString("first_name"));
            user.setSecondName(rs.getString("second_name"));
            user.setLastName(rs.getString("last_name"));
            user.setLogin(rs.getString("login"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setPhoneNumber(rs.getString("phone_number"));
            user.setWallet(new BigDecimal(rs.getString("wallet")));

            UUID role_id = UUID.fromString(rs.getString("role_id"));
            user.setRole(userRoleDAO.findUserRoleById(role_id)
                    .orElseThrow(() -> new InvalidUserRoleException(userRoleIsInvalidMessage)));

            UUID status_id = UUID.fromString(rs.getString("status_id"));
            user.setUserStatus(userStatusDAO.findUserStatusById(status_id)
                    .orElseThrow(() -> new InvalidUserStatusException(userStatusIsInvalidMessage)));
            return user;
        }
    }
}
