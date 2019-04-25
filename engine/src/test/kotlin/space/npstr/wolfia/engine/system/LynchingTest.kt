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

import space.npstr.wolfia.engine.BaseTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LynchingTest : BaseTest() {

    val p1 = goodie()
    val p2 = goodie()
    val p3 = goodie()

    var lynching: Lynching = Lynching()

    @BeforeTest
    fun setup() {
        lynching = Lynching()
    }

    @Test
    fun `simple majority`() {
        lynching.lynchVote(p1, p3)
        lynching.lynchVote(p2, p3)
        lynching.lynchVote(p3, p2)

        val lynchVoteResult = lynching.determineResult()
        assertTrue(lynchVoteResult is Lynch)
        assertEquals(p3, lynchVoteResult.winner)
    }

    @Test
    fun `simple majority with multiple revotes`() {
        lynching.lynchVote(p1, p3)
        lynching.lynchVote(p2, p3)
        lynching.lynchVote(p3, p2)
        lynching.lynchVote(p3, p2)
        lynching.lynchVote(p3, p2)


        val lynchVoteResult = lynching.determineResult()
        assertTrue(lynchVoteResult is Lynch)
        assertEquals(p3, lynchVoteResult.winner)
    }


    @Test
    fun `no votes`() {
        val lynchVoteResult = lynching.determineResult()
        assertTrue(lynchVoteResult is NoVotes)
    }

    @Test
    fun `no votes with unvotes`() {
        lynching.lynchVote(p1, p2)
        lynching.removeLynchVote(p1)
        val lynchVoteResult = lynching.determineResult()
        assertTrue(lynchVoteResult is NoVotes)
    }

    @Test
    fun `two player tie`() {
        lynching.lynchVote(p1, p2)
        lynching.lynchVote(p2, p1)
        val lynchVoteResult = lynching.determineResult()
        assertTrue(lynchVoteResult is Tie)
        assertEquals(2, lynchVoteResult.tied.size)
        assertTrue(lynchVoteResult.tied.contains(p1))
        assertTrue(lynchVoteResult.tied.contains(p2))
    }

    @Test
    fun `three player tie`() {
        lynching.lynchVote(p1, p2)
        lynching.lynchVote(p2, p3)
        lynching.lynchVote(p3, p1)

        val lynchVoteResult = lynching.determineResult()
        assertTrue(lynchVoteResult is Tie)
        assertEquals(3, lynchVoteResult.tied.size)
        assertTrue(lynchVoteResult.tied.contains(p1))
        assertTrue(lynchVoteResult.tied.contains(p2))
        assertTrue(lynchVoteResult.tied.contains(p3))
    }

    @Test
    fun `selfvote_is_possible`() {
        lynching.lynchVote(p1, p1)
        val lynchVoteResult = lynching.determineResult()
        assertTrue(lynchVoteResult is Lynch)
        assertEquals(p1, lynchVoteResult.winner)
    }

    @Test
    fun `clear votes`() {
        lynching.lynchVote(p1, p2)
        assertTrue(lynching.determineResult() is Lynch)
        lynching.clearLynchVotes()
        assertTrue(lynching.determineResult() is NoVotes)
    }

}
