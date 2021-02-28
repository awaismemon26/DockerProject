package de.uniba.dsg.jaxrs.utils;

// The idea of Either originates from Haskell's Either
// see https://hackage.haskell.org/package/base-4.14.0.0/docs/Data-Either.html
//
// For a C programmer: Either is basically a Union type. It is expected that the value is either Left, or Right.
// Not both at the same time
//
// The main idea about Either usage in this project is to differentiate easily between errors and success.
// If we receive an error, we just put the error in the "Left" and set "Right" to null
// If we receive a success, we just put the result in "Right" and set "Left" to null
//
// One could argue that one shall rename "left" to "error" and "right" to "success", but this does not match
// the generic usage of Either, as Either shall remain generic in many contexts, not just error handling
public class Either<L, R> {
    private final L left;
    private final R right;

    private Either(L left, R right) {
        if (left != null && right != null) {
            throw new RuntimeException("Constructing Either with two non-null values is not allowed");
        }

        if(left == null && right == null) {
            throw new RuntimeException("Constructing Either with to null values is not allowed");
        }

        this.left = left;
        this.right = right;
    }

    public static <L,R> Either<L, R> Right(R right) {
        return new Either<>(null, right);
    }

    public static <L,R> Either<L, R> Left(L left) {
        return new Either<>(left, null);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}
