/*
CREATE TABLE jobb_parameter (
                                NAVN VARCHAR(255) NOT NULL PRIMARY KEY ,
                                TEKST VARCHAR(255) NOT NULL,
                                VERDI VARCHAR(255)
);
create table verdier (
                         ID INT AUTO_INCREMENT PRIMARY KEY ,
                         verdi_navn varchar(255)  ,
                         verdi_verdi varchar(255)  ,
                         foreign key (verdi_navn) references jobb_parameter(NAVN)
)

 */