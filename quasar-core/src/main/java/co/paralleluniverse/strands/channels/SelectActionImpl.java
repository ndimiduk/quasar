/*
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2014, Parallel Universe Software Co. All rights reserved.
 * 
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *  
 *   or (per the licensee's choosing)
 *  
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.strands.channels;

public final class SelectActionImpl<Message> extends SelectAction<Message> {
    private volatile Selector selector;
    private final boolean isData;
    Object token;

    SelectActionImpl(Selector selector, int index, Port<Message> port, Message message) {
        super((Selectable<Message>) port);
        this.selector = selector;
        this.index = index;
        this.item = message;
        this.isData = message != null;
    }

    SelectActionImpl(Port<Message> port, Message message) {
        this(null, -1, port, message);
    }

    Selector selector() {
        return selector;
    }

    void setSelector(Selector selector) {
        assert this.selector == null;
        this.selector = selector;
    }

    void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int index() {
        return index;
    }

    public boolean isData() {
        return isData;
    }

    public void setItem(Message item) {
        this.item = item;
        this.done = true;
    }

    void resetReceive() {
        assert !isData;
        item = null;
        done = false;
    }

    public boolean lease() {
        if (selector == null)
            return true;
        return selector.lease();
    }

    void returnLease() {
        if (selector != null)
            selector.returnLease();
    }

    public void won() {
        if (selector != null)
            selector.setWinner(this);
    }

    @Override
    public String toString() {
        return "SelectAction{" + (isData ? ("send " + item + " to") : "receive from") + " " + port
                + (isDone() ? (" " + (isData ? "done" : (" -> " + item))) : "") + '}'
                + " " + selector;
    }
}
