/*
 * SPDX-FileCopyrightText: 2023 JohnnyJayJay
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

module codosseum {

  requires io.micronaut.core;
  requires io.micronaut.reactor.micronaut_reactor;
  requires io.micronaut.serde.micronaut_serde_api;
  requires io.micronaut.http_client;
  requires io.micronaut.http_server;
  requires io.swagger.v3.oas.annotations;
  requires io.soabase.recordbuilder.core;
  requires jakarta.annotation;
  requires jakarta.validation;
  requires com.fasterxml.jackson.annotation;
  requires jakarta.inject;
  requires io.micronaut.security.micronaut_security;
  requires java.compiler;
  requires io.micronaut.http;
  requires io.micronaut.validation.micronaut_validation;
  requires org.reactivestreams;
  requires io.micronaut.context;

}
