package fr.enssat.lanniontech;

public class Lane {
    private Cell[] cells;

    Lane(double length){
        System.out.println(length);
        int nbCells = (int)(length/0.5);
        System.out.println(nbCells);
        cells = new Cell[nbCells];

        for(int i=0;i<nbCells;i++){
            cells[i] = new Cell();
        }

        for(int i=0;i<nbCells;i++){
            if(i!=0){
                cells[i].setPrevCell(cells[i-1]);
            }
            if(i!=nbCells-1){
                cells[i].setNextCell(cells[i+1]);
            }
        }
    }

    public Cell getEntryCell(){
        return cells[0];
    }
}
