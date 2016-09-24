
function traceRoad()
    clf;  
    plot2d(0,0,rect=[-3,-3,3,3]); 
    n=0;
    x=0;
    y=0;
    bt=0;
    for n=1:3
        [bt,x(n),y(n)]=xclick()
        plot(x(n),y(n),"ro");
        if  n>1
            plot([x(n-1),x(n)],[y(n-1),y(n)],3,'LineWidth', 2)
        end
    end
    calcZ(x,y)
endfunction

function [z]=calcZ(x,y)
    z(1)=0;
    z(2)=sqrt((x(1)-x(2))^2+(y(1)-y(2))^2);
    z(3)=sqrt((x(3)-x(2))^2+(y(3)-y(2))^2);
    z(3)=z(2)+z(3)
    z=z/z(3)
    z=z*100
    disp(z)
endfunction

traceRoad();
