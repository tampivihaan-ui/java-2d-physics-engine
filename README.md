# Java 2D Physics Engine

A 2D rigid body physics engine built from scratch in Java using Swing. Supports circles and rotatable rectangles with realistic collision detection and response.

## Features

- **Circle rigid bodies** with configurable radius and mass
- **Rotatable rectangle rigid bodies** with configurable width, height, and angle
- **Circle-circle collision** with impulse-based response and positional correction
- **Rectangle-rectangle collision** using the Separating Axis Theorem (SAT)
- **Circle-rectangle collision** using local space transformation
- **Gravity simulation** for all bodies
- **Wall collision** for all shapes
- **Interactive spawning** — click anywhere to spawn shapes

## Controls

| Input | Action |
|-------|--------|
| `Click` | Spawn a shape at cursor position |
| `C` | Switch to circle spawn mode |
| `R` | Switch to rectangle spawn mode |

## How It Works

### Circle-Circle Collision
Uses impulse-based collision response with positional correction to prevent sinking. Collision normal is calculated from the vector between centers.

### Rectangle-Rectangle Collision (SAT)
The Separating Axis Theorem projects both rectangles onto 4 axes (2 per rectangle) and checks for overlap. If any axis shows no overlap, the shapes are not colliding.

### Circle-Rectangle Collision
Transforms the circle's position into the rectangle's local coordinate space using the rectangle's rotation axes (basis vectors). Performs an axis-aligned closest point check in local space, then transforms the result back to world space.

### Rotation
Rectangle corners are calculated using a rotated basis derived from the rectangle's angle:
```
axis1 = (cos θ, sin θ)   // along width
axis2 = (-sin θ, cos θ)  // along height
```
Each corner is expressed as a linear combination of these basis vectors — directly applying the 2D rotation matrix.

## How to Run

### Prerequisites
- Java JDK 8 or higher

### Running
```bash
javac *.java
java App
```

## Project Structure

```
├── App.java          # Entry point, creates JFrame window
├── GamePanel.java    # Main panel, game loop, collision detection
├── RigidBody.java    # Base class with position, velocity, mass
├── Circle.java       # Extends RigidBody, adds radius
└── Rectangle.java    # Extends RigidBody, adds width, height, angle, SAT
```

## What I Learned

Built as a project to learn Java coming from a C background. Key concepts covered:

- Java OOP — classes, inheritance, constructors
- Swing graphics and game loop using javax.swing.Timer
- 2D physics — impulse response, positional correction, tunneling prevention
- Linear algebra applied to graphics — rotation matrices, basis vectors, dot products, projections
- Separating Axis Theorem for convex shape collision detection
- Coordinate space transformations (local space vs world space)
