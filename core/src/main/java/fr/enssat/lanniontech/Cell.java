package fr.enssat.lanniontech;


public class Cell {
    private double length = 0.5;
    private Vehicle occupied = null;
    private Cell nextCell = null;
    private Cell prevCell = null;
    private Cell leftCell = null;
    private Cell rightCell = null;

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public Vehicle getOccupied() {
        return occupied;
    }

    public void setOccupied(Vehicle occupied) {
        if(occupied != null && this.occupied != null)
        {

        }
        this.occupied = occupied;
    }

    public Cell getNextCell() {
        return nextCell;
    }

    public void setNextCell(Cell nextCell) {
        this.nextCell = nextCell;
    }

    public Cell getPrevCell() {
        return prevCell;
    }

    public void setPrevCell(Cell prevCell) {
        this.prevCell = prevCell;
    }

    public Cell getLeftCell() {
        return leftCell;
    }

    public void setLeftCell(Cell leftCell) {
        this.leftCell = leftCell;
    }

    public Cell getRigthCell() {
        return rightCell;
    }

    public void setRigthCell(Cell rigthCell) {
        this.rightCell = rigthCell;
    }
}
