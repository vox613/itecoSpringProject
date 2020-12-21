package ru.iteco.project.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.iteco.project.dao.UserRoleDAO;
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
public class UserRoleDAOImpl implements UserRoleDAO {


    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;


    public UserRoleDAOImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                           SimpleJdbcInsert simpleJdbcInsert) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = simpleJdbcInsert;
        simpleJdbcInsert.withTableName("user_roles");
    }


    @Override
    public Optional<UserRole> findUserRoleByValue(String roleText) {
        List<UserRole> userRoleList = jdbcTemplate.query(
                "SELECT * FROM user_roles WHERE user_roles.value=?",
                new Object[]{roleText},
                new UserRoleRowMapper()
        );
        return userRoleList.isEmpty() ? Optional.empty() : Optional.ofNullable(userRoleList.get(0));
    }


    @Override
    public Optional<UserRole> findUserRoleById(UUID uuid) {
        return Optional.ofNullable(getByPK(uuid));
    }


    @Override
    public boolean userRoleExist(String roleText) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM user_roles WHERE user_roles.value = ?",
                new Object[]{roleText},
                Integer.class
        );
        return (count != null) && count > 0;
    }


    @Override
    public boolean userRoleWithIdIsExist(UUID uuid) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM user_roles WHERE user_roles.id = ?",
                new Object[]{uuid},
                Integer.class
        );
        return (count != null) && count > 0;
    }


    @Override
    public UserRole save(UserRole userRole) {
        userRole.setId(UUID.randomUUID());
        addUserRole(userRole);
        return userRole;
    }


    @Override
    public UserRole getByPK(UUID key) {
        List<UserRole> userRoleList = jdbcTemplate.query("SELECT * FROM user_roles WHERE id = ?",
                new Object[]{key},
                new UserRoleRowMapper()
        );
        return userRoleList.isEmpty() ? null : userRoleList.get(0);
    }


    @Override
    public UserRole deleteByPK(UUID id) {
        UserRole userRoleByPk = getByPK(id);
        if (userRoleByPk != null) {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            namedParameterJdbcTemplate.update("DELETE FROM user_roles WHERE id=:id", namedParameters);
        }
        return userRoleByPk;
    }


    @Override
    public UserRole update(UserRole ob) {
        updateUserRole(ob);
        return ob;
    }


    @Override
    public UserRole delete(UserRole ob) {
        return deleteByPK(ob.getId());
    }


    @Override
    public Collection<UserRole> getAll() {
        return jdbcTemplate.query("SELECT * FROM user_roles", new UserRoleRowMapper());
    }


    @Override
    public Collection<UserRole> addAll(Collection<UserRole> obs) {
        batchAddUserRoles(obs);
        return obs;
    }


    /**
     * Метод пакетного создания списка ролей пользователей при помощи namedParameterJdbcTemplate
     *
     * @param userRoles - список сущностей ролей пользователей
     * @return - массив, содержащий количество строк, затронутых каждым обновлением в пакете
     */
    public int[] batchAddUserRoles(final Collection<UserRole> userRoles) {
        final SqlParameterSource[] batch = createBatch(userRoles);
        String sql = "INSERT INTO user_roles VALUES (:id, :first_name)";
        return namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }


    /**
     * Метод добавляет роль пользователя в БД при помощи шаблона simpleJdbcInsert
     *
     * @param userRole - объект роли пользователя
     * @return - количество затронутых строк, возвращаемое драйвером JDBC
     */
    public int addUserRole(final UserRole userRole) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", userRole.getId());
        parameters.put("value", userRole.getValue());
        return simpleJdbcInsert.execute(parameters);
    }


    /**
     * Метод обновляет запись роли пользователя в БД при помощи шаблона namedParameterJdbcTemplate
     *
     * @param userRole - объект роли пользователя
     * @return количество затронутых строк
     */
    public int updateUserRole(final UserRole userRole) {
        String sql = "UPDATE user_roles SET value=:value WHERE id=:id";

        final SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", userRole.getId())
                .addValue("value", userRole.getValue());
        return namedParameterJdbcTemplate.update(sql, namedParameters);
    }


    /**
     * Класс предназначен для маппинга записи/строки из бд на бизнес сущность UserRole
     */
    private class UserRoleRowMapper implements RowMapper<UserRole> {
        @Override
        public UserRole mapRow(ResultSet rs, int rowNum) throws SQLException {
            final UserRole userRole = new UserRole();
            userRole.setId(UUID.fromString(rs.getString("id")));
            userRole.setValue(rs.getString("value"));
            return userRole;
        }
    }
}
