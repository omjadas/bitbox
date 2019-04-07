package unimelb.bitbox.actions;

public class FileCreateResponse implements Action {

    private String pathName;

    public FileCreateResponse(String pathName) {
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