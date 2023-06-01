FROM payara/server-full:6.2023.2-jdk17
ADD target/ssbd02-0.1.2.war /opt/payara/deployments
