package com.moblong.iwe;

import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.KeyValuePair;
import com.moblong.flipped.model.Whistle;

public interface IOnDialoguesChanged {

	public void onChanged(KeyValuePair<Account, Whistle> dialogue);
}
