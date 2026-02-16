public class Rectangle extends RigidBody {
    float width;
    float height;
    float  angle;
    Rectangle(float x , float y , float vx ,float vy , float width, float  height , float mass, float angle  ){
        super(x,y,vx,vy,mass);
        this.width = width;
        this.height  = height;
        this.angle  = angle;
    }
    float[][] getCoordinates(){
        float hw = width*0.5f;
        float hh = height*0.5f;
        float axis1x = (float)Math.cos(angle);
        float axis1y = (float)Math.sin(angle);
        float axis2x = -(float)Math.sin(angle);
        float axis2y = (float)Math.cos(angle);
        float worldX = x + hw * axis1x + (-hh) * axis2x;
        float worldY = y + hw * axis1y + (-hh) * axis2y;
        float[][] corners = new float[4][2];
        corners[0][0] = worldX; 
        corners[0][1] = worldY; 
        corners[1][0] = x + (-hw) * axis1x + (-hh) * axis2x;
        corners[1][1] = y + (-hw) * axis1y + (-hh) * axis2y;
        corners[2][0] = x + (-hw) * axis1x + (hh) * axis2x;
        corners[2][1] = y + (-hw) * axis1y + (hh) * axis2y;
        corners[3][0] = x + (hw) * axis1x + (hh) * axis2x;
        corners[3][1] = y + (hw) * axis1y + (hh) * axis2y;

        
        return corners;
    }
    float[] getAxes() {
        return new float[]{
            (float)Math.cos(angle), (float)Math.sin(angle),   // axis 1
            -(float)Math.sin(angle), (float)Math.cos(angle)   // axis 2
        };
        
    }
    
    


    float[] project(float[][] corners, float ax, float ay ){
        float maxdot;
        float mindot;
        float firstdot  = corners[0][0] * ax + corners[0][1] * ay;
        maxdot = firstdot;
        mindot = firstdot;
        for(float[] corner :corners){
            float dot  = corner[0] * ax + corner[1] * ay; 
            if(maxdot<dot){
                maxdot = dot;
            } else if(mindot>dot){
                mindot = dot;
            }
        
        }
       

        float[] result = new float[]{maxdot, mindot};
        return result;


    }
    static boolean checkSAT(Rectangle a , Rectangle b){
        float[][] acorners = a.getCoordinates();
        float[][] bcorners  = b.getCoordinates();
        float[] AxesA = a.getAxes();
        float[] AxesB = b.getAxes();
        float[] Aprojection1 = a.project(acorners,AxesA[0],AxesA[1]);
        float[] Bprojection1  = a.project(bcorners,AxesA[0],AxesA[1]);
        float[] Aprojection2 = a.project(acorners,AxesA[2],AxesA[3]);
        float[] Bprojection2  = a.project(bcorners,AxesA[2],AxesA[3]);
        float[] Aprojection3 = a.project(acorners,AxesB[0],AxesB[1]);
        float[] Bprojection3 = a.project(bcorners,AxesB[0],AxesB[1]);
        float[] Aprojection4 = a.project(acorners,AxesB[2],AxesB[3]);
        float[] Bprojection4 = a.project(bcorners,AxesB[2],AxesB[3]);        
        if(!(Aprojection1[0]>Bprojection1[1] && Bprojection1[0]>Aprojection1[1])){
            return false;
        }else if(!(Aprojection2[0]>Bprojection2[1] && Bprojection2[0]>Aprojection2[1])){
            return false;
        }else if(!(Aprojection3[0]>Bprojection3[1] && Bprojection3[0]>Aprojection3[1])){
            return false ;

        }else if(!(Aprojection4[0]>Bprojection4[1] && Bprojection4[0]>Aprojection4[1])){
            return false;
        }else{
            return true;
        }
    }
    
}
