package ru.iteco.project.controller.searching;

/**
 * Класс - dto для поиска ролей пользователей с переданными ограничениями по полям
 */
public class UserRoleSearchDto implements SearchDto<UserRoleSearchDto> {

    /*** Наименование роли пользователя */
    private String value;

    /*** Дата и время создания роли пользователя */
    private String createdAt;

    /*** Дата и время последнего обновления роли пользователя */
    private String updatedAt;


    public UserRoleSearchDto() {
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


    @Override
    public UserRoleSearchDto searchData() {
        return this;
    }
}
