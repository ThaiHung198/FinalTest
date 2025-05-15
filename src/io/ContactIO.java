package io;

import model.Contact;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ContactIO {

    private static final String CSV_DELIMITER = ",";

    public static List<Contact> read(String filePath) {
        List<Contact> contacts = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return contacts;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();


            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(CSV_DELIMITER, -1);
                if (values.length == 7) {
                    String phone = values[0];
                    String group = values[1];
                    String name = values[2];
                    String gender = values[3];
                    String address = values[4];
                    String birth = values[5].isEmpty() ? null : values[5];
                    String email = values[6].isEmpty() ? null : values[6];
                    contacts.add(new Contact(phone, group, name, gender, address, birth, email));
                } else {
                    System.err.println("Cảnh báo: Dòng CSV không đúng định dạng, bỏ qua: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy file '" + filePath + "' khi đọc.");
            return new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Lỗi IO khi đọc file: " + e.getMessage());
            return new ArrayList<>();
        }
        return contacts;
    }

    public static void write(String filePath, List<Contact> contacts) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                System.err.println("Lỗi: Không thể tạo thư mục data: " + parentDir.getAbsolutePath());
                return;
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("SĐT,Nhóm,Tên,Giới tính,Địa chỉ,Ngày sinh,Email");
            bw.newLine();

            for (Contact contact : contacts) {
                bw.write(contact.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Lỗi IO khi ghi file: " + e.getMessage());
        }
    }
}