/*
 * Copyright (C) 2017 ron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rdj;

import java.nio.file.Path;

public class RawCipher extends Thread
{
    public static int bytesPerSector = 512;
    private final UI ui;
        
    public RawCipher(UI ui) { this.ui = ui; }
    
    public void writeRawCipher(Path cipherFilePath, Path rawDeviceFilePath)
    {
        GPT gpt = new GPT(ui);
//        gpt.read(rawDeviceFilePath); gpt.print();
        gpt.create(cipherFilePath, rawDeviceFilePath);
        gpt.write(rawDeviceFilePath);
        gpt.writeCipher(cipherFilePath, rawDeviceFilePath);
    }
}