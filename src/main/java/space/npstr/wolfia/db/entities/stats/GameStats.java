/*
 * Copyright (C) 2017 Dennis Neufeld
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package space.npstr.wolfia.db.entities.stats;

import space.npstr.sqlsauce.entities.SaucedEntity;
import space.npstr.wolfia.game.definitions.Games;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by napster on 30.05.17.
 * <p>
 * Describe a game that happened
 * <p>
 */
@Entity
@Table(name = "stats_game")
public class GameStats extends SaucedEntity<Long, GameStats> {

    private static final long serialVersionUID = -577030472501735570L;

    //this is pretty much an auto incremented id generator starting by 1 and going 1 upwards
    //there are no hard guarantees that there wont be any gaps, or that they will be in any order in the table
    //that's good enough for our use case though (giving games an "easy" to remember number to request replays and stats
    //later, and passively showing off how many games the bot has done)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id", nullable = false, updatable = false)
    private long gameId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "game", orphanRemoval = true)
    @Column(name = "starting_teams")
    private Set<TeamStats> startingTeams = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "game", orphanRemoval = true)
    @Column(name = "actions")
    private Set<ActionStats> actions = new HashSet<>();

    @Column(name = "start_time", nullable = false)
    private long startTime;

    @Column(name = "end_time", nullable = false)
    private long endTime;

    @Column(name = "guild_id", nullable = false)
    private long guildId;

    //name of the guild at the time of creation
    @Column(name = "guild_name", nullable = false, columnDefinition = "text")
    private String guildName;

    @Column(name = "channel_id", nullable = false)
    private long channelId;

    @Column(name = "channel_name", nullable = false, columnDefinition = "text")
    private String channelName;

    @Column(name = "game_type", nullable = false, columnDefinition = "text")
    private String gameType;

    @Column(name = "game_mode", nullable = false, columnDefinition = "text")
    private String gameMode;

    @Column(name = "player_size", nullable = false)
    private int playerSize;


    public GameStats(final long guildId, final String guildName, final long channelId, final String channelName,
                     final Games gameType, final String gameMode, final int playerSize) {
        this.guildId = guildId;
        this.guildName = guildName;
        this.channelId = channelId;
        this.channelName = channelName;
        this.startTime = System.currentTimeMillis();
        this.gameType = gameType.name();
        this.gameMode = gameMode;
        this.playerSize = playerSize;
    }

    public void addAction(final ActionStats action) {
        this.actions.add(action);
    }

    public void addActions(final Collection<ActionStats> actions) {
        this.actions.addAll(actions);
    }

    public void addTeam(final TeamStats team) {
        this.startingTeams.add(team);
    }

    //do not use the autogenerated id, it will only be set after persisting
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.guildId ^ (this.guildId >>> 32));
        result = prime * result + (int) (this.channelId ^ (this.channelId >>> 32));
        result = prime * result + (int) (this.startTime ^ (this.startTime >>> 32));
        return result;
    }

    //do not compare the autogenerated id, it will only be set after persisting
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof GameStats)) {
            return false;
        }
        final GameStats g = (GameStats) obj;
        return this.startTime == g.startTime && this.guildId == g.guildId && this.channelId == g.channelId;
    }

    //########## boilerplate code below
    GameStats() {
    }

    @Override
    @Nonnull
    public Long getId() {
        return this.gameId;
    }

    @Nonnull
    @Override
    public GameStats setId(final Long gameId) {
        this.gameId = gameId;
        return this;
    }

    public Set<TeamStats> getStartingTeams() {
        return this.startingTeams;
    }

    public void setStartingTeams(final Set<TeamStats> startingTeams) {
        this.startingTeams = startingTeams;
    }

    public Set<ActionStats> getActions() {
        return this.actions;
    }

    public void setActions(final Set<ActionStats> actions) {
        this.actions = actions;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }

    public long getGuildId() {
        return this.guildId;
    }

    public void setGuildId(final long guildId) {
        this.guildId = guildId;
    }

    public String getGuildName() {
        return this.guildName;
    }

    public void setGuildName(final String guildName) {
        this.guildName = guildName;
    }

    public long getChannelId() {
        return this.channelId;
    }

    public void setChannelId(final long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public void setChannelName(final String channelName) {
        this.channelName = channelName;
    }

    public Games getGameType() {
        return Games.valueOf(this.gameType);
    }

    public void setGameType(final Games game) {
        this.gameType = game.name();
    }

    public String getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(final String gameMode) {
        this.gameMode = gameMode;
    }

    public int getPlayerSize() {
        return this.playerSize;
    }

    public void setPlayerSize(final int playerSize) {
        this.playerSize = playerSize;
    }
}
