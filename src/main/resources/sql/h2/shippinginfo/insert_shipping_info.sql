-- SHIPPING_INFOS-data.sql
INSERT INTO SHIPPING_INFOS (ID, MEMBER_NO, RECEIVER, MAIN_ADDRESS, SUB_ADDRESS, PHONE_NUMBER,
                            CREATED_DATE,
                            UPDATED_DATE)
VALUES (1, 1, 'John Doe', '123 Main St', 'Apt 4B', 1234567890, NOW(),
        NOW()),
       (2, 2, 'Jane Doe', '456 Elm St', 'Apt 2A', 9876543210, NOW(),
        NOW());