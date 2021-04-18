/* *****************************************************************************
 *  Name:    Ada Lovelace
 *  NetID:   alovelace
 *  Precept: P00
 *
 *  Description:  Prints 'Hello, World' to the terminal window.
 *                By tradition, this is everyone's first program.
 *                Prof. Brian Kernighan initiated this tradition in 1974.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;


public class Percolation
{

    private final boolean[][] block;
    private int openSite = 0;
    private final WeightedQuickUnionUF topUF;
    private final WeightedQuickUnionUF commonUF;
    private final int virtualTop;
    private final int virtualBottom;
    private final int rowLength;

    /**
     * Init nxn grid with 1 ( 1 - non blocked  , 0 - blocked )
     *
     * @param n
     */
    public Percolation(int n)
    {
        if (n <= 0)
            throw new IllegalArgumentException("n should greater than 0");
        this.block = new boolean[n][n];
        // Tracking of full site, prevent back wash from happening if use only one UF
        this.topUF = new WeightedQuickUnionUF(n * n + 1);
        // Tracking of percolations
        this.commonUF = new WeightedQuickUnionUF(n * n + 2);
        this.virtualTop = n * n;
        this.virtualBottom = n * n + 1;
        this.rowLength = n;
    }

    public void open(int row, int col)
    {
        if (!this.isOpen(row, col))
        {
            openCurrentTile(row, col);
            int currentIndex = this.combineCoordinate(row, col) - 1;
            connectToVirtualPoint(row, currentIndex);
            connectToSurroundedBlock(row, col, currentIndex);
        }
    }

    private void openCurrentTile(int row, int col)
    {
        this.block[row - 1][col - 1] = true;
        this.openSite++;
    }

    private void connectToSurroundedBlock(int row, int col, int currentIndex)
    {
        if (isBlockAvailable(row - 1, col))
        {
            this.doUnion(row - 1, col, currentIndex);
        }
        if (isBlockAvailable(row + 1, col))
        {
            this.doUnion(row + 1, col, currentIndex);
        }

        if (isBlockAvailable(row, col - 1))
        {
            this.doUnion(row, col - 1, currentIndex);
        }
        if (isBlockAvailable(row, col + 1))
        {
            this.doUnion(row, col + 1, currentIndex);
        }
    }

    private boolean isBlockAvailable(int row, int col)
    {
        return isOnGrid(row, col) && isOpen(row, col);
    }

    private boolean isOnGrid(int row, int col)
    {
        int shiftedRow = row - 1;
        int shiftedCol = col - 1;
        return shiftedRow >= 0 && shiftedCol >= 0 && shiftedRow < this.rowLength
                && shiftedCol < this.rowLength;
    }

    private void doUnion(int row, int col, int currentIndex)
    {
        this.topUF.union(currentIndex, this.combineCoordinate(row, col) - 1);
        this.commonUF.union(currentIndex, this.combineCoordinate(row, col) - 1);
    }

    private void connectToVirtualPoint(int row, int currentIndex)
    {
        if (row == 1)
        {
            this.commonUF.union(this.virtualTop, currentIndex);
            this.topUF.union(this.virtualTop, currentIndex);

        }
        if (row == this.rowLength)
        {
            this.commonUF.union(this.virtualBottom, currentIndex);
        }
    }


    private int combineCoordinate(int row, int col)
    {
        return this.rowLength * (row - 1) + col;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col)
    {
        this.validateSite(row, col);
        return this.block[row - 1][col - 1];
    }

    /**
     * Condition to check if full:
     *
     * @param row
     * @param col
     * @return
     * @constraint : Tile is full if it is connected to top through chain of neighbouring tiles
     * In case use only one QuickUnionFind, backwash will happen, since 2 virtual points
     * on the same UF will in the end connect to each other.
     * To prevent the back wash. @constructor, 2 UF were created:
     * + First one is used to check for top connection
     * + Second one is used for percolation
     * For eg: First ones connect to 100 at top. Second ones connect to 101 at top and 100 at bottom
     * When checking for isFull, there are 3 conditions must be satisfied :
     * 1. Node that connect to top handled by quickTopUnion
     * 2. Node that connect top top handled by quickCommonUnion
     * 3. Node that is not blocked ( isOpen)
     * (*ref)Eventually, 2nd  condition will be true in the case it percolates.
     * But 1st will prevent the backwash since the quickTopUnionFind cannot find any node
     * that connect to top. Only node in quickCommonUnion connect(->ref)
     */
    public boolean isFull(int row, int col)
    {
        this.validateSite(row, col);
        int currentCoordinate = this.combineCoordinate(row, col) - 1;
        return this.topUF.find(this.virtualTop) == this.topUF.find(currentCoordinate);
    }

    private void validateSite(int row, int col)
    {
        if (row < 0 || col < 0 || row > this.block.length || col > this.block.length)
            throw new IllegalArgumentException("Row or column should be greater than 0.");
    }

    // returns the number of open sites
    public int numberOfOpenSites()
    {
        return this.openSite;
    }

    // does the system percolate?
    public boolean percolates()
    {
        int topConnect = this.commonUF.find(this.virtualTop);
        int bottomConnect = this.commonUF.find(this.virtualBottom);
        return topConnect == bottomConnect;
    }

    public static void main(String[] args)
    {
        // Empty
    }

}
