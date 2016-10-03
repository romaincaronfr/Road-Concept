-- ******************************************************************************
-- ** ROADCONCEPTDB.SQL                                                        **
-- ******************************************************************************
-- ** product: Road Concept                                                    **
-- ** 	module: Road Concept API                                               **
-- ** version: 0.1_SNAPSHOT                                                    **
-- ** 	date: 03/10/2016                                                       **
-- ** file: src/main/resources/roadConceptDB_Postgre.sql                       **
-- ** author: Maëlig NANTEL						                               **
-- ******************************************************************************

--CREATE SCHEMA IF NOT EXISTS "road_concept";
--SET SCHEMA 'road_concept';

-- ==============================================================================
-- USERS
-- ==============================================================================

CREATE TABLE IF NOT EXISTS "user" (
	"id_user" SERIAL PRIMARY KEY,
	"email" varchar(89) NOT NULL UNIQUE CHECK ("email" ~ '[A-Z0-9._%-]+@[A-Z0-9._%-]+\.[A-Z]{2,4}'),
	"first_name" varchar(20) NOT NULL,
	"last_name" varchar(20) NOT NULL
);

-- ==============================================================================
-- MAPS INFO
-- ==============================================================================

CREATE TABLE IF NOT EXISTS "map_info" (
	"id_map" SERIAL PRIMARY KEY,
  "id_user" integer NOT NULL REFERENCES "user" (id_user) ON DELETE CASCADE,
	"name" varchar(31) NOT NULL,
	"image_url" varchar(100)
);
