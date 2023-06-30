DROP TABLE IF EXISTS TOOLS;

CREATE TABLE TOOLS (
    tool_code VARCHAR(20) PRIMARY KEY,
    tool_type VARCHAR(50) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    daily_charge DECIMAL (6,2),
    weekday_charge BOOLEAN,
    weekend_charge BOOLEAN,
    holiday_charge BOOLEAN
);