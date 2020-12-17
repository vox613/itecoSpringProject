package ru.iteco.project.controller.searching;

/**
 * Класс - dto для поиска контрактов с переданными ограничениями по полям
 */
public class ContractSearchDto implements SearchDto<ContractSearchDto> {

    /*** Статус договора */
    private String contractStatus;

    /*** Дата и время создания пользователя */
    private String createdAt;

    /*** Дата и время последнего обновления пользователя */
    private String updatedAt;


    public ContractSearchDto() {
    }


    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public ContractSearchDto searchData() {
        return this;
    }
}
