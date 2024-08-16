package ErrorHandling;

import java.util.ArrayList;

public class ErrorHandler {
    private static final ArrayList<Error> errorList = new ArrayList<>();

    public static void addError(Error error) {
        for (Error e : errorList) {
            if (e.getLineNum() == error.getLineNum()) {
                return;
            }
        }
        if (!errorList.isEmpty() &&
                errorList.get(errorList.size() - 1).getLineNum() > error.getLineNum()) {
            for (int i = 0; i < errorList.size(); i++) {
                if (error.getLineNum() < errorList.get(i).getLineNum()) {
                    errorList.add(i, error);
                    break;
                }
            }
        } else {
            errorList.add(error);
        }
    }

    public static ArrayList<Error> getList() {
        return errorList;
    }

    public static void print() {
        for (Error e : errorList) {
            System.out.println(e.getErrorType() + " : " + e.getLineNum());
        }
    }
}
