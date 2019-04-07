package unimelb.bitbox.actions;

public class FileModifyResponse implements Action {

    private String pathName;

    public FileModifyResponse(String pathName) {
        this.pathName = pathName;
    }

    @Override
    public void execute() {

    }

    @Override
    public int compare(Action action) {
        return 0;
    }

}