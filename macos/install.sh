mkdir -p /Applications/.songreader/
cp songreader.scpt /Applications/.songreader/songreader.scpt
cp songreader.plist ~/Library/LaunchAgents/songreader.plist
launchctl load  ~/Library/LaunchAgents/songreader.plist
