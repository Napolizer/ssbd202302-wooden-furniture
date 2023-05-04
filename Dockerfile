FROM payara/server-full:6.2023.2-jdk17
ADD target/ssbd02-0.0.8.war /opt/payara/deployments
