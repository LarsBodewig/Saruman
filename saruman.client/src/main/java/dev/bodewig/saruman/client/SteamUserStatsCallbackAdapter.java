package dev.bodewig.saruman.client;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamLeaderboardEntriesHandle;
import com.codedisaster.steamworks.SteamLeaderboardHandle;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUserStatsCallback;

public class SteamUserStatsCallbackAdapter implements SteamUserStatsCallback {

	@Override
	public void onUserStatsReceived(long gameId, SteamID steamIDUser, SteamResult result) {
	}

	@Override
	public void onUserStatsStored(long gameId, SteamResult result) {
	}

	@Override
	public void onUserStatsUnloaded(SteamID steamIDUser) {
	}

	@Override
	public void onUserAchievementStored(long gameId, boolean isGroupAchievement, String achievementName,
			int curProgress, int maxProgress) {
	}

	@Override
	public void onLeaderboardFindResult(SteamLeaderboardHandle leaderboard, boolean found) {
	}

	@Override
	public void onLeaderboardScoresDownloaded(SteamLeaderboardHandle leaderboard, SteamLeaderboardEntriesHandle entries,
			int numEntries) {
	}

	@Override
	public void onLeaderboardScoreUploaded(boolean success, SteamLeaderboardHandle leaderboard, int score,
			boolean scoreChanged, int globalRankNew, int globalRankPrevious) {
	}

	@Override
	public void onGlobalStatsReceived(long gameId, SteamResult result) {
	}

}
