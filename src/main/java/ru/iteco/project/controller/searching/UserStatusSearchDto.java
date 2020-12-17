package ru.iteco.project.controller.searching;

/**
 * Класс - dto для поиска статусов пользователей с переданными ограничениями по полям
 */
public class UserStatusSearchDto implements SearchDto<UserStatusSearchDto> {

    /*** Наименование статуса пользователя */
    private String value;

    /*** Описание статуса пользователя */
    private String description;

    /*** Дата и время создания роли пользователя */
    private String createdAt;

    /*** Дата и время последнего обновления роли пользователя */
    private String updatedAt;


    public UserStatusSearchDto() {
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
    public UserStatusSearchDto searchData() {
        return this;
    }
}
