--;;
CREATE TABLE user_info (
  user_id      bigint UNIQUE NOT NULL,
  height       double precision,
  weight       double precision
);