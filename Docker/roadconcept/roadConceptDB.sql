-- ******************************************************************************
-- ** ROADCONCEPTDB.SQL                                                        **
-- ******************************************************************************
-- ** product: Road Concept                                                    **
-- ** 	module: Road Concept API                                               **
-- ** version: 0.1-SNAPSHOT                                                    **
-- ** 	creationDate: 23/11/2016                                               **
-- ** file: src/main/resources/roadConceptDB.sql                               **
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
  "id_user"     INTEGER      NOT NULL REFERENCES "final_user" (id) ON DELETE CASCADE,
  "name"        VARCHAR(100) NOT NULL,
  "image_url"   VARCHAR(100),
  "description" TEXT
);

-- ==============================================================================
-- SIMULATION PARAMETERS
-- ==============================================================================

CREATE TABLE IF NOT EXISTS "simulation" (
  "uuid"                VARCHAR(40) PRIMARY KEY,
  "id_user"             INTEGER     NOT NULL REFERENCES "final_user" (id) ON DELETE CASCADE,
  "id_map"              INTEGER     NOT NULL REFERENCES "map_info" (id) ON DELETE CASCADE,
  "name"                VARCHAR(100),
  "sampling"            INTEGER     NOT NULL,
  "finish"              BOOLEAN     NOT NULL,
  "creation_date"       VARCHAR(10) NOT NULL,
  "living_feature"      VARCHAR(40) NOT NULL,
  "working_feature"     VARCHAR(40) NOT NULL,
  "departure_living_s"  INTEGER     NOT NULL,
  "departure_working_s" INTEGER     NOT NULL,
  "car_percentage"      INTEGER     NOT NULL,
  "vehicle_count"       INTEGER     NOT NULL
);

-- ==============================================================================
-- SIMULATION RESULTS
-- ==============================================================================

CREATE TABLE IF NOT EXISTS "simulation_vehicle" (
  "simulation_uuid" VARCHAR(40) NOT NULL REFERENCES "simulation" (uuid) ON DELETE CASCADE,
  "vehicle_id"      INTEGER     NOT NULL,
  "id_map"          INTEGER     NOT NULL REFERENCES "map_info" (id) ON DELETE CASCADE,
  "timestamp_s"     INTEGER     NOT NULL,
  "longitude"       INTEGER     NOT NULL,
  "latitude"        INTEGER     NOT NULL,
  "type"            INTEGER CHECK ("type" IN (6, 7)),
  PRIMARY KEY (simulation_uuid, vehicle_id, timestamp_s)
);

CREATE TABLE IF NOT EXISTS "simulation_congestion" (
  "feature_uuid"          VARCHAR(40) NOT NULL,
  "congestion_percentage" INTEGER     NOT NULL,
  "simulation_uuid"       VARCHAR(40) NOT NULL REFERENCES "simulation" (uuid) ON DELETE CASCADE,
  "timestamp_s"           INTEGER     NOT NULL,
  PRIMARY KEY (feature_uuid, simulation_uuid, timestamp_s)
);