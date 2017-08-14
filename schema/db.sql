CREATE TABLE IF NOT EXISTS userdata(

userId serial NOT NULL,
firstName VARCHAR(25) NOT NULL,
middleName VARCHAR(25),
lastName VARCHAR(25) NOT NULL,
mobileNumber BIGINT NOt NULL ,
gender VARCHAR(10) NOT NULL,
age INT NOT NULL,
email VARCHAR(100) NOT NULL,
password VARCHAR(500) NOT NULL,
isAdmin BOOLEAN NOT NULL,
isActive BOOLEAN NOT NULL,

PRIMARY KEY(userId)

);

CREATE TABLE IF NOT EXISTS hobby(

id serial NOT NULL,
name VARCHAR(25) NOT NULL,

PRIMARY KEY(id)
);


CREATE TABLE IF NOT EXISTS userhobby(

id serial NOT NULL,
userId INT NOT NULL,
hobbyId INT NOT NULL,

PRIMARY KEY(id)
);

INSERT INTO hobby VALUES
(1, 'Singing'),
(2, 'Dancing'),
(3, 'Travelling'),
(4, 'Swimming'),
(5, 'Sports');