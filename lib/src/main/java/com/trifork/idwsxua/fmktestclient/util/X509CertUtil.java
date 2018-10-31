package com.trifork.idwsxua.fmktestclient.util;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Objects;

public class X509CertUtil {
    public static KeyStore loadKeyStore(byte[] pkcs12, String password) {
        KeyStore pkcs12KeyStore;//
        try {
            pkcs12KeyStore = KeyStore.getInstance("PKCS12");
            try (InputStream pkcs12instream = new BufferedInputStream(new ByteArrayInputStream(pkcs12))) {
                pkcs12KeyStore.load(pkcs12instream, password.toCharArray());
            }
            return pkcs12KeyStore;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Unable to load PKCS12 content", e);
        }
    }

    public static KeyStore getKeyStoreFromResource(String resourceName, String password) {
        byte[] pkcs12 = readResource(resourceName);
        return loadKeyStore(pkcs12, password);
    }

    public static byte[] readResource(String resourceName) {

        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            BufferedInputStream buf = new BufferedInputStream(Objects.requireNonNull(in, "Unknown resource " + resourceName));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int n;
            while ((n = buf.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, n);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
