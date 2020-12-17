package ru.iteco.project.controller.searching;

import ru.iteco.project.controller.dto.DtoInterface;

public interface SearchDto<T extends DtoInterface> extends DtoInterface {

    T searchData();
}
