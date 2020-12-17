package ru.iteco.project.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.iteco.project.model.UserRole;
import ru.iteco.project.model.UserStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils.createBatch;

/**
 * Класс реализующий функционал доступа к данным о ролях пользователей
 */
@Repository
public class UserStatusDAOImpl implements UserStatusDAO {


    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;


    public UserStatusDAOImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                             SimpleJdbcInsert simpleJdbcInsert) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = simpleJdbcInsert;
        simpleJdbcInsert.withTableName("user_statuses");
    }


    @Override
    public Optional<UserStatus> findUserStatusByValue(String statusText) {
        List<UserStatus> userStatusList = jdbcTemplate.query(
                "SELECT * FROM user_statuses WHERE user_statuses.value=?",
                new Object[]{statusText},
                new UserStatusRowMapper()
        );
        return userStatusList.isEmpty() ? Optional.empty() : Optional.ofNullable(userStatusList.get(0));
    }


    @Override
    public Optional<UserStatus> findUserStatusById(UUID uuid) {
        return Optional.ofNullable(getByPK(uuid));
    }


    @Override
    public boolean userStatusExist(String statusText) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM user_statuses WHERE user_statuses.value = ?",
                new Object[]{statusText},
                Integer.class
        );
        return (count != null) && count > 0;
    }


    @Override
    public boolean userStatusWithIdIsExist(UUID uuid) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM user_statuses WHERE user_statuses.id = ?",
                new Object[]{uuid},
                Integer.class
        );
        return (count != null) && count > 0;
    }


    @Override
    public UserStatus save(UserStatus userStatus) {
        userStatus.setId(UUID.randomUUID());
        addUserStatus(userStatus);
        return userStatus;
    }


    @Override
    public UserStatus getByPK(UUID key) {
        List<UserStatus> userStatusList = jdbcTemplate.query("SELECT * FROM user_statuses WHERE id = ?",
                new Object[]{key},
                new UserStatusRowMapper()
        );
        return userStatusList.isEmpty() ? null : userStatusList.get(0);
    }


    @Override
    public UserStatus deleteByPK(UUID id) {
        UserStatus userStatusByPK = getByPK(id);
        if (userStatusByPK != null) {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            namedParameterJdbcTemplate.update("DELETE FROM user_statuses WHERE id=:id", namedParameters);
        }
        return userStatusByPK;
    }


    @Override
    public UserStatus update(UserStatus ob) {
        updateUserStatus(ob);
        return ob;
    }


    @Override
    public UserStatus delete(UserStatus ob) {
        return deleteByPK(ob.getId());
    }


    @Override
    public Collection<UserStatus> getAll() {
        return jdbcTemplate.query("SELECT * FROM user_statuses", new UserStatusRowMapper());
    }


    @Override
    public Collection<UserStatus> addAll(Collection<UserStatus> obs) {
        batchAddUserStatuses(obs);
        return obs;
    }


    /**
     * Метод пакетного создания списка статусов пользователей при помощи namedParameterJdbcTemplate
     *
     * @param userStatuses - список сущностей статусов пользователей
     * @return - массив, содержащий количество строк, затронутых каждым обновлением в пакете
     */
    public int[] batchAddUserStatuses(final Collection<UserStatus> userStatuses) {
        final SqlParameterSource[] batch = createBatch(userStatuses);
        String sql = "INSERT INTO user_statuses VALUES (:id, :first_name, :description)";
        return namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }


    /**
     * Метод обновляет запись статуса пользователя в БД при помощи шаблона namedParameterJdbcTemplate
     *
     * @param userStatus - объект статуса пользователя
     * @return количество затронутых строк
     */
    public int updateUserStatus(final UserStatus userStatus) {
        String sql = "UPDATE user_statuses SET value=:value, description=:description WHERE id=:id";

        final SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", userStatus.getId())
                .addValue("value", userStatus.getValue())
                .addValue("description", userStatus.getDescription());
        return namedParameterJdbcTemplate.update(sql, namedParameters);
    }


    /**
     * Метод добавляет статус пользователя в БД при помощи шаблона simpleJdbcInsert
     *
     * @param userStatus - объект статуса пользователя
     * @return - количество затронутых строк, возвращаемое драйвером JDBC
     */
    public int addUserStatus(final UserStatus userStatus) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", userStatus.getId());
        parameters.put("value", userStatus.getValue());
        parameters.put("description", userStatus.getDescription());
        return simpleJdbcInsert.execute(parameters);
    }


    /**
     * Класс предназначен для маппинга записи/строки из бд на бизнес сущность UserStatus
     */
    private class UserStatusRowMapper implements RowMapper<UserStatus> {
        @Override
        public UserStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
            final UserStatus userStatus = new UserStatus();
            userStatus.setId(UUID.fromString(rs.getString("id")));
            userStatus.setValue(rs.getString("value"));
            userStatus.setDescription(rs.getString("description"));
            return userStatus;
        }
    }
}
