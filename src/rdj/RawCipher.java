/*
 * Copyright (C) 2018 Ron de Jong (ronuitzaandam@gmail.com).
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

import java.nio.file.Path;

public class RawCipher extends Thread
{
    private final UI ui;
        
    public RawCipher(UI ui) { this.ui = ui; }
    
    public void writeRawCipher(Path cipherFilePath, Path targetDeviceFilePath)
    {
        GPT gpt = new GPT(ui);
        gpt.create(GPT.getCipherFileSize(ui, cipherFilePath), targetDeviceFilePath);
        gpt.write(targetDeviceFilePath);
        gpt.writeCipher(cipherFilePath, targetDeviceFilePath);
        try { Thread.sleep(250); } catch (InterruptedException ex) {  }
    }

    public void cloneRawCipher(Path cipherDeviceFilePath, Path targetDeviceFilePath)
    {
        GPT gpt = new GPT(ui);
//        gpt.read(cipherDeviceFilePath); // gpt.print();
        gpt.create(Device.getCipherPartitionSize(cipherDeviceFilePath), targetDeviceFilePath);
        gpt.write(targetDeviceFilePath);
        gpt.cloneCipher(cipherDeviceFilePath, targetDeviceFilePath);
        try { Thread.sleep(250); } catch (InterruptedException ex) {  }
    }
}