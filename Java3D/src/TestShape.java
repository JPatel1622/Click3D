import javax.vecmath.*;

import com.sun.j3d.utils.geometry.Sphere;

import javax.media.j3d.*;

public class TestShape extends IndexedTriangleArray {
  public TestShape() {
	  super(4, TriangleArray.COORDINATES | TriangleArray.NORMALS, 12);
	    setCoordinate(0, new Point3f(1f,1f,1f));
	    setCoordinate(1, new Point3f(1f,-1,-1f));
	    setCoordinate(2, new Point3f(-1f,1f,-1f));
	    setCoordinate(3, new Point3f(-1f,-1f,1f));
	    int[] coords = {0,1,2,0,3,1,1,3,2,2,3,0};
	    float n = (float)(1.0/Math.sqrt(3));
	    setNormal(0, new Vector3f(n,n,-n));
	    setNormal(1, new Vector3f(n,-n,n));
	    setNormal(2, new Vector3f(-n,-n,-n));
	    setNormal(3, new Vector3f(-n,n,n));
	    int[] norms = {0,0,0,1,1,1,2,2,2,3,3,3};
	    setCoordinateIndices(0, coords);
	    setNormalIndices(0, norms);
	  
//	super(6, GeometryArray.COORDINATES, 24); // 6 vertices, 3*8 indices
//    setCoordinate(0, new Point3f(0f, 0f, 1f));
//    setCoordinate(1, new Point3f(-1f, 0f, 0f));
//    setCoordinate(2, new Point3f(0f, -1f, 0f));
//    setCoordinate(3, new Point3f(1f, 0f, 0f));
//    setCoordinate(4, new Point3f(0f, 1f, 0f));
//    setCoordinate(5, new Point3f(0f, 0f, -1f));
//    
//    int[] coords = {3, 4, 0, 0, 4, 1, 1, 4, 5, 5, 4, 3, 3, 2, 0, 0, 2, 1, 1, 2, 5, 5, 2, 3};   
//   
//    setCoordinateIndices(0, coords);
	  
  }
}