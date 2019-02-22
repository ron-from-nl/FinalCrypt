/*
 * Copyright © 2017 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/ ; either
 * version 4.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International Public License for more details.
 *
 * You should have received a copy of the Creative Commons 
 * Public License License along with this software;
 */

package rdj;

public interface UI
{
    public void log(String message, boolean status, boolean log, boolean logfile, boolean errfile, boolean print);
//    
    public void processGraph(int value);
    public void processProgress(int filesProgressPercent, int fileProgressPercent, long bytesTotalParam, long bytesProcessedParam, long bytesPerMiliSecondParam);
    public void fileProgress();
    public void processFinished();
    public void processStarted();
    public void buildReady(FCPathList fcPathListParam, boolean validBuild);
}
