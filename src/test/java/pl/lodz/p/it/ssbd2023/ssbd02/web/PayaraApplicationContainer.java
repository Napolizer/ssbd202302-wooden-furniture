package pl.lodz.p.it.ssbd2023.ssbd02.web;

import org.microshed.testing.testcontainers.ApplicationContainer;

public class PayaraApplicationContainer extends ApplicationContainer {
  @Override
  public void stop() {
    this.dockerClient
        .stopContainerCmd(this.getContainerId())
        .withTimeout(1000)
        .exec();
  }
}
