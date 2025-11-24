SELECT 'CREATE DATABASE "testnav-brregstub"'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'testnav-brregstub')\gexec