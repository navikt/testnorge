/*
CREATE TABLE jobb_parameter (
                                navn VARCHAR(255) NOT NULL PRIMARY KEY ,
                                tekst VARCHAR(255) NOT NULL,
                                verdi VARCHAR(255)
);

create table verdier (
                         id INT SERIAL PRIMARY KEY ,
                         verdi_navn varchar(255)  ,
                         verdi_verdi varchar(255)  ,
                         foreign key (verdi_navn) references jobb_parameter(navn)
);

 */