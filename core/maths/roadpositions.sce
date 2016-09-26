
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

function [z]=calcZ(x,y)
    z(1)=0;
    z(2)=sqrt((x(1)-x(2))^2+(y(1)-y(2))^2);
endfunction

function fXYz(x,y,z)
    fXz(1)=x(1)
    fXz(2)=(x(2)-x(1))/z(2)
    fYz(1)=y(1)
    fYz(2)=(y(2)-y(1))/z(2)
    k=1
    for n=0:0.1:z(2)
        Xz(k)=fXz(1)+n*fXz(2)
        Yz(k)=fYz(1)+n*fYz(2)
        k=k+1
    end
    
    vx=fXz(2)
    vy=fYz(2);
    
    vxp=vy*0.5
    vyp=-vx*0.5
    
    plot(Xz+vxp,Yz+vyp,"y");
    
    
endfunction

traceRoad();
