
function traceRoad()
    clf;
    isoview(-3,-3,3,3)
    plot2d(0,0,rect=[-3,-3,3,3]); 
    n=0;
    x=0;
    y=0;
    bt=0;
    for n=1:2
        [bt,x(n),y(n)]=xclick()
        plot(x(n),y(n),"ro");
        if  n>1
            //plot([x(n-1),x(n)],[y(n-1),y(n)],3,'LineWidth', 2)
        end
    end
    plot(x,y)
    z=calcZ(x,y)
    fXYz(x,y,z)
endfunction

function roadInter()
    clf;
    isoview(-3,-3,3,3)
    plot2d(0,0,rect=[-3,-3,3,3]); 
    n=0;
    x1=0;
    y1=0;
    bt=0;
    for n=1:2
        [bt,x1(n),y1(n)]=xclick()
        plot(x1(n),y1(n),"ro");
    end
    
    z1=calcZ(x1,y1)
    [fX1,fY1]=fXYz(x1,y1,z1)
    tracefXYzw(fX1,fY1,z1,0,"b")
    
    x2=0;
    y2=0;
    bt=0;
    for n=1:2
        [bt,x2(n),y2(n)]=xclick()
        plot(x2(n),y2(n),"go");
    end
    z2=calcZ(x1,y1)
    [fX2,fY2]=fXYz(x2,y2,z2)
    tracefXYzw(fX2,fY2,z2,0,"m")
    
    
    [p1,p2]=findInter(fX1,fY1,fX2,fY2)
    
    tracefXYzPoint(fX1,fY1,p1)
endfunction

function [z]=calcZ(x,y)
    z(1)=0;
    z(2)=sqrt((x(1)-x(2))^2+(y(1)-y(2))^2);
endfunction

function [fXz,fYz]=fXYz(x,y,z)
    fXz(1)=x(1)
    fXz(2)=(x(2)-x(1))/z(2)
    fYz(1)=y(1)
    fYz(2)=(y(2)-y(1))/z(2)
    fXz(3)=fYz(2)
    fYz(3)=-fXz(2)
    
endfunction

function tracefXYzw(fX,fY,z,w,c)
    k=1
    for n=0:0.01:z(2)
        Xz(k)=fX(1)+n*fX(2)+w*fX(3)
        Yz(k)=fY(1)+n*fY(2)+w*fY(3)
        k=k+1
    end
    
    plot(Xz,Yz,c);
endfunction

function tracefXYzPoint(fX,fY,z)
    Xz=fX(1)+z*fX(2)
    Yz=fY(1)+z*fY(2)
    
    plot(Xz,Yz,"Ro");
endfunction

function [p1,p2]=findInter(fX1,fY1,fX2,fY2)
    M1=[fX1(2),-fX2(2);fY1(2),-fY2(2)]
    R1=[fX2(1)-fX1(1);fY2(1)-fY1(1)]
    disp(M1)
    disp(R1)
    
    k=M1(1,1)/M1(2,1)
    M1(1,:)=M1(1,:)-M1(2,:)*k
    
    disp(M1)
    R1(1)=R1(1)-R1(2)*k
    disp(R1)
    
    p2=R1(1)/M1(1,2);
    p1=(R1(2)-p2*M1(2,2))/M1(2,1)
    
endfunction

roadInter();

//traceRoad();
