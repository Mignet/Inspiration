/*******************************************************************************
 * Copyright 2015 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.ai.pfa.indexed;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.utils.Array;

public class IndexedAStarPathFinderTest {

	@Test
	public void searchNodePath_WhenSearchingAdjacentTile_ExpectedOuputPathLengthEquals2 () {
		// @off - disable libgdx formatter
		final String graphDrawing =
				"..........\n" +
				"..........\n" +
				"..........";
		// @on - enable libgdx formatter

		final MyGraph graph = createGraphFromTextRepresentation(graphDrawing);

		final IndexedAStarPathFinder<MyNode> pathfinder = new IndexedAStarPathFinder<>(graph);

		final GraphPath<MyNode> outPath = new DefaultGraphPath<>();

		// @off - disable libgdx formatter
		// 0123456789
		// .......... 0
		// .....S.... 10
		// .....E.... 20
		// @on - enable libgdx formatter
		final boolean searchResult1 = pathfinder.searchNodePath(graph.nodes.get(15), graph.nodes.get(25), new ManhattanDistance(),
			outPath);

		Assert.assertTrue("Unexpected search result", searchResult1);
		Assert.assertEquals("Unexpected number of nodes in path", 2, outPath.getCount());

		// @off - disable libgdx formatter
		// 0123456789
		// .......... 0
		// .....SE... 10
		// .......... 20
		// @on - enable libgdx formatter
		outPath.clear();
		final boolean searchResult2 = pathfinder.searchNodePath(graph.nodes.get(15), graph.nodes.get(16), new ManhattanDistance(),
			outPath);

		Assert.assertTrue("Unexpected search result", searchResult2);
		Assert.assertEquals("Unexpected number of nodes in path", 2, outPath.getCount());

		// @off - disable libgdx formatter
		// 0123456789
		// .......... 0
		// ....ES.... 10
		// .......... 20
		// @on - enable libgdx formatter
		outPath.clear();
		final boolean searchResult3 = pathfinder.searchNodePath(graph.nodes.get(15), graph.nodes.get(14), new ManhattanDistance(),
			outPath);

		Assert.assertTrue("Unexpected search result", searchResult3);
		Assert.assertEquals("Unexpected number of nodes in path", 2, outPath.getCount());

		// @off - disable libgdx formatter
		// 0123456789
		// .....E.... 0
		// .....S.... 10
		// .......... 20
		// @on - enable libgdx formatter
		outPath.clear();
		final boolean searchResult4 = pathfinder.searchNodePath(graph.nodes.get(15), graph.nodes.get(5), new ManhattanDistance(),
			outPath);

		Assert.assertTrue("Unexpected search result", searchResult4);
		Assert.assertEquals("Unexpected number of nodes in path", 2, outPath.getCount());
	}

	@Test
	public void searchNodePath_WhenSearchCanHitDeadEnds_ExpectedOuputPathFound () {
		// @off - disable libgdx formatter
		final String graphDrawing =
			".#.#.......#..#...............\n" +
			".#............#.....#..#####..\n" +
			"...#.#######..#.....#.........\n" +
			".#.#.#........#.....########..\n" +
			".###.#....#####.....#......##.\n" +
			".#...#....#.........#...##....\n" +
			".#####....#.........#....#....\n" +
			".#........#.........#....#####\n" +
			".####....##.........#......#..\n" +
			"....#...............#......#..";
		// @on - enable libgdx formatter

		final MyGraph graph = createGraphFromTextRepresentation(graphDrawing);

		final IndexedAStarPathFinder<MyNode> pathfinder = new IndexedAStarPathFinder<>(graph);

		final GraphPath<MyNode> outPath = new DefaultGraphPath<>();

		// @off - disable libgdx formatter
		// 012345678901234567890123456789
		// S#.#.......#..#............... 0
		// .#............#.....#..#####.. 30
		// ...#.#######..#.....#......... 60
		// .#.#.#........#.....########.. 90
		// .###.#....#####.....#......##. 120
		// .#...#....#.........#...##.... 150
		// .#####....#.........#....#.... 180
		// .#E.......#.........#....##### 210
		// .####....##.........#......#.. 240
		// ....#...............#......#.. 270
		// @on - enable libgdx formatter
		final boolean searchResult = pathfinder.searchNodePath(graph.nodes.get(0), graph.nodes.get(212), new ManhattanDistance(),
			outPath);

		Assert.assertTrue("Unexpected search result", searchResult);
		Assert.assertEquals("Unexpected number of nodes in path", 32, outPath.getCount());
	}

	@Test
	public void searchNodePath_WhenDestinationUnreachable_ExpectedNoOuputPathFound () {
		// @off - disable libgdx formatter
		final String graphDrawing =
			".....#....\n" +
			".....#....\n" +
			".....#....";
		// @on - enable libgdx formatter

		final MyGraph graph = createGraphFromTextRepresentation(graphDrawing);

		final IndexedAStarPathFinder<MyNode> pathfinder = new IndexedAStarPathFinder<>(graph);

		final GraphPath<MyNode> outPath = new DefaultGraphPath<>();

		// @off - disable libgdx formatter
		// 0123456789
		// S....#...E 0
		// .....#.... 10
		// .....#.... 20
		// @on - enable libgdx formatter
		final boolean searchResult = pathfinder.searchNodePath(graph.nodes.get(0), graph.nodes.get(9), new ManhattanDistance(),
			outPath);

		Assert.assertFalse("Unexpected search result", searchResult);
	}

	private static MyGraph createGraphFromTextRepresentation (final String graphTextRepresentation) {
		final String[][] tiles = createStringTilesFromGraphTextRepresentation(graphTextRepresentation);

		final int numRows = tiles[0].length;
		final int numCols = tiles.length;

		final MyNode[][] nodes = new MyNode[numCols][numRows];
		final Array<MyNode> indexedNodes = new Array<>(numCols * numRows);

		int index = 0;
		for (int y = 0; y < numRows; y++) {
			for (int x = 0; x < numCols; x++, index++) {
				nodes[x][y] = new MyNode(index, x, y, 4);
				indexedNodes.add(nodes[x][y]);
			}
		}

		for (int y = 0; y < numRows; y++, index++) {
			for (int x = 0; x < numCols; x++, index++) {
				if (tiles[x][y].equals("#")) {
					continue;
				}

				if (x - 1 >= 0 && tiles[x - 1][y].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x - 1][y]));
				}

				if (x + 1 < numCols && tiles[x + 1][y].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x + 1][y]));
				}

				if (y - 1 >= 0 && tiles[x][y - 1].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x][y - 1]));
				}

				if (y + 1 < numRows && tiles[x][y + 1].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x][y + 1]));
				}

			}
		}

		return new MyGraph(indexedNodes);
	}

	private static String[][] createStringTilesFromGraphTextRepresentation (final String graphTextRepresentation) {
		final String[] rows = graphTextRepresentation.split("\n");

		final int numRows = rows.length;
		final int numCols = rows[0].length();

		final String[][] tiles = new String[numCols][numRows];

		for (int y = 0; y < numRows; y++) {
			final String row = rows[y];
			for (int x = 0; x < numCols; x++) {
				tiles[x][y] = "" + row.charAt(x);
			}
		}

		return tiles;
	}

	private static class MyNode {

		private final int index;
		private final int x;
		private final int y;
		private final Array<Connection<MyNode>> connections;

		public MyNode (final int index, final int x, final int y, final int capacity) {
			this.index = index;
			this.x = x;
			this.y = y;
			this.connections = new Array<>(capacity);
		}

		public int getIndex () {
			return index;
		}

		public Array<Connection<MyNode>> getConnections () {
			return connections;
		}

		@Override
		public String toString () {
			return "IndexedNodeFake [index=" + index + ", x=" + x + ", y=" + y + ", connections=" + connections + "]";
		}

	}

	private static class MyGraph implements IndexedGraph<MyNode> {

		protected Array<MyNode> nodes;

		public MyGraph (Array<MyNode> nodes) {
			this.nodes = nodes;
		}

		@Override
		public int getIndex (MyNode node) {
			return node.getIndex();
		}

		@Override
		public Array<Connection<MyNode>> getConnections (MyNode fromNode) {
			return fromNode.getConnections();
		}

		@Override
		public int getNodeCount () {
			return nodes.size;
		}
	}

	private static class ManhattanDistance implements Heuristic<MyNode> {
		@Override
		public float estimate (final MyNode node, final MyNode endNode) {
			return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y);
		}
	}

}
