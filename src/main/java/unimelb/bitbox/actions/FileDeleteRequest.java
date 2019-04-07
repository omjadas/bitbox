package unimelb.bitbox.actions;

public class FileDeleteRequest implements Action {

    private String pathName;

    public FileDeleteRequest(String pathName) {
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