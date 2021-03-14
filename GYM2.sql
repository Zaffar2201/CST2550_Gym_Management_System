DROP DATABASE GYM2;
CREATE DATABASE GYM2;
USE GYM2;


CREATE TABLE Trainer(
	TrainerId VARCHAR(4) NOT NULL,
	TrainerName VARCHAR(25) NOT NULL,
	TrainerAge INT NOT NULL,
	TrainerGender ENUM('M','F'),
	PRIMARY KEY(TrainerId)

);

CREATE TABLE Client(
	ClientId VARCHAR(4) NOT NULL,
	ClientName VARCHAR(25) NOT NULL,
	CLientAge INT NOT NULL,
	ClientGender ENUM('M','F'),
	PRIMARY KEY(ClientId)

);

CREATE TABLE Specialism(
	SpecId VARCHAR(4) NOT NULL,
	TrainerId VARCHAR(4) NOT NULL,
	Focus VARCHAR(15) NOT NULL,
	PRIMARY KEY(SpecId),
	FOREIGN KEY(TrainerId) REFERENCES Trainer(TrainerId)
);


CREATE TABLE Booking(
	BookingId VARCHAR(4),
	TrainerId VARCHAR(4) NOT NULL,
	ClientId VARCHAR(4) NOT NULL,
	SpecId VARCHAR(4) NOT NULL,
	BookingDate DATE NOT NULL,
	BookingTime TIME NOT NULL,
	BookingDuration TIME NOT NULL,
	PRIMARY KEY(BookingId),
	FOREIGN KEY(TrainerId) REFERENCES Trainer(TrainerId),
	FOREIGN KEY(ClientId) REFERENCES Client(ClientId),
	FOREIGN KEY(SpecId) REFERENCES Specialism(SpecId)
	
);
	
INSERT INTO Trainer
VALUES ('PT01','David',35,'M'),
('PT02','Arnod',37,'M'),
('PT03','Alexa',30,'F'),
('PT04','Sam',40,'M'),
('PT05','Robet',38,'M');

INSERT INTO Client
VALUES ('C001','Anil',25,'M'),
('C002','Karim',34,'M'),
('C003','Mary',22,'F'),
('C004','Anita',30,'F'),
('C005','Jooe',38,'M');


INSERT INTO Specialism
VALUES('S001','PT01','Weight Loss'),
('S002','PT01','Muscle Gain'),
('S003','PT02','Flexibility'),
('S004','PT02','Squat'),
('S005','PT03','Box Jump'),
('S006','PT03','Plank'),
('S007','PT04','Bench Press'),
('S008','PT04','Basu Squats'),
('S009','PT05','Foam Roller'),
('S010','PT05','Zumba Dance');

	
INSERT INTO Booking
VALUES ('B001', 'PT01', 'C001','S001','2020-10-01','09:00','01:00'),
 ('B002', 'PT02','C002','S003','2020-10-02','10:00','02:00'),
 ('B003', 'PT03', 'C003','S005','2020-10-03','11:00','03:00'),
 ('B004', 'PT04','C004','S007','2020-10-04','09:00','04:00'),
('B005', 'PT04','C004','S008','2020-10-05','10:00','01:00'),
('B006', 'PT05','C005','S009','2020-10-06','09:00','02:00'),
 ('B007', 'PT05','C005','S010','2020-10-07','10:00','03:00');






