package tecnico.ulisboa.sirs.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordHolder {
    private String oldPassword;
    private String newPassword;
    private String newPasswordRepeat;
}