package Resume.Resume.entity;

 public class MyClass {

    public static void main(String[] args) {
        Solution s = new Solution();
        Node n8 = new Node("n8", null, null);
        Node n7 = new Node("n7", null,n8);
        Node n6 = new Node("n6",null,null);
        Node n4 = new Node("n4",null,null);
        Node n5 = new Node("n5",null,null);
        Node n2 = new Node("n2", n4,n5);
        Node n3 = new Node("n3", n6,n7);
        Node n1 = new Node("n1", n2,n3);

        System.out.println( s.lowestAncestor( n1 , n3 , n5 ).name );
    }
}

class Solution {
    public boolean isInTree( Node root, Node node ){
        if( root == null ){
            return false;
        }
        if(node == root){
            return true;
        }
        return isInTree(root.left , node) || isInTree(root.right , node);
    }
     public Node lowestAncestor(Node root, Node node1, Node node2) {
         if(root == node1 || root == node2){
             return root;
         }

         boolean node1inLeft = isInTree( root.left , node1);
         boolean node1inRight = !node1inLeft;
         boolean node2inLeft = isInTree( root.left , node2);
         boolean node2inRight = !node2inLeft;

         if( (node1inLeft && node2inRight) || (node1inRight && node2inLeft) ){
             return root;
         } else if ( node1inLeft ) {
             return lowestAncestor(root.left , node1 , node2);
         } else {
             return lowestAncestor( root.right , node1 , node2 );
         }
    }
}

class Node{
    String name;
    Node left;
    Node right;

    Node(String name, Node n1 , Node n2 ){
        this.left = n1;
        this.right = n2;
        this.name = name;
    }
}

/*Assume there is a binary tree (doesn’t have to be balanced) where each node has a left and right child.  Find the Lowest Common Ancestor of two nodes.

     n1
    /   \
  n2     n3
 / \     /  \
n4   n5 n6   n7
               \
                n8



// For node n3 and n4, it is n1.  For node n4 and n5, it is n2.  For node n2 and n1, it is n1.


Node lowestAncestor(Node root, Node node1, Node node2) {

}

class Node{
   Node left;
   Node right;
}

?*
 */