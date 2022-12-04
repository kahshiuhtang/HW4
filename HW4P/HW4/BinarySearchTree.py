class BinarySearchTree:
    def __init__(self, name, root):
        self.temp_node = None
        self.ptr = None
        self.name = name
        self.root = root

    def add_all(self, *args):
        for val in args:
            ptr = self.root
            temp_node = Node(value=val)
            if ptr.value is None:
                self.root = temp_node
                continue
            else:
                while True:
                    if ptr.value is not None and ptr.value > val:
                        if not ptr.left:
                            ptr.left = temp_node
                            break
                        ptr = ptr.left
                    elif ptr.value is not None and ptr.value < val:
                        if not ptr.right:
                            ptr.right = temp_node
                            break
                        ptr = ptr.right
                    else:
                        break
        return

    def __str__(self):
        ans = "[" + self.name + "]"
        order = self.helper(self.root)
        return ans + order

    def helper(self, node):
        if node.left is None and node.right is None:
            return node.value.__str__()
        elif node.left is None:
            return node.value.__str__() + " R:(" + self.helper(node.right) + ")"
        elif node.right is None:
            return node.value.__str__() + " L:(" + self.helper(node.left) + ")"
        return node.value.__str__() + " L:(" + self.helper(node.left) + ") R:(" + self.helper(node.right) + ")"


class Node:
    def __init__(self, value=None):
        self.value = value
        self.left = None
        self.right = None
        self.stack = []
        self.move(self)

    def __str__(self):
        return self.value.__str__()

    def helper(self, node):
        if node.left is None and node.right is None:
            return node.value.__str__()
        elif node.left is None:
            return node.value.__str__() + " R:(" + self.helper(node.right) + ")"
        elif node.right is None:
            return node.value + " L:(" + self.helper(node.left) + ")"
        return node.value + " L:(" + self.helper(node.left) + ") R:(" + self.helper(node.right) + ")"

    def __iter__(self):
        return self.inorder_traversal(self)

    def __next__(self):
        yield self.inorder_traversal(self)

    def move(self, t):
        ptr = t
        while ptr:
            self.stack.append(ptr)
            ptr = ptr.left
        return

    def inorder_traversal(self, root):
        stack = []
        ptr = root
        while ptr or len(stack) > 0:
            while ptr:
                stack.append(ptr)
                ptr = ptr.left
            ptr = stack.pop()
            yield ptr.value
            ptr = ptr.right


class Merger:
    def merge(self, tree1, tree2):
        ans = []
        first = tree1.root.inorder_traversal(tree1.root)
        second = tree2.root.inorder_traversal(tree2.root)

        def get_one():
            for x in first:
                return x
            return StopIteration

        def get_second():
            for val in second:
                return val
            return StopIteration

        first_tree = get_one()
        second_tree = get_second()
        if first_tree is not None and second_tree is not None:
            while first_tree is not StopIteration and second_tree is not StopIteration:
                if first_tree > second_tree:
                    ans.append(second_tree)
                    second_tree = get_second()
                else:
                    ans.append(first_tree)
                    first_tree = get_one()

        while first_tree is not StopIteration and first_tree is not None:
            ans.append(first_tree)
            first_tree = get_one()
        while second_tree is not StopIteration and second_tree is not None:
            ans.append(second_tree)
            second_tree = get_second()
        return ans


if __name__ == '__main__':
    t1 = BinarySearchTree(name="Oak", root=Node())
    t2 = BinarySearchTree(name="Birch", root=Node())
    t1.add_all(0, 1, 10, 2, 7)
    t2.add_all(1, 0, 10, 2, 7)
    for x in t1.root:
        print(x)
    for x in t2.root:
        print(x)
    print(Merger().merge(t1, t2))
