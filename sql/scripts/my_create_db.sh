#!/bin/bash

psql -p 8889 projectdb < ~/CS166/Project/project/sql/src/create_tables.sql
psql -p 8889 projectdb < ~/CS166/Project/project/sql/src/create_indexes.sql
psql -p 8889 projectdb < ~/CS166/Project/project/sql/src/triggers.sql
psql -p 8889 projectdb < ~/CS166/Project/project/sql/src/load_data.sql
