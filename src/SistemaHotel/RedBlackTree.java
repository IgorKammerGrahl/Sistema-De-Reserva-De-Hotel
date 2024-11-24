package SistemaHotel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RedBlackTree<T extends Reserva> {

	private enum Color {
		RED,
		BLACK
	}

	private class Node {
		private T value;
		Color color;
		Node left, right, parent;

		public Node(T value) {
			this.value = value;
			this.color = Color.RED;
			this.left = this.right = this.parent = null;
		}
	}

	private Node root;

	public RedBlackTree() {
		root = null;
	}

	public void insert(T value) {
		Node newNode = new Node(value);
		root = insertNode(root, newNode);
		fixInsertion(newNode);
	}

	private Node insertNode(Node current, Node newNode) {
		if (current == null)
			return newNode;

		if (newNode.value.compareTo(current.value) < 0) {
			current.left = insertNode(current.left, newNode);
			current.left.parent = current;
		} else if (newNode.value.compareTo(current.value) > 0) {
			current.right = insertNode(current.right, newNode);
			current.right.parent = current;
		}

		return current;
	}

	private void fixInsertion(Node node) {
	    Node parent, grandparent;

	    while (node != root && node.parent != null && node.parent.color == Color.RED) {
	        parent = node.parent;
	        grandparent = parent.parent;

	        if (parent == grandparent.left) {
	            Node uncle = grandparent.right;

	            if (uncle != null && uncle.color == Color.RED) {
	                parent.color = Color.BLACK;
	                uncle.color = Color.BLACK;
	                grandparent.color = Color.RED;
	                node = grandparent;
	            } else {
	                if (node == parent.right) {
	                    rotateLeft(parent);
	                    node = parent;
	                    parent = node.parent;
	                }
	                rotateRight(grandparent);
	                swapColors(parent, grandparent); 
	                node = parent;
	            }
	        } else {
	            Node uncle = grandparent.left;

	            if (uncle != null && uncle.color == Color.RED) {
	                parent.color = Color.BLACK;
	                uncle.color = Color.BLACK;
	                grandparent.color = Color.RED;
	                node = grandparent;
	            } else {
	                if (node == parent.left) {
	                    rotateRight(parent);
	                    node = parent;
	                    parent = node.parent;
	                }
	                rotateLeft(grandparent);
	                swapColors(parent, grandparent); 
	                node = parent;
	            }
	        }
	    }

	    root.color = Color.BLACK;
	}

	private void rotateLeft(Node node) {
		Node newNode = node.right;
		node.right = newNode.left;

		if (newNode.left != null)
			newNode.left.parent = node;

		newNode.parent = node.parent;

		if (node.parent == null)
			root = newNode;
		else if (node == node.parent.left)
			node.parent.left = newNode;
		else
			node.parent.right = newNode;

		newNode.left = node;
		node.parent = newNode;
	}

	private void rotateRight(Node node) {
		Node newNode = node.left;
		node.left = newNode.right;

		if (newNode.right != null)
			newNode.right.parent = node;

		newNode.parent = node.parent;

		if (node.parent == null)
			root = newNode;
		else if (node == node.parent.right)
			node.parent.right = newNode;
		else
			node.parent.left = newNode;

		newNode.right = node;
		node.parent = newNode;
	}

	private void swapColors(Node node1, Node node2) {
		Color temp = node1.color;
		node1.color = node2.color;
		node2.color = temp;
	}

	public T search(T value, Comparator<T> comparator) {
		Node result = searchNode(root, value, comparator);
		return (result != null) ? result.value : null;
	}

	private Node searchNode(Node current, T value, Comparator<T> comparator) {
		if (current == null || comparator.compare(value, current.value) == 0) {
			return current;
		}

		if (comparator.compare(value, current.value) < 0) {
			return searchNode(current.left, value, comparator);
		} else {
			return searchNode(current.right, value, comparator);
		}
	}

	public void delete(T value, Comparator<T> comparator) {
		Node nodeToDelete = searchNode(root, value, comparator);

		if (nodeToDelete == null) {
			return;
		}
		Color originalColor = nodeToDelete.color;

		Node nodeToFix;

		if (nodeToDelete.left == null) {
			nodeToFix = nodeToDelete.right;
			transplant(nodeToDelete, nodeToDelete.right);
		} else if (nodeToDelete.right == null) {
			nodeToFix = nodeToDelete.left;
			transplant(nodeToDelete, nodeToDelete.left);
		} else {
			Node successor = minimum(nodeToDelete.right);
			originalColor = successor.color;
			nodeToFix = successor.right;

			if (successor.parent == nodeToDelete) {
				if (nodeToFix != null) {
					nodeToFix.parent = successor;
				}
			} else {
				transplant(successor, successor.right);
				successor.right = nodeToDelete.right;
				successor.right.parent = successor;
			}

			transplant(nodeToDelete, successor);
			successor.left = nodeToDelete.left;
			successor.left.parent = successor;
			successor.color = nodeToDelete.color;
		}

		if (originalColor == Color.BLACK) {
			fixDeletion(nodeToFix);
		}
	}

	private void transplant(Node u, Node v) {
		if (u.parent == null) {
			root = v;
		} else if (u == u.parent.left) {
			u.parent.left = v;
		} else {
			u.parent.right = v;
		}

		if (v != null) {
			v.parent = u.parent;
		}
	}

	private Node minimum(Node node) {
		while (node.left != null) {
			node = node.left;
		}
		return node;
	}

	private void fixDeletion(Node node) {
		while (node != root && (node == null || node.color == Color.BLACK)) {
			if (node == node.parent.left) {
				Node sibling = node.parent.right;

				if (sibling.color == Color.RED) {
					sibling.color = Color.BLACK;
					node.parent.color = Color.RED;
					rotateLeft(node.parent);
					sibling = node.parent.right;
				}

				if ((sibling.left == null || sibling.left.color == Color.BLACK) &&
						(sibling.right == null || sibling.right.color == Color.BLACK)) {
					sibling.color = Color.RED;
					node = node.parent;
				} else {
					if (sibling.right == null || sibling.right.color == Color.BLACK) {
						if (sibling.left != null) {
							sibling.left.color = Color.BLACK;
						}
						sibling.color = Color.RED;
						rotateRight(sibling);
						sibling = node.parent.right;
					}

					sibling.color = node.parent.color;
					node.parent.color = Color.BLACK;
					if (sibling.right != null) {
						sibling.right.color = Color.BLACK;
					}
					rotateLeft(node.parent);
					node = root;
				}
			} else {
				Node sibling = node.parent.left;

				if (sibling.color == Color.RED) {
					sibling.color = Color.BLACK;
					node.parent.color = Color.RED;
					rotateRight(node.parent);
					sibling = node.parent.left;
				}

				if ((sibling.right == null || sibling.right.color == Color.BLACK) &&
						(sibling.left == null || sibling.left.color == Color.BLACK)) {
					sibling.color = Color.RED;
					node = node.parent;
				} else {
					if (sibling.left == null || sibling.left.color == Color.BLACK) {
						if (sibling.right != null) {
							sibling.right.color = Color.BLACK;
						}
						sibling.color = Color.RED;
						rotateLeft(sibling);
						sibling = node.parent.left;
					}

					sibling.color = node.parent.color;
					node.parent.color = Color.BLACK;
					if (sibling.left != null) {
						sibling.left.color = Color.BLACK;
					}
					rotateRight(node.parent);
					node = root;
				}
			}
		}

		if (node != null) {
			node.color = Color.BLACK;
		}
	}

	public void displayTree() {
		if (root == null) {
			System.out.println("A árvore está vazia");
		} else {
			displayTreeRecursive(root, " ", true);
		}
	}

	private void displayTreeRecursive(Node node, String indent, boolean last) {
		if (node != null) {
			System.out.println(indent + (last ? "└── " : "├── ") + node.value + "(" + node.color + ")");
			displayTreeRecursive(node.left, indent + (last ? "    " : "│   "), false);
			displayTreeRecursive(node.right, indent + (last ? "    " : "│   "), true);
		}
	}

	public List<T> inOrderTraversal() {
		List<T> result = new ArrayList<>();
		inOrderHelper(root, result);
		return result;
	}

	private void inOrderHelper(Node node, List<T> result) {
		if (node != null) {
			inOrderHelper(node.left, result);
			result.add(node.value);
			inOrderHelper(node.right, result);
		}
	}
}
