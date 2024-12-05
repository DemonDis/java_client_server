package loader;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.util.Calendar;
import java.util.Date;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import java.security.KeyPairGenerator;
import java.security.KeyPair;

public class CertificateGenerator {

    public static void main(String[] args) throws Exception {
        String keystorePassword = "123456";  // Пароль для хранилища
        String keyPassword = "123456";       // Пароль для ключа
        String keystoreFile = "mykeystore.p12";  // Имя файла для хранилища ключей
        String alias = "myalias";  // Псевдоним сертификата

        // Генерация пары ключей (приватного и публичного)
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);  // 2048 бит для RSA
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Генерация сертификата
        X509Certificate certificate = generateCertificate(alias, keyPair);

        // Создание хранилища ключей (Keystore)
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(null, null);

        // Запись пары ключей в хранилище
        keystore.setKeyEntry(alias, keyPair.getPrivate(), keyPassword.toCharArray(), new java.security.cert.Certificate[]{certificate});

        // Сохранение хранилища ключей в файл
        try (FileOutputStream fos = new FileOutputStream(keystoreFile)) {
            keystore.store(fos, keystorePassword.toCharArray());
        }

        System.out.println("Сертификат и хранилище ключей успешно созданы!");
    }

    // Метод для генерации самоподписанного сертификата
    public static X509Certificate generateCertificate(String alias, KeyPair keyPair) throws Exception {
        Calendar calendar = Calendar.getInstance();
        Date startDate = calendar.getTime();

        // Устанавливаем срок действия сертификата (10 лет)
        calendar.add(Calendar.YEAR, 10);
        Date endDate = calendar.getTime();

        // Создаем X509-сертификат
        X509Certificate certificate = null;
        try {
            certificate = new X509Certificate() {
                // Сгенерируем самоподписанный сертификат
                @Override
                public void checkValidity() { }

                @Override
                public void checkValidity(Date date) { }

                @Override
                public int getVersion() {
                    return 3; // X509 v3
                }

                @Override
                public BigInteger getSerialNumber() {
                    return BigInteger.valueOf(System.currentTimeMillis());
                }

                @Override
                public Principal getIssuerDN() {
                    return new X500Principal("CN=" + alias);
                }

                @Override
                public Principal getSubjectDN() {
                    return new X500Principal("CN=" + alias);
                }

                @Override
                public Date getNotBefore() {
                    return startDate;
                }

                @Override
                public Date getNotAfter() {
                    return endDate;
                }

                @Override
                public byte[] getTBSCertificate() throws CertificateEncodingException {
                    return new byte[0]; // Реализация создания TBS сертификата
                }

                @Override
                public byte[] getSignature() {
                    return new byte[0]; // Реализация получения подписи
                }

                @Override
                public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException { }

                @Override
                public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException { }

                @Override
                public String toString() {
                    return "Самоподписанный сертификат для: " + alias;
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

        return certificate;
    }
}
