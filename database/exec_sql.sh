#!/bin/bash

# This script is used to update the database
# It will execute a SQL script inside a running Docker container
# Usage: ./exec_sql.sh <container_name> <db_name> <db_user> <db_password> <sql_file>

# Check if the correct number of arguments is provided
if [ "$#" -ne 4 ]; then
    echo "Usage: $0 <container_name> <db_name> <db_user> <sql_file>"
    exit 1
fi


# Assign arguments to variables
CONTAINER_NAME=$1
DB_NAME=$2
DB_USER=$3
INIT_SQL_PATH=$4

# Get the container ID of the running database container
CONTAINER_ID=$(docker ps -q --filter "name=$CONTAINER_NAME")


# Check if the container is running
if [ -z "$CONTAINER_ID" ]; then
    echo "Error: No running container found with name '$CONTAINER_NAME'."
    exit 1
fi

# copy the SQL script into the container
docker cp "$INIT_SQL_PATH" "$CONTAINER_NAME":/"$INIT_SQL_PATH"



# Check if the copy was successful
if [ $? -ne 0 ]; then
    echo "Error: Failed to copy SQL script into container."
    exit 1
fi

# Execute the SQL script inside the container
docker exec -it "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" -f /"$INIT_SQL_PATH"
# Check if the execution was successful

if [ $? -ne 0 ]; then
    echo "Error: Failed to execute SQL script inside container."
    exit 1
fi

# Print success message
echo "SQL script executed successfully inside container."