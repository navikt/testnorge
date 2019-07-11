alter table ereg_adresse
    drop constraint UK_mwy9fnkebxg8f85ak183y14gu;

alter table ereg_adresse
    drop constraint FKsc5tbhryssbqy7xeuik532naj;

alter table ereg_adresse
    drop constraint FKfm1k7o3ad0mk31vl67786ta2o;

drop table ereg_adresse;
drop table adresse;

alter table ereg
    add column forretnings_adresse varchar(255),
    add column forretnings_kommunenr varchar(255),
    add column forretnings_landkode varchar(255),
    add column forretnings_postnr varchar(255),
    add column forretnings_poststed varchar(255),
    add column adresse varchar(255),
    add column kommunenr varchar(255),
    add column landkode varchar(255),
    add column postnr varchar(255),
    add column poststed varchar(255);