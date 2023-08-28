{ pkgs, ... }:

{

  packages = with pkgs; [ git pre-commit checkstyle nixfmt ];

  languages.nix.enable = true;
  languages.java.enable = true;
  languages.java.gradle.enable = true;

}
