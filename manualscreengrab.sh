#! /nix/store/kq5si67lhbidwkw444kc3lz3x3l7nxy6-bash-5.1-p8/bin/bash -e
export JAVA_HOME=${JAVA_HOME-'/nix/store/qhj15x1nj3sfxik19ibvx1999paglndy-android-studio-canary-2021.2.1.3-unwrapped/jre'}
export ANDROID_EMULATOR_USE_SYSTEM_LIBS='1'
export QT_XKB_CONFIG_ROOT='/nix/store/7wrqfsrck767wg6f07j39ndsqfxv5p1n-xkeyboard-config-2.33/share/X11/xkb'
export FONTCONFIG_FILE='/nix/store/x6fxsv1srx7kiwas1f1r518ypa3b44r8-fonts.conf'
export PATH='/nix/store/5wch96kji9zlffxjqpjdrszjzp4i7m3a-coreutils-9.0/bin:/nix/store/0hj8idivq7434gbsi0jx3dfhd7bsgb3a-findutils-4.8.0/bin:/nix/store/vksla9jd81f6bd90l50yvbx48cvk8pql-gnugrep-3.7/bin:/nix/store/3873gikx7706qfh0mv82awvw9b9iwhcw-which-2.21/bin:/nix/store/g1ixda6g2bbwh77aglm8161pd7qnsxz1-gnused-4.8/bin:/nix/store/w8nrkhxxi10pzgd4iq69hq0rwrixcd7z-file-5.41/bin:/nix/store/k684x21djlwdjycghxyy7d129zr5y1am-glxinfo-8.4.0/bin:/nix/store/001xkf118dz0b1cfvqp1ghafn3vn5b13-pciutils-3.7.0/bin:/nix/store/h470368fys826k6l5shmhxa8zl6zhgzn-setxkbmap-1.3.2/bin:/nix/store/3s8bwqw4glwv0qa7gy900cycnj4301sj-gnutar-1.34/bin:/nix/store/6x6rgqrmxd5j0llw42zzpbgl2kw5dyvf-gzip-1.11/bin:/nix/store/qc34acwphyjjr2y5vxyz1q9vz8hrk1ik-git-2.33.1/bin:/nix/store/canhzj7r2p5rmfi70lfcwdc9mxhb9c3z-ps-procps-3.3.16/bin'${PATH:+':'}$PATH
export LD_LIBRARY_PATH='/nix/store/p17b5jyml2hrj79f9gz0kg8zh6d0awsc-fontconfig-2.13.94-lib/lib:/nix/store/mbmf8p3ss808gmv76n2ns58fp3di4ljq-freetype-2.11.0/lib:/nix/store/79bp4xrqhs15gc8gv0qqr6zzgm1d0yb1-libXext-1.3.4/lib:/nix/store/p3vh84v2mlj6i25ky9i6lp1i56f1grgx-libXi-1.8/lib:/nix/store/165gxvjixkiq7iqd8xbv37v7bz602s5z-libXrender-0.9.10/lib:/nix/store/459bnw83zn290s278mlw226s811mlh45-libXtst-1.2.3/lib:/nix/store/vq7r6jvhn3mffzvi0x7w478llls7h2jv-gcc-10.3.0-lib/lib:/nix/store/a7b6pb51d9kfi79hc23pzl7919vg3919-gcc-10.3.0-lib/lib:/nix/store/swzzy9z4pfkrx27glzyi2lfarsazd7sy-zlib-1.2.11/lib:/nix/store/m92iwy2b8chvc390hgv0mk28wbl09hs8-zlib-1.2.11/lib:/nix/store/a3mb795si7kmdpyqxsv164mh4b0q37f5-libXrandr-1.5.2/lib:/nix/store/nj3xyp0jkq5rvdj00hj3x0aaxrbyazrr-alsa-lib-1.2.5.1/lib:/nix/store/7v6bhil80mgi7zn083qf9g0kqqwjh4db-dbus-1.12.20-lib/lib:/nix/store/infdrbnz70gl77fp1ryll0m595zl3988-expat-2.4.3/lib:/nix/store/2yb6nxnavzlxhvkz3z0aa6ff9a4pjqmv-libpulseaudio-14.2/lib:/nix/store/vvhzh59s7pnkrxnzxsy2pzyyrd1r1kxs-util-linux-2.37.3-lib/lib:/nix/store/9z1hyyfajc0dcl246lanh0q6snh88dbh-libX11-1.7.2/lib:/nix/store/63vqdf5vqvmb959zdhl739j4iba1asng-libxcb-1.14/lib:/nix/store/z0b0szgchacxlwr8bham73a3rc9kq0y8-libXcomposite-0.4.5/lib:/nix/store/d2vhqpnln88wdrmx3x92cn2iw2n0lwzf-libXcursor-1.2.0/lib:/nix/store/0hp0c2b4yj6in9vkn0hhdcx5g5365v33-libXdamage-1.1.5/lib:/nix/store/1c8hx8006zxc685ycjl23ilr280wspyd-libXfixes-6.0.0/lib:/nix/store/f311jicd48msgbms7r3yd4cnifpcdkdx-libGL-1.3.4/lib:/nix/store/gg4ysakbniwv6nq42ya71b54dv6x4zqp-nspr-4.32/lib:/nix/store/w75i2znchksazx5c5hhq9wz8d4jkbsxz-nss-3.73/lib:/nix/store/kxk1v614466fcv8qwaqjnpgd0wkhinnx-systemd-249.7/lib:/nix/store/6vlndxxx64p5dfpg2gypr8c71calkz6g-gtk+-2.24.33/lib:/nix/store/xgngl8y1yswxrsw85a4nc2laja8c9784-gnome-vfs-2.24.4/lib:/nix/store/p4djhkamspymchvps0sz8y5y135z42j9-glib-2.70.1/lib:/nix/store/n090x7x6sf0bvdmvv3i0cnnhcq3rzi6q-gconf-3.2.6/lib'${LD_LIBRARY_PATH:+':'}$LD_LIBRARY_PATH
export PATH="$PATH:~/Android/Sdk/build-tools/32.0.0"
export ANDROID_HOME=~/Android/Sdk/
export LC_ALL=fr_FR.UTF-8
export LANG=fr_FR.UTF-8
export DEVICE=R58R435L9NA
export DEVICE=QDG9X18B29G01653
export appPackageName=org.scotthamilton.trollslate
export locales=(en-US fr-FR de-DE in)
export screengrabDir=screengrab
# export screengrabDir=app_screengrab
export deviceScreensDir=/storage/F25E-151D/Android/data/$appPackageName/files/$screengrabDir
# export deviceScreensDir=/data/user/0/org.scotthamilton.trollslate/$screengrabDir
screenslocale(){
	/home/scott/Android/Sdk/platform-tools/adb -s $DEVICE \
		shell am instrument \
			--no-window-animation -w \
			-e testLocale $1 \
			-e endingLocale fr_FR \
			-e appendTimestamp true \
			-e package $appPackageName \
			$appPackageName.test/androidx.test.runner.AndroidJUnitRunner
}
export debugApk=app/build/outputs/apk/debug/app-debug.apk
export androidTestApk=app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb -s $DEVICE install -t -r $debugApk
adb -s $DEVICE install -t -r $androidTestApk
temp=$(mktemp -d)
adb -s $DEVICE shell "run-as $appPackageName sh -c \"rm -rf $deviceScreensDir\""
adb -s $DEVICE shell pm grant $appPackageName android.permission.CHANGE_CONFIGURATION
adb -s $DEVICE shell pm grant $appPackageName android.permission.WRITE_EXTERNAL_STORAGE
adb -s $DEVICE shell pm grant $appPackageName android.permission.READ_EXTERNAL_STORAGE
for l in ${locales[@]}
do
	screenslocale $l
done
pushd $temp
ls -lh
echo "$deviceScreensDir"
adb -s $DEVICE shell "run-as $appPackageName sh -c \"cd $deviceScreensDir/.. && tar czpf - $screengrabDir\"" | tar xzpf - -C .
popd
for l in ${locales[@]}
do
	fastlaneimagesdir=fastlane/metadata/android/$l/images/phoneScreenshots
	if [ ! -d "$fastlaneimagesdir" ]; then
		echo "kreating \`$fastlaneimagesdir\`"
		mkdir -p $fastlaneimagesdir
	else
		echo "Deletting \`$fastlaneimagesdir\`"
		rm -rf "$fastlaneimagesdir"
	fi
	cp -r "$temp/$screengrabDir/$l/images/screenshots" "$fastlaneimagesdir"
done
rm -rf $temp
