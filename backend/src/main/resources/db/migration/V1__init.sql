-- Create lists table (owner of items)
CREATE TABLE IF NOT EXISTS lists (
  id          INT PRIMARY KEY,
  name        TEXT        NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Create items table
CREATE TABLE IF NOT EXISTS items (
  id          INT PRIMARY KEY,         -- maps to @Id + IDENTITY/SEQUENCE
  list_id     INT      NOT NULL,          -- maps to Item.listId
  name        TEXT        NOT NULL,          -- maps to Item.name
  done        BOOLEAN     NOT NULL DEFAULT FALSE, -- maps to Item.done
  category    TEXT,                           -- maps to Item.category
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_items_list
    FOREIGN KEY (list_id) REFERENCES lists(id) ON DELETE CASCADE
);

-- Helpful indexes for queries (by list and by done-flag)
CREATE INDEX IF NOT EXISTS idx_items_list_id ON items(list_id);
CREATE INDEX IF NOT EXISTS idx_items_done    ON items(done);
