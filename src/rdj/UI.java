package rdj;

public interface UI
{
    public void log(String message);
    public void error(String message);
    public void status(String status);
    public void println(String message);
    public void updateEncryptionDiffStats(int value);
    public void updateProgress(int filesProgress, int fileProgress);
    public void encryptionEnded();
}
