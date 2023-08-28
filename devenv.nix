{ pkgs, ... }:

{

  packages = with pkgs; [ git pre-commit checkstyle nixfmt ];

  # https://devenv.sh/languages/
   languages.nix.enable = true;
   languages.java.enable = true;
   languages.java.gradle.enable = true;

  # https://devenv.sh/pre-commit-hooks/
  # pre-commit.hooks.shellcheck.enable = true;

  # https://devenv.sh/processes/
  # processes.ping.exec = "ping example.com";

  # See full reference at https://devenv.sh/reference/options/
}
