package unimelb.bitbox.actions;

public class FileCreateRequest implements Action {

    private String pathName;

    public FileCreateRequest(String pathName) {
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