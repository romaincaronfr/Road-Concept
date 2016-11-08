-- ******************************************************************************
-- ** ROADCONCEPTDB.SQL                                                        **
-- ******************************************************************************
-- ** product: Road Concept                                                    **
-- ** 	module: Road Concept API                                               **
-- ** version: 0.1-SNAPSHOT                                                    **
-- ** 	date: 05/10/2016                                                       **
-- ** file: src/main/resources/roadConceptDB_dev.sql                           **
-- ** author: Maëlig NANTEL						                                         **
-- ******************************************************************************

-- ==============================================================================
-- USERS
-- ==============================================================================

CREATE TABLE IF NOT EXISTS "final_user" (
  "id"         SERIAL PRIMARY KEY,
  "email"      VARCHAR(89) NOT NULL UNIQUE CHECK ("email" ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'),
  "password"   VARCHAR     NOT NULL,
  "first_name" VARCHAR(20) NOT NULL,
  "last_name"  VARCHAR(20) NOT NULL,
  "type"       INTEGER CHECK ("type" IN (1, 2))
);

-- ==============================================================================
-- MAPS INFO
-- ==============================================================================

CREATE TABLE IF NOT EXISTS "map_info" (
  "id"          SERIAL PRIMARY KEY,
  "id_user"     INTEGER     NOT NULL REFERENCES "final_user" (id) ON DELETE CASCADE,
  "name"        VARCHAR(31) NOT NULL,
  "from_osm"    BOOLEAN     NOT NULL,
  "image_url"   VARCHAR(100),
  "description" TEXT
);

-- ==============================================================================
-- SIMULATION PARAMETERS
-- ==============================================================================

CREATE TABLE IF NOT EXISTS "simulation" (
  "uuid"        VARCHAR(40) PRIMARY KEY,
  "id_user"     INTEGER     NOT NULL REFERENCES "final_user" (id) ON DELETE CASCADE,
  "name"        VARCHAR(31) NOT NULL
);
