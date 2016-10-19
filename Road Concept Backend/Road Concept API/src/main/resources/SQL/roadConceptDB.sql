-- ******************************************************************************
-- ** ROADCONCEPTDB.SQL                                                        **
-- ******************************************************************************
-- ** product: Road Concept                                                    **
-- ** 	module: Road Concept API                                               **
-- ** version: 0.1-SNAPSHOT                                                    **
-- ** 	date: 05/10/2016                                                       **
-- ** file: src/main/resources/roadConceptDB_dev.sql                       **
-- ** author: Maëlig NANTEL						                                         **
-- ******************************************************************************

-- ==============================================================================
-- PROCEDURES
-- ==============================================================================

CREATE OR REPLACE FUNCTION check_id_change() RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
	new.id = old.id;
	RETURN new;
END $$;

-- ==============================================================================
-- USERS
-- ==============================================================================

CREATE TABLE IF NOT EXISTS "final_user" (
	"id" SERIAL PRIMARY KEY,
	"email" varchar(89) NOT NULL UNIQUE CHECK ("email" ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'),
	"password" varchar NOT NULL,
	"first_name" varchar(20) NOT NULL,
	"last_name" varchar(20) NOT NULL,
	"type" INTEGER CHECK ("type" IN (1, 2))
);

CREATE TRIGGER "final_user_update_trigger"
BEFORE UPDATE ON "final_user"
FOR EACH ROW EXECUTE PROCEDURE check_id_change();

-- ==============================================================================
-- MAPS INFO
-- ==============================================================================

CREATE TABLE IF NOT EXISTS "map_info" (
	"id" SERIAL PRIMARY KEY,
  "id_user" integer NOT NULL REFERENCES "final_user" (id) ON DELETE CASCADE,
	"name" varchar(31) NOT NULL,
	"from_osm" BOOLEAN NOT NULL,
	"image_url" varchar(100),
	"description" text
);

CREATE TRIGGER "map_info_update_trigger"
BEFORE UPDATE ON "map_info"
FOR EACH ROW EXECUTE PROCEDURE check_id_change();

