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
import ru.iteco.project.exception.InvalidTaskStatusException;
import ru.iteco.project.exception.InvalidUserRoleException;
import ru.iteco.project.exception.InvalidUserStatusException;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.TaskStatus;
import ru.iteco.project.model.User;
import ru.iteco.project.model.UserStatus;
import ru.iteco.project.service.mappers.DateTimeMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils.createBatch;
import static ru.iteco.project.model.UserRole.UserRoleEnum.*;


/**
 * Класс реализующий функционал доступа к данным о заданиях
 */
@Repository
@PropertySource(value = {"classpath:errors.properties"})
public class TaskDAOImpl implements TaskDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final UserDAO userDAO;
    private final TaskStatusDAOImpl taskStatusDAO;


    @Value("${errors.task.status.invalid}")
    private String taskStatusIsInvalidMessage;



    public TaskDAOImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                       SimpleJdbcInsert simpleJdbcInsert, UserDAO userDAO, TaskStatusDAOImpl taskStatusDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = simpleJdbcInsert;
        this.userDAO = userDAO;
        this.taskStatusDAO = taskStatusDAO;

        simpleJdbcInsert.withTableName("task");
    }


    @Override
    public Optional<Task> findTaskById(UUID taskId) {
        return Optional.ofNullable(getByPK(taskId));
    }


    @Override
    public Collection<Task> findAllTasksByUser(User user) {
        if (isEqualsUserRole(CUSTOMER, user)) {
            return findAllTasksByCustomerId(user.getId());
        } else if (isEqualsUserRole(EXECUTOR, user)) {
            return findAllTasksByExecutorId(user.getId());
        } else {
            return Collections.emptyList();
        }
    }


    @Override
    public Collection<Task> findAllTasksByCustomerId(UUID customerId) {
        return jdbcTemplate.query("SELECT * FROM task WHERE customer_id=?", new Object[]{customerId}, new TaskRowMapper());
    }


    @Override
    public Collection<Task> findAllTasksByExecutorId(UUID executorId) {
        return jdbcTemplate.query("SELECT * FROM task WHERE executor_id=?", new Object[]{executorId}, new TaskRowMapper());
    }


    @Override
    public Collection<Task> findAllTasksWithStatus(TaskStatus taskStatus) {
        return findAllTasksWithStatusId(taskStatus.getId());
    }


    @Override
    public Collection<Task> findAllTasksWithStatusId(UUID taskStatusId) {
        return jdbcTemplate.query("SELECT * FROM task WHERE task_status_id=?", new Object[]{taskStatusId}, new TaskRowMapper());
    }

    @Override
    public boolean taskWithIdIsExist(UUID uuid) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM task WHERE id = ?", new Object[]{uuid}, Integer.class);
        return (count != null) && count > 0;
    }


    @Override
    public Optional<Task> updateTaskStatus(Task task, TaskStatus.TaskStatusEnum taskStatusEnum) {
        Optional<TaskStatus> taskStatusByValue = taskStatusDAO.findTaskStatusByValue(taskStatusEnum.name());
        TaskStatus taskStatus = taskStatusByValue
                .orElseThrow(() -> new InvalidTaskStatusException(taskStatusIsInvalidMessage));
        task.setTaskStatus(taskStatus);
        return Optional.ofNullable(update(task));
    }

    @Override
    public Task save(Task task) {
        task.setId(UUID.randomUUID());
        addTask(task);
        return task;
    }

    /**
     * Метод добавляет задание в БД при помощи шаблона simpleJdbcInsert
     *
     * @param task - объект задания
     * @return - количество затронутых строк, возвращаемое драйвером JDBC
     */
    public int addTask(final Task task) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", task.getId());
        parameters.put("customer_id", task.getCustomer().getId());
        parameters.put("executor_id", (task.getExecutor() != null) ? task.getExecutor().getId() : null);
        parameters.put("title", task.getTitle());
        parameters.put("description", task.getDescription());
        parameters.put("task_creation_date", Timestamp.valueOf(task.getTaskCreationDate()));
        parameters.put("task_completion_date", Timestamp.valueOf(task.getTaskCompletionDate()));
        parameters.put("last_task_update_date", Timestamp.valueOf(task.getLastTaskUpdateDate()));
        parameters.put("task_status_id", task.getTaskStatus().getId());
        parameters.put("price", task.getPrice());
        parameters.put("task_decision", task.getTaskDecision());
        return simpleJdbcInsert.execute(parameters);
    }


    @Override
    public Task getByPK(UUID key) {
        List<Task> taskList = jdbcTemplate.query("SELECT * FROM task WHERE id = ?",
                new Object[]{key},
                new TaskRowMapper()
        );
        return taskList.isEmpty() ? null : taskList.get(0);
    }


    @Override
    public Task deleteByPK(UUID id) {
        Task taskByPK = getByPK(id);
        if (taskByPK != null) {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            namedParameterJdbcTemplate.update("DELETE FROM task WHERE id=:id", namedParameters);
        }
        return taskByPK;
    }


    @Override
    public Task update(Task ob) {
        updateUser(ob);
        return ob;
    }


    @Override
    public Task delete(Task ob) {
        return deleteByPK(ob.getId());
    }


    @Override
    public Collection<Task> getAll() {
        return jdbcTemplate.query("SELECT * FROM task", new TaskRowMapper());
    }


    @Override
    public Collection<Task> addAll(Collection<Task> obs) {
        batchUpdate(obs);
        return obs;
    }


    /**
     * Метод пакетного создапния списка заданий при помощи namedParameterJdbcTemplate
     *
     * @param tasks - список сущностей заданий
     * @return - массив, содержащий количество строк, затронутых каждым обновлением в пакете
     */
    public int[] batchUpdate(final Collection<Task> tasks) {
        final SqlParameterSource[] batch = createBatch(tasks);
        String sql = "INSERT INTO task VALUES (:id, " +
                ":customer_id, " +
                ":executor_id, " +
                ":title, " +
                ":description, " +
                ":task_creation_date, " +
                ":task_completion_date, " +
                ":last_task_update_date, " +
                ":task_status_id, " +
                ":price, " +
                ":task_decision)";
        return namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }


    /**
     * Метод обновляет запись задания в БД при помощи шаблона namedParameterJdbcTemplate
     *
     * @param task - объект задания
     * @return количество затронутых строк
     */
    public int updateUser(final Task task) {
        String sql = "UPDATE task SET customer_id=:customer_id," +
                " executor_id=:executor_id," +
                " title=:title," +
                " description=:description," +
                " task_creation_date=:task_creation_date," +
                " task_completion_date=:task_completion_date," +
                " last_task_update_date=:last_task_update_date," +
                " task_status_id=:task_status_id," +
                " price=:price," +
                " task_decision=:task_decision " +
                "WHERE id=:id";

        final SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", task.getId())
                .addValue("customer_id", task.getCustomer().getId())
                .addValue("executor_id", (task.getExecutor() != null) ? task.getExecutor().getId() : null)
                .addValue("title", task.getTitle())
                .addValue("description", task.getDescription())
                .addValue("task_creation_date", Timestamp.valueOf(task.getTaskCreationDate()))
                .addValue("task_completion_date", Timestamp.valueOf(task.getTaskCompletionDate()))
                .addValue("last_task_update_date", Timestamp.valueOf(task.getLastTaskUpdateDate()))
                .addValue("task_status_id", task.getTaskStatus().getId())
                .addValue("price", task.getPrice())
                .addValue("task_decision", task.getTaskDecision());
        return namedParameterJdbcTemplate.update(sql, namedParameters);
    }

    /**
     * Класс предназначен для маппинга записи/строки из бд на бизнес сущность Task
     */
    private class TaskRowMapper implements RowMapper<Task> {
        @Override
        public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Task task = new Task();
            task.setId(UUID.fromString(rs.getString("id")));
            UUID customer_id = UUID.fromString(rs.getString("customer_id"));
            task.setCustomer(userDAO.getByPK(customer_id));
            String executor_id = rs.getString("executor_id");
            task.setExecutor((executor_id != null) ? userDAO.getByPK(UUID.fromString(executor_id)) : null);
            task.setTitle(rs.getString("title"));
            task.setDescription(rs.getString("description"));

            Timestamp taskCreationDate = rs.getTimestamp("task_creation_date", DateTimeMapper.tzUTC);
            Timestamp taskCompletionDate = rs.getTimestamp("task_completion_date", DateTimeMapper.tzUTC);
            Timestamp lastTaskUpdateDate = rs.getTimestamp("last_task_update_date", DateTimeMapper.tzUTC);
            task.setTaskCreationDate(taskCreationDate.toLocalDateTime());
            task.setTaskCompletionDate(taskCompletionDate.toLocalDateTime());
            task.setLastTaskUpdateDate(lastTaskUpdateDate.toLocalDateTime());

            UUID task_status_id = UUID.fromString(rs.getString("task_status_id"));
            task.setTaskStatus(taskStatusDAO.findTaskStatusById(task_status_id)
                    .orElseThrow(() -> new InvalidTaskStatusException(taskStatusIsInvalidMessage)));

            task.setPrice(new BigDecimal(rs.getString("price")));
            task.setTaskDecision(rs.getString("task_decision"));
            return task;
        }
    }
}
