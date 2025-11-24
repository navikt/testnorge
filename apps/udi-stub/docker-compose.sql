SELECT 'CREATE DATABASE "testnav-udistub"'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'testnav-udistub')\gexec