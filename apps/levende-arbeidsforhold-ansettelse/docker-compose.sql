SELECT 'CREATE DATABASE "testnav-levende-arbeidsforhold"'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'testnav-levende-arbeidsforhold')\gexec