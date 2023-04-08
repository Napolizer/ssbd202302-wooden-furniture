FROM payara/server-full:6.2023.2-jdk17
ADD target/ssbd02-0.0.4.war /opt/payara/deployments
