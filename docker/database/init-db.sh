#!/usr/bin/env bash

set -e

if [ -z "$BAR_DB_USERNAME" ] || [ -z "$BAR_DB_PASSWORD" ]; then
  echo "ERROR: Missing environment variable. Set value for both 'BAR_DB_USERNAME' and 'BAR_DB_PASSWORD'."
  exit 1
fi

# Create role and database
psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=$BAR_DB_USERNAME --set PASSWORD=$BAR_DB_PASSWORD <<-EOSQL
  CREATE USER :USERNAME WITH PASSWORD ':PASSWORD';

      CREATE DATABASE bar
    WITH OWNER = :USERNAME
    ENCODING = 'UTF-8'
    CONNECTION LIMIT = -1;
EOSQL
