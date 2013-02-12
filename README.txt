### INTRODUCTION ###

TantRigged is a fast backtracking solver for Tantrix Discovery puzzles.


### USAGE ###

The puzzle set must be encoded in the enum PuzzleSet and TantRigged
should be constructed with the PuzzleSet object to be solved. Then
TantRigged.solve(cnt, col) will find all solutions (closed rings) using
the first cnt elements of the set and making the closed ring with the
given colour col.


### LICENSE ###

TantRigged is distributed under the GNU General Public License Version 3.
A copy of the license is found in LICENSE.txt.
