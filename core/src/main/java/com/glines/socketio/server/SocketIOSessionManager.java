/**
 * The MIT License
 * Copyright (c) 2010 Tad Glines
 *
 * Contributors: Ovea.com, Mycila.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.glines.socketio.server;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class SocketIOSessionManager implements SessionManager {
    private static final char[] BASE64_ALPHABET =
          "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
          .toCharArray();
    private static final int SESSION_ID_LENGTH = 20;

    private static Random random = new SecureRandom();

    private static String generateRandomString(int length) {
        StringBuilder result = new StringBuilder(length);
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        for (int i = 0; i < bytes.length; i++) {
          result.append(BASE64_ALPHABET[bytes[i] & 0x3F]);
        }
        return result.toString();
    }

    public static String generateSessionId() {
        return generateRandomString(SESSION_ID_LENGTH);
    }

    final private ConcurrentMap<String, SocketIOSession> socketIOSessions = new ConcurrentHashMap<String, SocketIOSession>();
    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Override
    public SocketIOSession createSession(SocketIOInbound inbound, String sessionId) {
        DefaultSession impl = new DefaultSession(this, inbound, sessionId);
        socketIOSessions.put(impl.getSessionId(), impl);
        return impl;
    }

    @Override
    public SocketIOSession getSession(String sessionId) {
        return socketIOSessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        this.socketIOSessions.remove(sessionId);
    }
}
