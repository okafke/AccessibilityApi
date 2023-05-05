// INavigationTreeService.aidl
package io.github.okafke.aapi.aidl;

import io.github.okafke.aapi.aidl.INavigationTreeListener;
import io.github.okafke.aapi.aidl.IKeyboard;
import io.github.okafke.aapi.aidl.Node;

interface INavigationTreeService {
    int getInputs();

    void setNavigationTree(inout Node[] tree);

    long registerListener(INavigationTreeListener listener);

    void unregisterListener(long id);

    IKeyboard getKeyboard();

}
