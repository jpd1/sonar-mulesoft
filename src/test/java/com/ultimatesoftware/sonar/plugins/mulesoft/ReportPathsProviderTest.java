/*
 * SonarQube JaCoCo Plugin
 * Copyright (C) 2018-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.ultimatesoftware.sonar.plugins.mulesoft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportPathsProviderTest {
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  private SensorContextTester tester;
  private ReportPathsProvider provider;
  private Path mavenPath = Paths.get("munit-coverage.json");
  private Path baseDir;

  @Before
  public void setUp() throws IOException {
    baseDir = temp.newFolder("baseDir").toPath();
    tester = SensorContextTester.create(baseDir);
    Path reportPath = baseDir.resolve(mavenPath);
    Files.createDirectories(reportPath.getParent());
    Files.createFile(reportPath);
    provider = new ReportPathsProvider(tester);
  }

  @Test
  public void should_use_provided_paths() throws IOException {

    MapSettings settings = new MapSettings();
    settings.setProperty(ReportPathsProvider.REPORT_PATHS_PROPERTY_KEY, "mypath1,mypath2");
    tester.setSettings(settings);

    assertThat(provider.getPaths()).containsOnly(baseDir.resolve("mypath1"), baseDir.resolve("mypath2"));
  }

  @Test
  public void should_use_provided_absolute_path() {
    MapSettings settings = new MapSettings();
    settings.setProperty(ReportPathsProvider.REPORT_PATHS_PROPERTY_KEY, "/my/path");
    tester.setSettings(settings);

    assertThat(provider.getPaths()).containsOnly(Paths.get("/my/path"));
  }

  @Test
  public void should_return_empty_if_nothing_specified_and_default_doesnt_exist() throws IOException {
    assertThat(provider.getPaths()).isEmpty();
  }
}
