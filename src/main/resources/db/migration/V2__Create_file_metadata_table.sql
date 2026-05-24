CREATE TABLE file_metadata (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL UNIQUE,
    original_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    uploaded_by BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_file_metadata_file_name ON file_metadata(file_name);
CREATE INDEX idx_file_metadata_uploaded_by ON file_metadata(uploaded_by);
