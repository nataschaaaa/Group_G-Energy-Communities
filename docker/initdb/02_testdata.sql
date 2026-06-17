INSERT INTO usage_data (hour, community_produced, community_used, grid_used) VALUES
    ('2025-01-09 08:00:00', 12.5,  10.2, 2.3),
    ('2025-01-09 09:00:00', 15.8,  13.1, 0.0),
    ('2025-01-09 10:00:00', 18.2,  14.5, 0.0),
    ('2025-01-09 11:00:00', 20.1,  16.8, 0.0),
    ('2025-01-09 12:00:00', 22.4,  18.0, 0.0),
    ('2025-01-10 08:00:00', 11.7,   9.5, 1.4),
    ('2025-01-10 09:00:00', 14.9,  12.7, 0.8)
    ON CONFLICT (hour) DO NOTHING;

INSERT INTO current_percentage (hour, community_depleted, grid_portion) VALUES
    ('2025-01-10 09:00:00', 80.36, 19.64)
    ON CONFLICT (hour) DO NOTHING;