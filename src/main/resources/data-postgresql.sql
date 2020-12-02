-- Скрипт для заполнения таблиц с возможными статусами и ролями пользователей/заданий/договоров

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO user_roles (id, value)
VALUES (uuid_generate_v1(), 'ADMIN'),
       (uuid_generate_v1(), 'CUSTOMER'),
       (uuid_generate_v1(), 'EXECUTOR');


INSERT INTO user_statuses (id, value, description)
VALUES (uuid_generate_v1(), 'NOT_EXIST', 'Пользователя не существует'),
       (uuid_generate_v1(), 'CREATED', 'Создан'),
       (uuid_generate_v1(), 'BLOCKED', 'Заблокирован'),
       (uuid_generate_v1(), 'ACTIVE', 'Активен');

INSERT INTO task_statuses (id, value, description)
VALUES (uuid_generate_v1(), 'REGISTERED', 'Задание зарегистрировано'),
       (uuid_generate_v1(), 'IN_PROGRESS', 'Задание на выполнении'),
       (uuid_generate_v1(), 'ON_CHECK', 'Задание на проверке'),
       (uuid_generate_v1(), 'ON_FIX', 'Задание на исправлении'),
       (uuid_generate_v1(), 'DONE', 'Задание выполнено'),
       (uuid_generate_v1(), 'CANCELED', 'Задание отменено');

INSERT INTO contract_statuses (id, value, description)
VALUES (uuid_generate_v1(), 'TERMINATED', 'Договор расторгнут'),
       (uuid_generate_v1(), 'PAID', 'Договор оплачен'),
       (uuid_generate_v1(), 'DONE', 'Договор исполнен');


INSERT INTO users(id, first_name, last_name, second_name, login, password, email, phone_number, role_id, status_id,
                  wallet)
SELECT uuid_generate_v1(),
       'admin',
       'admin',
       'admin',
       'admin',
       'admin',
       'admin@mail.ru',
       '81234567890',
       user_roles.id,
       user_statuses.id,
       1000
FROM user_roles, user_statuses
WHERE user_roles.value = 'ADMIN' AND user_statuses.value = 'ACTIVE';