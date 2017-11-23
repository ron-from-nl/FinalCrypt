package rdj;

public interface UI
{
    public void log(String message);
    public void error(String message);
    public void status(String status);
    public void updateEncryptionDiffStats(int value);
    public void updateProgress(int filesProgress, int fileProgress);
    public void updateProgressMax(int filesProgressMax, int fileProgressMax);
    public void encryptionEnded();
}
