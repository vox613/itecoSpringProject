package ru.iteco.project.controller.searching;

/**
 * Класс - dto для поиска статусов контрактов с переданными ограничениями по полям
 */
public class ContractStatusSearchDto implements SearchDto<ContractStatusSearchDto> {

    /*** Наименование статуса задания */
    private String value;

    /*** Описание статуса задания */
    private String description;

    /*** Дата и время создания статуса задания */
    private String createdAt;

    /*** Дата и время последнего обновления статуса задания */
    private String updatedAt;


    public ContractStatusSearchDto() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public ContractStatusSearchDto searchData() {
        return this;
    }
}
