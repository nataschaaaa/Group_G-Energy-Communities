-- Tabelle für stündliche Verbrauchs- und Produktionsdaten
CREATE TABLE IF NOT EXISTS usage_data (
                                          id                BIGSERIAL PRIMARY KEY,
                                          hour              TIMESTAMP NOT NULL UNIQUE,
                                          community_produced DOUBLE PRECISION NOT NULL DEFAULT 0,
                                          community_used    DOUBLE PRECISION NOT NULL DEFAULT 0,
                                          grid_used         DOUBLE PRECISION NOT NULL DEFAULT 0
);

-- Tabelle für den aktuellen Prozentwert der laufenden Stunde
CREATE TABLE IF NOT EXISTS current_percentage (
                                                  id                  BIGSERIAL PRIMARY KEY,
                                                  hour                TIMESTAMP NOT NULL UNIQUE,
                                                  community_depleted  DOUBLE PRECISION NOT NULL DEFAULT 0,
                                                  grid_portion        DOUBLE PRECISION NOT NULL DEFAULT 0
);