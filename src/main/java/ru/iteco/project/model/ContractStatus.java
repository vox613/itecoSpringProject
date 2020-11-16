package ru.iteco.project.model;

/**
 * Перечисление возможных статусов договора
 */
public enum ContractStatus {

    REGISTERED("Договор зарегистрирован"),
    TERMINATED("Договор расторгнут"),
    PAID("Договор оплачен"),
    DONE("Договор исполнен");

    private final String description;

    ContractStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
     *
     * @param inputContractStatus - текстовое представление роли
     * @return true - в перечислении присутствует аргумент с данным именем,
     * false - в перечислении отсутствует аргумент с данным именем
     */
    public static boolean isCorrectValue(String inputContractStatus) {
        for (ContractStatus status : values()) {
            if (status.name().equals(inputContractStatus)) {
                return true;
            }
        }
        return false;
    }
}
