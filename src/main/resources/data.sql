-- =========================
-- PLAYERS
-- =========================
INSERT INTO cricketers (name, image_link, type, is_foreign)
VALUES
('Virat Kohli', 'virat.jpg', 'BATSMAN', false),
('Rohit Sharma', 'rohit.jpg', 'BATSMAN', false),
('Jasprit Bumrah', 'bumrah.jpg', 'BOWLER', false),
('Ben Stokes', 'stokes.jpg', 'ALL_ROUNDER', true),
('AB de Villiers', 'abd.jpg', 'BATSMAN', true),
('Pat Cummins', 'cummins.jpg', 'BOWLER', true);

-- =========================
-- BATSMEN STATS
-- (IDs: 1, 2, 5)
-- =========================
INSERT INTO batsmen_stats (player_id, runs, matches, batting_avg, strike_rate)
VALUES
(1, 12000, 250, 57.30, 92.50),
(2, 11000, 240, 49.10, 88.40),
(5, 9500, 184, 53.50, 101.20);

-- =========================
-- BOWLER STATS
-- (IDs: 3, 6)
-- =========================
INSERT INTO bowler_stats (player_id, matches, wickets, economy, best_figure)
VALUES
(3, 180, 350, 4.30, '6/19'),
(6, 160, 280, 5.10, '5/27');

-- =========================
-- ALL-ROUNDER STATS
-- (ID: 4)
-- =========================
INSERT INTO allrounder_stats (player_id, runs, matches, economy, strike_rate)
VALUES
(4, 6500, 220, 5.20, 135.00);