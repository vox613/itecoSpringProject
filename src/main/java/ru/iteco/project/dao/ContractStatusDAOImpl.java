package ru.iteco.project.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.iteco.project.model.ContractStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils.createBatch;

/**
 * Класс реализующий функционал доступа к данным о статусах контрактов
 */
@Repository
public class ContractStatusDAOImpl implements ContractStatusDAO {


    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;


    public ContractStatusDAOImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                 SimpleJdbcInsert simpleJdbcInsert) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = simpleJdbcInsert;
        simpleJdbcInsert.withTableName("contract_statuses");
    }

    @Override
    public Optional<ContractStatus> findContractStatusByValue(String statusText) {
        List<ContractStatus> contractStatusList = jdbcTemplate.query(
                "SELECT * FROM contract_statuses WHERE contract_statuses.value=?",
                new Object[]{statusText},
                new ContractStatusRowMapper()
        );
        return contractStatusList.isEmpty() ? Optional.empty() : Optional.ofNullable(contractStatusList.get(0));
    }

    @Override
    public Optional<ContractStatus> findContractStatusById(UUID uuid) {
        return Optional.ofNullable(getByPK(uuid));
    }

    @Override
    public boolean contractStatusExist(String statusText) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM contract_statuses WHERE contract_statuses.value = ?",
                new Object[]{statusText},
                Integer.class
        );
        return (count != null) && count > 0;
    }


    @Override
    public boolean contractStatusWithIdIsExist(UUID uuid) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM contract_statuses WHERE contract_statuses.id = ?",
                new Object[]{uuid},
                Integer.class
        );
        return (count != null) && count > 0;
    }

    @Override
    public ContractStatus save(ContractStatus contractStatus) {
        contractStatus.setId(UUID.randomUUID());
        addContractStatus(contractStatus);
        return contractStatus;
    }

    @Override
    public ContractStatus getByPK(UUID key) {
        List<ContractStatus> contractStatusList = jdbcTemplate.query("SELECT * FROM contract_statuses WHERE id = ?",
                new Object[]{key},
                new ContractStatusRowMapper()
        );
        return contractStatusList.isEmpty() ? null : contractStatusList.get(0);
    }

    @Override
    public ContractStatus deleteByPK(UUID id) {
        ContractStatus contractStatusByPK = getByPK(id);
        if (contractStatusByPK != null) {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            namedParameterJdbcTemplate.update("DELETE FROM contract_statuses WHERE id=:id", namedParameters);
        }
        return contractStatusByPK;
    }

    @Override
    public ContractStatus update(ContractStatus ob) {
        updateContractStatus(ob);
        return ob;
    }

    @Override
    public ContractStatus delete(ContractStatus ob) {
        return deleteByPK(ob.getId());
    }

    @Override
    public Collection<ContractStatus> getAll() {
        return jdbcTemplate.query("SELECT * FROM contract_statuses", new ContractStatusRowMapper());
    }

    @Override
    public Collection<ContractStatus> addAll(Collection<ContractStatus> obs) {
        batchAddContractStatuses(obs);
        return obs;
    }


    /**
     * Метод пакетного создания списка статусов контрактов при помощи namedParameterJdbcTemplate
     *
     * @param contractStatuses - список сущностей статусов контрактов
     * @return - массив, содержащий количество строк, затронутых каждым обновлением в пакете
     */
    public int[] batchAddContractStatuses(final Collection<ContractStatus> contractStatuses) {
        final SqlParameterSource[] batch = createBatch(contractStatuses);
        String sql = "INSERT INTO contract_statuses VALUES (:id, :first_name, :description)";
        return namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }


    /**
     * Метод добавляет статус контракта в БД при помощи шаблона simpleJdbcInsert
     *
     * @param contractStatus - объект статуса контракта
     * @return - количество затронутых строк, возвращаемое драйвером JDBC
     */
    public int addContractStatus(final ContractStatus contractStatus) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", contractStatus.getId());
        parameters.put("value", contractStatus.getValue());
        parameters.put("description", contractStatus.getDescription());
        return simpleJdbcInsert.execute(parameters);
    }


    /**
     * Метод обновляет запись статуса контракта в БД при помощи шаблона namedParameterJdbcTemplate
     *
     * @param contractStatus - объект статуса контракта
     * @return количество затронутых строк
     */
    public int updateContractStatus(final ContractStatus contractStatus) {
        String sql = "UPDATE contract_statuses SET value=:value, description=:description WHERE id=:id";

        final SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", contractStatus.getId())
                .addValue("value", contractStatus.getValue())
                .addValue("description", contractStatus.getDescription());
        return namedParameterJdbcTemplate.update(sql, namedParameters);
    }


    /**
     * Класс предназначен для маппинга записи/строки из бд на бизнес сущность ContractStatus
     */
    private class ContractStatusRowMapper implements RowMapper<ContractStatus> {
        @Override
        public ContractStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
            final ContractStatus contractStatus = new ContractStatus();
            contractStatus.setId(UUID.fromString(rs.getString("id")));
            contractStatus.setValue(rs.getString("value"));
            contractStatus.setDescription(rs.getString("description"));
            return contractStatus;
        }
    }
}
