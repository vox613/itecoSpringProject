package ru.iteco.project.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.iteco.project.model.TaskStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils.createBatch;

/**
 * Класс реализующий функционал доступа к данным о ролях пользователей
 */
@Repository
public class TaskStatusDAOImpl implements TaskStatusDAO {


    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;


    public TaskStatusDAOImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                             SimpleJdbcInsert simpleJdbcInsert) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = simpleJdbcInsert;
        simpleJdbcInsert.withTableName("task_statuses");
    }


    @Override
    public Optional<TaskStatus> findTaskStatusByValue(String statusText) {
        List<TaskStatus> taskStatusList = jdbcTemplate.query(
                "SELECT * FROM task_statuses WHERE task_statuses.value=?",
                new Object[]{statusText},
                new TaskStatusRowMapper()
        );
        return taskStatusList.isEmpty() ? Optional.empty() : Optional.ofNullable(taskStatusList.get(0));
    }

    @Override
    public Optional<TaskStatus> findTaskStatusById(UUID uuid) {
        return Optional.ofNullable(getByPK(uuid));
    }

    @Override
    public boolean taskStatusExist(String statusText) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM task_statuses WHERE task_statuses.value = ?",
                new Object[]{statusText},
                Integer.class
        );
        return (count != null) && count > 0;
    }

    @Override
    public boolean taskStatusWithIdIsExist(UUID uuid) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM task_statuses WHERE task_statuses.id = ?",
                new Object[]{uuid},
                Integer.class
        );
        return (count != null) && count > 0;
    }


    @Override
    public TaskStatus save(TaskStatus taskStatus) {
        taskStatus.setId(UUID.randomUUID());
        addTaskStatus(taskStatus);
        return taskStatus;
    }

    @Override
    public TaskStatus getByPK(UUID key) {
        List<TaskStatus> taskStatusList = jdbcTemplate.query("SELECT * FROM task_statuses WHERE id = ?",
                new Object[]{key},
                new TaskStatusRowMapper()
        );
        return taskStatusList.isEmpty() ? null : taskStatusList.get(0);
    }


    @Override
    public TaskStatus deleteByPK(UUID id) {
        TaskStatus taskStatusByPK = getByPK(id);
        if (taskStatusByPK != null) {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            namedParameterJdbcTemplate.update("DELETE FROM task_statuses WHERE id=:id", namedParameters);
        }
        return taskStatusByPK;
    }


    @Override
    public TaskStatus update(TaskStatus ob) {
        updateTaskStatus(ob);
        return ob;
    }

    @Override
    public TaskStatus delete(TaskStatus ob) {
        return deleteByPK(ob.getId());
    }

    @Override
    public Collection<TaskStatus> getAll() {
        return jdbcTemplate.query("SELECT * FROM task_statuses", new TaskStatusRowMapper());
    }

    @Override
    public Collection<TaskStatus> addAll(Collection<TaskStatus> obs) {
        batchAddTaskStatuses(obs);
        return obs;
    }


    /**
     * Метод пакетного создания списка статусов задания при помощи namedParameterJdbcTemplate
     *
     * @param taskStatuses - список сущностей статусов задания
     * @return - массив, содержащий количество строк, затронутых каждым обновлением в пакете
     */
    public int[] batchAddTaskStatuses(final Collection<TaskStatus> taskStatuses) {
        final SqlParameterSource[] batch = createBatch(taskStatuses);
        String sql = "INSERT INTO task_statuses VALUES (:id, :first_name, :description)";
        return namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }


    /**
     * Метод добавляет статус задания в БД при помощи шаблона simpleJdbcInsert
     *
     * @param taskStatus - объект статуса задания
     * @return - количество затронутых строк, возвращаемое драйвером JDBC
     */
    public int addTaskStatus(final TaskStatus taskStatus) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", taskStatus.getId());
        parameters.put("value", taskStatus.getValue());
        parameters.put("description", taskStatus.getDescription());
        return simpleJdbcInsert.execute(parameters);
    }


    /**
     * Метод обновляет запись статуса задания в БД при помощи шаблона namedParameterJdbcTemplate
     *
     * @param taskStatus - объект статуса задания
     * @return количество затронутых строк
     */
    public int updateTaskStatus(final TaskStatus taskStatus) {
        String sql = "UPDATE task_statuses SET value=:value, description=:description WHERE id=:id";

        final SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", taskStatus.getId())
                .addValue("value", taskStatus.getValue())
                .addValue("description", taskStatus.getDescription());
        return namedParameterJdbcTemplate.update(sql, namedParameters);
    }


    /**
     * Класс предназначен для маппинга записи/строки из бд на бизнес сущность TaskStatus
     */
    private class TaskStatusRowMapper implements RowMapper<TaskStatus> {
        @Override
        public TaskStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
            final TaskStatus taskStatus = new TaskStatus();
            taskStatus.setId(UUID.fromString(rs.getString("id")));
            taskStatus.setValue(rs.getString("value"));
            taskStatus.setDescription(rs.getString("description"));
            return taskStatus;
        }
    }
}
