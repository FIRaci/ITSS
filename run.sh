#!/bin/bash
export DB_URL="jdbc:postgresql://localhost:5432/ITSS"
export DB_USER="a123"
export DB_PASS=""
java --enable-native-access=ALL-UNNAMED -cp "target/classes:target/dependency/*" com.itss.App
