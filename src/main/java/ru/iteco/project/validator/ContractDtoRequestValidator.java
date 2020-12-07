package ru.iteco.project.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.iteco.project.controller.dto.ContractDtoRequest;
import ru.iteco.project.exception.ContractConclusionException;
import ru.iteco.project.model.ContractStatus;

/**
 * Класс содержит валидаторы для полей объекта запроса ContractDtoRequest
 */
@Component
public class ContractDtoRequestValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(ContractDtoRequestValidator.class.getName());


    public ContractDtoRequestValidator(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ContractDtoRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ContractDtoRequest contractForm = (ContractDtoRequest) target;


        if (StringUtils.isEmpty(contractForm.getExecutorId())) {
            logger.error("contract executorId is empty");
            prepareErrorMessage(errors, "contract.executor.id.empty", "executorId");
        }


        if (StringUtils.isEmpty(contractForm.getTaskId())) {
            logger.error("contract taskId is empty");
            prepareErrorMessage(errors, "contract.task.id.empty", "taskId");
        }


        String confirmationCode = contractForm.getConfirmationCode();
        String repeatConfirmationCode = contractForm.getRepeatConfirmationCode();
        if (StringUtils.isEmpty(confirmationCode) || StringUtils.isEmpty(repeatConfirmationCode)) {
            logger.error("contract confirmation code is empty");
            prepareErrorMessage(errors, "contract.confirmation.code.empty", "repeatConfirmationCode");
        } else if (!confirmationCode.equals(repeatConfirmationCode)) {
            logger.error("confirmation codes mismatch");
            prepareErrorMessage(errors, "contract.confirmation.code.mismatch", "repeatConfirmationCode");
        }


        String contractStatus = contractForm.getContractStatus();
        if ((contractStatus != null) && !ContractStatus.isCorrectValue(contractStatus)) {
            logger.error("contract status is invalid");
            prepareErrorMessage(errors, "contract.status.invalid", "contractStatus");
        }

        if (errors.hasErrors()) {
            throw new ContractConclusionException("Impossible to prepare a contract, insufficient data!", errors.getAllErrors());
        }

    }

}
