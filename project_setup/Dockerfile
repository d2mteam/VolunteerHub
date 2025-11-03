# Base image: PostgreSQL 15
FROM postgres:15

# Install build dependencies
RUN apt-get update && \
    apt-get install -y git build-essential postgresql-server-dev-15 && \
    rm -rf /var/lib/apt/lists/*

# Clone and build pg_ivm
RUN git clone https://github.com/sraoss/pg_ivm.git /tmp/pg_ivm && \
    cd /tmp/pg_ivm && \
    make && make install && \
    rm -rf /tmp/pg_ivm

# Optional: preload pg_ivm so it's always available
RUN echo "shared_preload_libraries = 'pg_ivm'" >> /usr/share/postgresql/postgresql.conf.sample

# Default environment
ENV POSTGRES_USER=admin
ENV POSTGRES_PASSWORD=admin123
ENV POSTGRES_DB=volunteerhub

# Expose Postgres port
EXPOSE 5432


#services:
#  db:
#    build:
#      context: .
#      dockerfile: Dockerfile
#    container_name: volunteerhub-postgresql
#    ports:
#      - "5431:5432"
#    volumes:
#      - ./data:/var/lib/postgresql/data
#    environment:
#      POSTGRES_USER: admin
#      POSTGRES_PASSWORD: admin123
#      POSTGRES_DB: volunteerhub