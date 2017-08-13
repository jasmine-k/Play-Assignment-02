# --- !Ups

CREATE TABLE IF NOT EXISTS userdata(

userId serial NOT NULL,
firstName VARCHAR(25) NOT NULL,
lastName VARCHAR(25) NOT NULL,
gender VARCHAR(10) NOT NULL,
dateOfBirth VARCHAR(20) NOT NULL,
email VARCHAR(40) NOT NULL,
password VARCHAR(20) NOT NULL,

PRIMARY KEY(userId)

);

# --- !Downs

DROP TABLE userdata