-- ******************************************************************************
-- ** ROADCONCEPTDB.SQL                                                        **
-- ******************************************************************************
-- ** product: Road Concept                                                    **
-- ** 	module: Road Concept API                                               **
-- ** version: 0.1_SNAPSHOT                                                    **
-- ** 	date: 03/10/2016                                                       **
-- ** file: src/main/resources/roadConceptDB_SQLite.sql                        **
-- ** author: Maëlig NANTEL						                                         **
-- ******************************************************************************

--CREATE SCHEMA IF NOT EXISTS "road_concept";
--SET SCHEMA 'road_concept';

-- ==============================================================================
-- USERS
-- ==============================================================================

CREATE TABLE IF NOT EXISTS "user" (
  "id_user" SERIAL PRIMARY KEY,
  "email" TEXT NOT NULL,
  "first_name" TEXT NOT NULL,
  "last_name" TEXT NOT NULL
);

-- ==============================================================================
-- MAPS INFO
-- ==============================================================================

CREATE TABLE IF NOT EXISTS "map_info" (
  "id_map" SERIAL PRIMARY KEY,
  "id_user" INTEGER NOT NULL REFERENCES user(id_user) ON DELETE CASCADE,
  "name" TEXT NOT NULL,
  "image_url" TEXT
);