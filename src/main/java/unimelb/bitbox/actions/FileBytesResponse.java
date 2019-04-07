package unimelb.bitbox.actions;

public class FileBytesResponse implements Action {

    private String pathName;

    public FileBytesResponse(String pathName) {
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