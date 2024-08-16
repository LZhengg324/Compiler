package ErrorHandling;

public class Error {
    private final ErrorType errorType;
    private final int lineNum;

    public Error(ErrorType errorType, int lineNum) {
        this.errorType = errorType;
        this.lineNum = lineNum;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public int getLineNum() {
        return lineNum;
    }
}
