package school.enrollment.model;

import java.time.LocalDate;

public class Student {
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String birthPlace;
    private String civilStatus;
    private String sex;
    private String address;
    private LocalDate registrationDate;
    private boolean active = true;

    public Student() {}

    /** Legacy 6-arg constructor — kept for combo-box placeholder usage. */
    public Student(String studentId, String firstName, String lastName,
                   String email, String phone, String address) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.phone     = phone;
        this.address   = address;
    }

    /** Full constructor including the 4 new personal-info fields. */
    public Student(String studentId, String firstName, String lastName, String email,
                   String phone, LocalDate birthDate, String birthPlace,
                   String civilStatus, String sex, String address) {
        this.studentId   = studentId;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.email       = email;
        this.phone       = phone;
        this.birthDate   = birthDate;
        this.birthPlace  = birthPlace;
        this.civilStatus = civilStatus;
        this.sex         = sex;
        this.address     = address;
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String    getStudentId()   { return studentId; }
    public void      setStudentId(String v)   { this.studentId = v; }
    public String    getFirstName()   { return firstName; }
    public void      setFirstName(String v)   { this.firstName = v; }
    public String    getLastName()    { return lastName; }
    public void      setLastName(String v)    { this.lastName = v; }
    public String    getEmail()       { return email; }
    public void      setEmail(String v)       { this.email = v; }
    public String    getPhone()       { return phone; }
    public void      setPhone(String v)       { this.phone = v; }
    public LocalDate getBirthDate()   { return birthDate; }
    public void      setBirthDate(LocalDate v){ this.birthDate = v; }
    public String    getBirthPlace()  { return birthPlace; }
    public void      setBirthPlace(String v)  { this.birthPlace = v; }
    public String    getCivilStatus() { return civilStatus; }
    public void      setCivilStatus(String v) { this.civilStatus = v; }
    public String    getSex()         { return sex; }
    public void      setSex(String v)         { this.sex = v; }
    public String    getAddress()     { return address; }
    public void      setAddress(String v)     { this.address = v; }
    public LocalDate getRegistrationDate()    { return registrationDate; }
    public void      setRegistrationDate(LocalDate v) { this.registrationDate = v; }

    public String getFullName() { return firstName + " " + lastName; }

    @Override
    public String toString() {
        return studentId + " - " + getFullName() + " (" + email + ")";
    }
}