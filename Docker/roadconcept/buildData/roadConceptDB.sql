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
  "image_url"   TEXT,
  "description" TEXT
);

-- ==============================================================================
-- SIMULATION PARAMETERS
-- ==============================================================================

CREATE TABLE IF NOT EXISTS "simulation" (
  "uuid"                   VARCHAR(40) PRIMARY KEY,
  "id_user"                INTEGER             NOT NULL REFERENCES "final_user" (id) ON DELETE CASCADE,
  "id_map"                 INTEGER             NOT NULL REFERENCES "map_info" (id) ON DELETE CASCADE,
  "name"                   VARCHAR(100)        NOT NULL,
  "sampling"               INTEGER             NOT NULL,
  "finish"                 BOOLEAN             NOT NULL,
  "creation_date"          VARCHAR(10)         NOT NULL,
  "min_departure_living_s" INTEGER             NOT NULL,
  "random_traffic"         BOOLEAN             NOT NULL
);

CREATE TABLE IF NOT EXISTS "simulation_zone" (
  "id"                  SERIAL PRIMARY KEY,
  "simulation_uuid"     VARCHAR(40) NOT NULL REFERENCES "simulation" (uuid) ON DELETE CASCADE,
  "living_feature"      VARCHAR(40) NOT NULL,
  "working_feature"     VARCHAR(40) NOT NULL,
  "departure_living_s"  INTEGER     NOT NULL,
  "departure_working_s" INTEGER     NOT NULL,
  "car_percentage"      INTEGER     NOT NULL,
  "vehicle_count"       INTEGER     NOT NULL
);
CREATE INDEX IF NOT EXISTS index_simulation_zone_simulation_uuid
  ON simulation_zone (simulation_uuid);

-- ==============================================================================
-- SIMULATION RESULTS
-- ==============================================================================

CREATE UNLOGGED TABLE IF NOT EXISTS "simulation_vehicle" (
  "simulation_uuid" VARCHAR(40)      NOT NULL REFERENCES "simulation" (uuid) ON DELETE CASCADE,
  "vehicle_id"      INTEGER          NOT NULL,
  "timestamp_s"     INTEGER          NOT NULL,
  "longitude"       DOUBLE PRECISION NOT NULL,
  "latitude"        DOUBLE PRECISION NOT NULL,
  "angle"           DOUBLE PRECISION NOT NULL,
  "type"            INTEGER CHECK ("type" IN (6, 7)),
  PRIMARY KEY (simulation_uuid, vehicle_id, timestamp_s)
);
CREATE INDEX IF NOT EXISTS index_simulation_vehicle
  ON simulation_vehicle (simulation_uuid, timestamp_s);


CREATE UNLOGGED TABLE IF NOT EXISTS "simulation_congestion" (
  "simulation_uuid"       VARCHAR(40) NOT NULL REFERENCES "simulation" (uuid) ON DELETE CASCADE,
  "feature_uuid"          VARCHAR(40) NOT NULL,
  "congestion_percentage" INTEGER     NOT NULL,
  "timestamp_s"           INTEGER     NOT NULL,
  PRIMARY KEY (feature_uuid, simulation_uuid, timestamp_s)
);
CREATE INDEX IF NOT EXISTS simulation_congestion
  ON simulation_vehicle (simulation_uuid, timestamp_s);

CREATE UNLOGGED TABLE IF NOT EXISTS "simulation_vehicle_statistics" (
  "simulation_uuid" VARCHAR(40) NOT NULL REFERENCES "simulation" (uuid) ON DELETE CASCADE,
  "vehicle_id"      INTEGER     NOT NULL,
  "time"            INTEGER     NOT NULL,
  "distance"        INTEGER     NOT NULL,
  "average_speed"   INTEGER     NOT NULL,
  PRIMARY KEY (simulation_uuid, vehicle_id)
);