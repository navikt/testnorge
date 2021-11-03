-------------------------------
-- M O D I F Y   T A B L E S --
-------------------------------

alter table bestilling
add column opprett_fra_gruppe integer references gruppe (id);
