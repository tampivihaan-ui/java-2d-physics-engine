public class Circle extends RigidBody{
    float radius;
    Circle(float x , float y , float vx , float vy, float mass ,  float radius ){
        super(x,y,vx,vy,mass);
        this.radius = radius;
    }
    
}
