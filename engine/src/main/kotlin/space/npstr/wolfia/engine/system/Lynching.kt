/*
 * Copyright (C) 2017-2019 Dennis Neufeld
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

package space.npstr.wolfia.engine.system

import space.npstr.wolfia.engine.Player

class Lynching {

    private val votes: MutableMap<Player, Player> = LinkedHashMap() //using linked to keep first votes at the top

    fun lynchVote(voter: Player, target: Player) {
        votes[voter] = target
    }

    fun removeLynchVote(voter: Player) {
        votes.remove(voter)
    }

    fun clearLynchVotes() {
        votes.clear()
    }

    fun determineResult(): LynchVoteResult {
        val lynchResultCalculator = LynchResultCalculator()
        return lynchResultCalculator.calculate(votes)
    }
}

private class LynchResultCalculator {

    fun calculate(votes: Map<Player, Player>): LynchVoteResult {
        if (votes.entries.isEmpty()) return NoVotes

        val aggregated: MutableMap<Player, Int> = HashMap()

        votes.entries.forEach {
            val target = it.value
            aggregated.compute(target) { _, v -> (v ?: 0) + 1 }
        }

        val grouped: MutableMap<Int, MutableSet<Player>> = HashMap()
        var mostVotes = Int.MIN_VALUE
        aggregated.entries.forEach {
            val target = it.key
            val votesAmount = it.value
            mostVotes = Math.max(mostVotes, votesAmount)
            grouped.compute(votesAmount) { _, v ->
                val set = (v ?: HashSet())
                set.add(target)
                return@compute set
            }
        }

        val mostVotedPlayers = grouped[mostVotes] ?: HashSet()
        if (mostVotedPlayers.isEmpty()) {
            return NoVotes
        }
        if (mostVotedPlayers.size == 1) {
            return Lynch(mostVotedPlayers.first())
        }
        return Tie(mostVotedPlayers)
    }
}

sealed class LynchVoteResult

data class Tie(val tied: Collection<Player>) : LynchVoteResult()
data class Lynch(val winner: Player) : LynchVoteResult()
object NoVotes : LynchVoteResult()
