FROM open-liberty:full
COPY --chown=1001:0 src/main/liberty/config/server.xml /config/
COPY --chown=1001:0 target/ssbd02-Kv1.0.war /config/apps/ssbd02-Kv1.0.war
