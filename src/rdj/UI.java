package rdj;

public interface UI
{
    public void log(String message);
    public void status(String status);
    public void updateEncryptionDiffStats(int value);
    public void updateTotalProgress(int value);
    public void updateFileProgress(int value);
    public void updateBufferProgress(int value);
}
