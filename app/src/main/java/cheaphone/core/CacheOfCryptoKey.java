/**

 Copyright 2014 Bortoli Tomas

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
/**
 * Cache for AES key
 *
 */
package cheaphone.core;

import java.io.File;
import java.util.Date;

public class CacheOfCryptoKey
{
  private final short EXPIRATION_KEY_PERIOD = 4; //days of validity
  private Cryptography c = new Cryptography();
  private File file;
  private Pair<byte[], Pair<byte[], Date>> key; //hash as first element, key and date second

  public CacheOfCryptoKey()
  {
	file= new File(Constants.applicationFilesPath + Constants.cacheKey);
	key= (Pair<byte[], Pair<byte[], Date>>)Serialize.loadSerializedObject(this.file);
	  
	if (this.key == null)
	{
		this.key = new Pair<byte[], Pair<byte[], Date>>();
		this.key.setFirst(null);
	}
  }
  
  public byte[] getHash(){
	  return key.getFirst();
  }

  public byte[] getKey()
  {
    if (this.key.getFirst() == null)
      return null;
    if (new Date().before((this.key.getSecond()).getSecond()))
      return (byte[])(this.key.getSecond()).getFirst();
    return null;
  }

  public void setKey(byte[] key)
  {
    this.key.setFirst(this.c.Sha1Hash(key));
    Pair<byte[], Date> couple = new Pair<byte[], Date>();
    couple.setFirst(key);
    couple.setSecond(Utility.addDays(new Date(), EXPIRATION_KEY_PERIOD));
    this.key.setSecond(couple);
    
    Serialize.saveObject(this.key, this.file);
  }
}