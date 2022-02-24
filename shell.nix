{ pkgs ? import <nixpkgs> {} }:
with pkgs; mkShell {
  buildInputs = [ fastlane ];
  shellHook = ''
    export LC_ALL=fr_FR.UTF-8
    export LANG=fr_FR.UTF-8
  '';
}

