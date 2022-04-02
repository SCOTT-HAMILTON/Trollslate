{ pkgs ? import <nixpkgs> {} }:
with pkgs; mkShell {
  buildInputs = [ fastlane ];
  shellHook = ''
    export LC_ALL=fr_FR.UTF-8
    export LANG=fr_FR.UTF-8
    makescreens(){
      ANDROID_HOME=~/Android/Sdk/ /nix/store/31d9chzig76rkfj29k8rv0lc6j4yz0vc-android-studio-canary-2021.2.1.3-fhs-env/bin/android-studio-canary-2021.2.1.3-fhs-env -c './manualscreengrab.sh'
    }
  '';
}

