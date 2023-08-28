# SPDX-FileCopyrightText: 2023 Alexander Wood
# SPDX-License-Identifier: AGPL-3.0-or-later
{ pkgs, ... }:

{

  packages = with pkgs; [ git pre-commit checkstyle nixfmt ];

  languages.nix.enable = true;
  languages.java.enable = true;
  languages.java.gradle.enable = true;

}
