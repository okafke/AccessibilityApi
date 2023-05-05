// IKeyboard.aidl
package io.github.okafke.aapi.aidl;

interface IKeyboard {
    void type(String key);

    void delete();

    void enter();

    void open();

    void hide();

}