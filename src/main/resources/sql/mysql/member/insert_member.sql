INSERT INTO member (MEMBER_NO, EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, GENDER, PHONE_NUMBER, BIRTHDAY, ROLE, STATUS,
                    ADDRESS_MAIN,
                    ADDRESS_SUB, ZIP_CODE, CREATED_DATE, UPDATED_DATE, DELETED_DATE, LAST_LOGIN_DATE)
VALUES (1, 'qkrtkdwns3410@naver.com', '{bcrypt}jiohioqh123!@#', 'Park', 'SangJun', 'MEN', '010-2520-4929', NOW(),
        'ROLE_CUSTOMER',
        'ACTIVE', 'ADDRESS_MAIN 1', 'ADDRESS_SUB 1', '01234', NOW(), NOW(), NOW(), NOW()),
       (2, 'qkrtkdwns34102@naver.com', '{bcrypt}jiohioqh123!@#', 'Park', 'SangJun2', 'MEN', '010-2520-4929', NOW(),
        'ROLE_CUSTOMER',
        'ACTIVE', 'ADDRESS_MAIN 1', 'ADDRESS_SUB 1', '01234', NOW(), NOW(), NOW(), NOW()),
       (3, 'akdjlkajsldjkaldj@kkk.com', '{bcrypt}jiohioqh123!@#', 'Park', 'SangJun3', 'MEN', '010-2520-4929', NOW(),
        'ROLE_CUSTOMER',
        'ACTIVE', 'ADDRESS_MAIN 1', 'ADDRESS_SUB 1', '01234', NOW(), NOW(), NOW(), NOW());


INSERT INTO MEMBER_REFRESH_TOKEN (MEMBER_NO, REFRESH_TOKEN, CREATED_DATE)
VALUES ('qkrtkdwns3410@naver.com', 'refresh_token_1', NOW()),
       ('qkrtkdwns34102@naver.com', 'refresh_token_2', NOW()),
       ('akdjlkajsldjkaldj@kkk.com', 'refresh_token_3', NOW());
;