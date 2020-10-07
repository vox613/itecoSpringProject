package model;

public enum ContractStatus {

    CONTRACT_REGISTERED("Договор зарегистрирован"),
    CONTRACT_TERMINATED("Договор расторгнут"),
    CONTRACT_PAID("Договор оплачен"),
    CONTRACT_DONE("Договор исполнен");

    private String description;

    ContractStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
