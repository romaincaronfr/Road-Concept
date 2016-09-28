package fr.enssat.lanniontech.roadElements;

import fr.enssat.lanniontech.positioning.Position;

import java.util.ArrayList;

public class Road {
    private String id;
    private double maxSpeed;
    private ArrayList<RoadSection> sections;
    private double length;
    private double timeWeigth;
    private Position A;
    private Position B;

    Road(String id,double maxSpeed){
        this.id = id;
        this.maxSpeed=maxSpeed;
        this.sections = new ArrayList<RoadSection>();
        this.length = 0;
        this.timeWeigth = 0;
        A = null;
        B = null;
    }

    private void updateRoad(){
        length = 0;
        for (int i = 0; i < sections.size() ; i++) {
            length += sections.get(i).getLength();
        }
        timeWeigth = length/maxSpeed;
    }

    public void addSection(RoadSection newSection){
        if (A==null && B==null){
            A=newSection.getA();
            B=newSection.getB();
            sections.add(newSection);
        }else if (A==newSection.getA()){
            A=newSection.getB();
            sections.add(newSection);
        }else if(A==newSection.getB()){
            A=newSection.getA();
            sections.add(newSection);
        }else if (B==newSection.getA()){
            B=newSection.getB();
            sections.add(sections.size(),newSection);
        }else if(B==newSection.getB()){
            B=newSection.getA();
            sections.add(sections.size(),newSection);
        } else return;
        updateRoad();
    }

}
