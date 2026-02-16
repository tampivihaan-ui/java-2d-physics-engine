public class RigidBody {
    float x;
    float y;
    float vx;
    float vy;
    float mass; 
    

    RigidBody(float startx , float starty , float startvx, float startvy, float mass){
        this.x = startx;
        this.y = starty;
        this.vx  = startvx;
        this.vy = startvy;
        this.mass = mass;
        



    }
    void update(float dt){
        x += vx * dt ;
        y += vy * dt;
    }
    
    
}
