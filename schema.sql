-- Optional: Hibernate creates this automatically (ddl-auto=update). Use for manual setup / viva demo.
CREATE DATABASE IF NOT EXISTS dsa_tracker;
USE dsa_tracker;
CREATE TABLE IF NOT EXISTS problems (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  topic VARCHAR(60) NOT NULL,
  difficulty ENUM('EASY','MEDIUM','HARD') NOT NULL,
  status ENUM('TODO','IN_PROGRESS','SOLVED','REVISIT') NOT NULL DEFAULT 'TODO',
  platform VARCHAR(40),
  link VARCHAR(500),
  notes TEXT,
  solved_date DATE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_topic (topic), INDEX idx_status (status), INDEX idx_solved (solved_date)
);
INSERT INTO problems (title,topic,difficulty,status,platform,link,solved_date) VALUES
('Two Sum','Arrays','EASY','SOLVED','LeetCode','https://leetcode.com/problems/two-sum/',CURDATE()),
('Merge Intervals','Arrays','MEDIUM','REVISIT','LeetCode','https://leetcode.com/problems/merge-intervals/',CURDATE()-INTERVAL 1 DAY),
('LRU Cache','Design','MEDIUM','TODO','LeetCode','https://leetcode.com/problems/lru-cache/',NULL);
-- Useful analytics query: solved per topic
-- SELECT topic, COUNT(*) FROM problems WHERE status='SOLVED' GROUP BY topic ORDER BY 2 DESC;
