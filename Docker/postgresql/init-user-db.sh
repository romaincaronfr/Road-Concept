#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER roadconcept PASSWORD 'roadconcept';
    CREATE DATABASE roadconcept;
    GRANT ALL PRIVILEGES ON DATABASE roadconcept TO roadconcept;
EOSQL