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
import ru.iteco.project.exception.InvalidContractStatusException;
import ru.iteco.project.model.Contract;
import ru.iteco.project.model.ContractStatus;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.User;
import ru.iteco.project.service.mappers.DateTimeMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils.createBatch;

/**
 * Класс реализующий функционал доступа к данным о договорах
 */
@Repository
@PropertySource(value = {"classpath:errors.properties"})
public class ContractDAOImpl implements ContractDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final ContractStatusDAO contractStatusDAO;
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;

    @Value("${errors.contract.status.invalid}")
    private String contractStatusIsInvalidMessage;

    public ContractDAOImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                           SimpleJdbcInsert simpleJdbcInsert, ContractStatusDAO contractStatusDAO,
                           UserDAO userDAO, TaskDAO taskDAO) {

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = simpleJdbcInsert;
        this.contractStatusDAO = contractStatusDAO;
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
        simpleJdbcInsert.withTableName("contract");
    }

    @Override
    public boolean contractIsExist(Contract enterContract) {
        return contractWithIdIsExist(enterContract.getId());
    }


    @Override
    public Optional<Contract> findContractById(UUID contractId) {
        return Optional.ofNullable(getByPK(contractId));
    }


    @Override
    public Optional<Contract> findContractByTask(Task task) {
        List<Contract> contractList = jdbcTemplate.query("SELECT * FROM contract WHERE task_id=?",
                new Object[]{task.getId()},
                new ContractRowMapper());
        return contractList.isEmpty() ? Optional.empty() : Optional.ofNullable(contractList.get(0));
    }


    @Override
    public Collection<Contract> findAllContractsByExecutor(User executor) {
        return jdbcTemplate.query("SELECT * FROM contract WHERE executor_id=?", new Object[]{executor.getId()}, new ContractRowMapper());
    }


    @Override
    public Collection<Contract> findAllContractsByStatus(ContractStatus contractStatus) {
        return findAllContractsByStatus(contractStatus.getId());
    }


    @Override
    public Collection<Contract> findAllContractsByStatus(UUID contractStatusId) {
        return jdbcTemplate.query("SELECT * FROM contract WHERE contract_status_id=?",
                new Object[]{contractStatusId},
                new ContractRowMapper()
        );
    }

    @Override
    public boolean contractWithIdIsExist(UUID uuid) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM contract WHERE id = ?", new Object[]{uuid}, Integer.class);
        return (count != null) && count > 0;
    }


    @Override
    public Contract save(Contract contract) {
        contract.setId(UUID.randomUUID());
        addContract(contract);
        return contract;
    }


    /**
     * Метод добавляет контракт в БД при помощи шаблона simpleJdbcInsert
     *
     * @param contract - объект задания
     * @return - количество затронутых строк, возвращаемое драйвером JDBC
     */
    public int addContract(final Contract contract) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", contract.getId());
        parameters.put("customer_id", contract.getCustomer().getId());
        parameters.put("executor_id", contract.getExecutor().getId());
        parameters.put("time_contract_conclusion", Timestamp.valueOf(contract.getTimeOfContractConclusion()));
        parameters.put("task_id", contract.getTask().getId());
        parameters.put("contract_status_id", contract.getContractStatus().getId());
        return simpleJdbcInsert.execute(parameters);
    }


    @Override
    public Contract getByPK(UUID key) {
        List<Contract> contractList = jdbcTemplate.query("SELECT * FROM contract WHERE id = ?",
                new Object[]{key},
                new ContractRowMapper()
        );
        return contractList.isEmpty() ? null : contractList.get(0);
    }


    @Override
    public Contract deleteByPK(UUID id) {
        Contract contractByPK = getByPK(id);
        if (contractByPK != null) {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            namedParameterJdbcTemplate.update("DELETE FROM contract WHERE id=:id", namedParameters);
        }
        return contractByPK;
    }


    @Override
    public Contract update(Contract ob) {
        updateContract(ob);
        return ob;
    }


    @Override
    public Contract delete(Contract ob) {
        return deleteByPK(ob.getId());
    }


    @Override
    public Collection<Contract> getAll() {
        return jdbcTemplate.query("SELECT * FROM contract", new ContractRowMapper());
    }


    @Override
    public Collection<Contract> addAll(Collection<Contract> obs) {
        batchUpdate(obs);
        return obs;
    }


    /**
     * Метод пакетного создапния списка контрактов при помощи namedParameterJdbcTemplate
     *
     * @param contracts - список сущностей контрактов
     * @return - массив, содержащий количество строк, затронутых каждым обновлением в пакете
     */
    public int[] batchUpdate(final Collection<Contract> contracts) {
        final SqlParameterSource[] batch = createBatch(contracts);
        String sql = "INSERT INTO contract VALUES (:id, " +
                ":customer_id, " +
                ":executor_id, " +
                ":time_contract_conclusion, " +
                ":task_id, " +
                ":contract_status_id)";
        return namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }


    /**
     * Метод обновляет запись контракта в БД при помощи шаблона namedParameterJdbcTemplate
     *
     * @param contract - объект контракта
     * @return количество затронутых строк
     */
    public int updateContract(final Contract contract) {
        String sql = "UPDATE contract SET customer_id=:customer_id," +
                " executor_id=:executor_id," +
                " time_contract_conclusion=:time_contract_conclusion," +
                " task_id=:task_id," +
                " contract_status_id=:contract_status_id " +
                "WHERE id=:id";

        final SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", contract.getId())
                .addValue("customer_id", contract.getCustomer().getId())
                .addValue("executor_id", contract.getExecutor().getId())
                .addValue("time_contract_conclusion", Timestamp.valueOf(contract.getTimeOfContractConclusion()))
                .addValue("task_id", contract.getTask().getId())
                .addValue("contract_status_id", contract.getContractStatus().getId());
        return namedParameterJdbcTemplate.update(sql, namedParameters);
    }

    /**
     * Класс предназначен для маппинга записи/строки из бд на бизнес сущность Contract
     */
    private class ContractRowMapper implements RowMapper<Contract> {
        @Override
        public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Contract contract = new Contract();
            contract.setId(UUID.fromString(rs.getString("id")));

            UUID customer_id = UUID.fromString(rs.getString("customer_id"));
            contract.setCustomer(userDAO.getByPK(customer_id));

            String executor_id = rs.getString("executor_id");
            contract.setExecutor(userDAO.getByPK(UUID.fromString(executor_id)));

            Timestamp taskCreationDate = rs.getTimestamp("time_contract_conclusion", DateTimeMapper.tzUTC);
            contract.setTimeOfContractConclusion(taskCreationDate.toLocalDateTime());

            UUID task_id = UUID.fromString(rs.getString("task_id"));
            contract.setTask(taskDAO.getByPK(task_id));

            UUID contract_status_id = UUID.fromString(rs.getString("contract_status_id"));
            contract.setContractStatus(contractStatusDAO.findContractStatusById(contract_status_id)
                    .orElseThrow(() -> new InvalidContractStatusException(contractStatusIsInvalidMessage)));

            return contract;
        }
    }
}
