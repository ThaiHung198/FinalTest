package controller;

import model.Contact;
import io.ContactIO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ContactController {
    private final List<Contact> contacts = new ArrayList<>();
    private final Scanner sc = new Scanner(System.in);

    private String currentWorkingFilePath = "data/contacts.csv";


    private static final Pattern PHONE_PATTERN = Pattern.compile("0\\d{9,10}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ContactController() {

    }


    private String getValidatedStringInput(String prompt, Pattern pattern, String errorMessage, boolean allowEmpty) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                if (allowEmpty) return "";
                System.out.println("Thông tin này không được để trống.");
                continue;
            }
            if (pattern == null || pattern.matcher(input).matches()) return input;
            System.out.println(errorMessage);
        }
    }

    private String getValidatedDateInput(String prompt, boolean allowEmpty) {
        String input;
        while (true) {
            System.out.print(prompt + " (định dạng yyyy-MM-dd): ");
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                if (allowEmpty) return "";
                System.out.println("Ngày sinh không được để trống nếu không được phép.");
                continue;
            }
            try {
                LocalDate.parse(input, DATE_FORMATTER);
                return input;
            } catch (DateTimeParseException e) {
                System.out.println("Định dạng ngày không hợp lệ. Vui lòng nhập theo yyyy-MM-dd.");
            }
        }
    }

    private Contact inputContact(boolean isUpdate) {
        String phone;
        if (!isUpdate) {
            System.out.println("--- THÊM MỚI DANH BẠ ---");
            phone = getValidatedStringInput("SĐT (*): ", PHONE_PATTERN, "Số điện thoại không hợp lệ (VD: 09xxxxxxxx hoặc 01xxxxxxxxx).", false);
            for (Contact existingContact : contacts) {
                if (existingContact.getPhone().equals(phone)) {
                    System.out.println("Lỗi: Số điện thoại này đã tồn tại trong danh bạ!");
                    return null;
                }
            }
        } else {
            phone = null;
        }

        String group = getValidatedStringInput("Nhóm (*): ", null, "", false);
        String name = getValidatedStringInput("Tên (*): ", null, "", false);
        String gender = getValidatedStringInput("Giới tính (*): ", null, "", false);
        String address = getValidatedStringInput("Địa chỉ (*): ", null, "", false);
        String birth = getValidatedDateInput("Ngày sinh (để trống nếu không có): ", true);
        String email = getValidatedStringInput("Email (để trống nếu không có): ", EMAIL_PATTERN, "Email không hợp lệ.", true);

        return new Contact(phone, group, name, gender, address, birth, email);
    }

    public void addContact() {
        Contact c = inputContact(false);
        if (c != null) {
            contacts.add(c);
            System.out.println(" Thêm thành công vào bộ nhớ!");
            saveContactsToFileWithMessage();
        } else {
            System.out.println(" Thêm mới không thành công.");
        }
    }

    public void showContacts() {
        if (contacts.isEmpty()) {
            System.out.println(" Danh sách rỗng.");
            return;
        }
        System.out.println(String.format("%-3s | %-12s | %-10s | %-22s | %-8s | %-20s | %-12s | %s",
                "STT", "SĐT", "Nhóm", "Họ Tên", "Giới tính", "Địa chỉ", "Ngày sinh", "Email"));
        System.out.println(new String(new char[120]).replace("\0", "-"));

        for (int i = 0; i < contacts.size(); i++) {
            System.out.println(String.format("%-3d | %s", (i + 1), contacts.get(i).toString()));
            if ((i + 1) % 5 == 0 && i < contacts.size() - 1) {
                System.out.print("Nhấn Enter để xem tiếp...");
                sc.nextLine();
            }
        }
    }

    public void updateContact(String phoneToSearch) {
        Contact contactToUpdate = null;
        int indexToUpdate = -1;
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getPhone().equals(phoneToSearch)) {
                contactToUpdate = contacts.get(i);
                indexToUpdate = i;
                break;
            }
        }

        if (contactToUpdate == null) {
            System.out.println(" Không tìm thấy danh bạ với SĐT: " + phoneToSearch);
            return;
        }

        System.out.println("--- CẬP NHẬT DANH BẠ CHO SĐT: " + phoneToSearch + " ---");
        System.out.println("(Nhấn Enter để giữ lại giá trị cũ)");

        String newGroup = getValidatedStringInputWithDefault("Nhóm mới", contactToUpdate.getGroup(), null, "", false);
        String newName = getValidatedStringInputWithDefault("Tên mới", contactToUpdate.getName(), null, "", false);
        String newGender = getValidatedStringInputWithDefault("Giới tính mới", contactToUpdate.getGender(), null, "", false);
        String newAddress = getValidatedStringInputWithDefault("Địa chỉ mới", contactToUpdate.getAddress(), null, "", false);
        String newBirth = getValidatedDateInputWithDefault("Ngày sinh mới", contactToUpdate.getBirth(), true);
        String newEmail = getValidatedStringInputWithDefault("Email mới", contactToUpdate.getEmail(), EMAIL_PATTERN, "Email không hợp lệ.", true);

        contactToUpdate.setGroup(newGroup);
        contactToUpdate.setName(newName);
        contactToUpdate.setGender(newGender);
        contactToUpdate.setAddress(newAddress);
        contactToUpdate.setBirth(newBirth.isEmpty() ? null : newBirth);
        contactToUpdate.setEmail(newEmail.isEmpty() ? null : newEmail);

        System.out.println(" Cập nhật thành công vào bộ nhớ!");
        saveContactsToFileWithMessage();
    }

    private String getValidatedStringInputWithDefault(String prompt, String defaultValue, Pattern pattern, String errorMessage, boolean allowEmpty) {
        System.out.print(prompt + " (hiện tại: " + (defaultValue != null ? defaultValue : "trống") + "): ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) return defaultValue;
        if (allowEmpty && input.isEmpty()) return "";
        if (!allowEmpty && input.isEmpty()) {
            System.out.println("Thông tin này không được để trống khi bạn thay đổi.");
            return defaultValue;
        }
        if (pattern == null || pattern.matcher(input).matches()) return input;
        System.out.println(errorMessage + " Giữ lại giá trị cũ.");
        return defaultValue;
    }

    private String getValidatedDateInputWithDefault(String prompt, String defaultValue, boolean allowEmpty) {
        System.out.print(prompt + " (hiện tại: " + (defaultValue != null ? defaultValue : "trống") + ", định dạng yyyy-MM-dd): ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) return defaultValue;
        if (allowEmpty && input.isEmpty()) return "";
        try {
            LocalDate.parse(input, DATE_FORMATTER);
            return input;
        } catch (DateTimeParseException e) {
            System.out.println("Định dạng ngày không hợp lệ. Giữ lại giá trị cũ.");
            return defaultValue;
        }
    }


    public void deleteContact(String phoneToDelete) {
        Contact contactToRemove = null;
        int indexToRemove = -1;
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getPhone().equals(phoneToDelete)) {
                contactToRemove = contacts.get(i);
                indexToRemove = i;
                break;
            }
        }

        if (contactToRemove == null) {
            System.out.println(" Không tìm thấy danh bạ với SĐT: " + phoneToDelete);
            return;
        }

        System.out.println("Bạn có chắc chắn muốn xoá danh bạ sau?");
        System.out.println(contactToRemove.toString());
        System.out.print("Nhập 'Y' để xác nhận xoá: ");
        if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
            contacts.remove(indexToRemove);
            System.out.println(" Đã xoá thành công khỏi bộ nhớ.");
            saveContactsToFileWithMessage();
        } else {
            System.out.println(" Đã hủy thao tác xoá.");
        }
    }

    public void search(String keyword) {
        List<Contact> results = new ArrayList<>();
        String keywordLower = keyword.toLowerCase().trim();
        if (keywordLower.isEmpty()) {
            System.out.println(" Từ khoá tìm kiếm không được để trống.");
            return;
        }

        for (Contact c : contacts) {
            if (c.getPhone().contains(keywordLower) ||
                    (c.getName() != null && c.getName().toLowerCase().contains(keywordLower))) {
                results.add(c);
            }
        }

        if (results.isEmpty()) {
            System.out.println(" Không tìm thấy kết quả nào cho từ khoá: '" + keyword + "'");
        } else {
            System.out.println(" Kết quả tìm kiếm cho từ khoá: '" + keyword + "' (" + results.size() + " kết quả)");
            System.out.println(String.format("%-3s | %-12s | %-10s | %-22s | %-8s | %-20s | %-12s | %s",
                    "STT", "SĐT", "Nhóm", "Họ Tên", "Giới tính", "Địa chỉ", "Ngày sinh", "Email"));
            System.out.println(new String(new char[120]).replace("\0", "-"));
            for (int i = 0; i < results.size(); i++) {
                System.out.println(String.format("%-3d | %s", (i + 1), results.get(i).toString()));
            }
        }
    }

    public void readFromFile(String filePathFromView) {
        System.out.print("Đọc từ file sẽ XÓA TOÀN BỘ danh bạ hiện tại trong bộ nhớ.\n" +
                "Bạn có chắc chắn muốn tiếp tục? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println(" Đã hủy thao tác đọc file.");
            return;
        }

        this.currentWorkingFilePath = filePathFromView;
        List<Contact> loadedContacts = ContactIO.read(this.currentWorkingFilePath);

        if (loadedContacts == null) {
            System.out.println(" Lỗi nghiêm trọng khi đọc file. Dữ liệu không được tải.");
            return;
        }

        this.contacts.clear();
        this.contacts.addAll(loadedContacts);

        File f = new File(this.currentWorkingFilePath);
        if (loadedContacts.isEmpty() && f.exists() && f.length() > 0) {
            System.out.println(" File '" + this.currentWorkingFilePath + "' được tìm thấy nhưng không có danh bạ hợp lệ nào được tải (có thể file trống sau header hoặc sai định dạng từng dòng).");
        } else if (loadedContacts.isEmpty() && (!f.exists() || f.length() == 0)) {
            System.out.println(" File '" + this.currentWorkingFilePath + "' không tồn tại hoặc trống.");
        } else if (!loadedContacts.isEmpty()) {
            System.out.println(" Đã tải thành công " + loadedContacts.size() + " liên hệ từ file '" + this.currentWorkingFilePath + "'.");
        }
    }

    public void writeToFile(String filePathFromView) {
        if (this.contacts.isEmpty()) {
            System.out.println(" Danh bạ hiện đang trống. Không có gì để ghi vào file.");
            return;
        }
        this.currentWorkingFilePath = filePathFromView;

        System.out.print("Ghi vào file '" + this.currentWorkingFilePath + "' sẽ GHI ĐÈ toàn bộ nội dung file hiện tại (nếu có).\n" +
                "Bạn có chắc chắn muốn tiếp tục? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println(" Đã hủy thao tác ghi vào file.");
            return;
        }
        saveContactsToFileWithMessage();
    }

    private void saveContactsToFileWithMessage() {
        ContactIO.write(this.currentWorkingFilePath, this.contacts);
        System.out.println(" Đã lưu " + this.contacts.size() + " liên hệ vào file: '" + this.currentWorkingFilePath + "'.");
    }
}