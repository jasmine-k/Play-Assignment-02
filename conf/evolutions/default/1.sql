 # --- !Ups

CREATE TABLE IF NOT EXISTS userdata(

userid serial NOT NULL,
firstname VARCHAR(25) NOT NULL,
middlename VARCHAR(25),
lastname VARCHAR(25) NOT NULL,
mobilenumber BIGINT NOt NULL ,
gender VARCHAR(10) NOT NULL,
age INT NOT NULL,
email VARCHAR(100) NOT NULL,
password VARCHAR(500) NOT NULL,
isadmin BOOLEAN NOT NULL,
isactive BOOLEAN NOT NULL,

PRIMARY KEY(userId)
);

CREATE TABLE IF NOT EXISTS hobby(

id serial NOT NULL,
name VARCHAR(25) NOT NULL,

PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS userhobby(

id serial NOT NULL,
userid INT NOT NULL,
hobbyid INT NOT NULL,

PRIMARY KEY(id)
);

INSERT INTO hobby VALUES
(1, 'Singing'),
(2, 'Dancing'),
(3, 'Travelling'),
(4, 'Swimming'),
(5, 'Sports');

# --- !Downs

DROP TABLE userdata;
DROP TABLE userhobby;
DROP TABLE hobby;