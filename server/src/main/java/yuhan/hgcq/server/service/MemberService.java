package yuhan.hgcq.server.service;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.dto.member.LoginForm;
import yuhan.hgcq.server.dto.member.MemberUpdateForm;
import yuhan.hgcq.server.dto.member.SignupForm;
import yuhan.hgcq.server.dto.photo.UploadMemberForm;
import yuhan.hgcq.server.repository.MemberRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository mr;
    private final S3Operations s3Operations;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * Join
     *
     * @param form Join form
     * @return memberId
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public synchronized Long join(SignupForm form) throws IllegalArgumentException {
        String name = form.getName();
        String email = form.getEmail();
        String password = form.getPassword();

        if (!duplicateEmail(email)) {
            throw new IllegalArgumentException("Already Exist Email");
        }

        if (!duplicateName(name)) {
            throw new IllegalArgumentException("Already Exist Name");
        }

        Member member = new Member(name, email, password);
        mr.save(member);

        log.info("Join Member : {}", member);
        return member.getId();
    }

    @Transactional
    public void delete(Long memberId) {
        mr.delete(memberId);
        log.info("Delete Member : {}", memberId);
    }

    /**
     * Login
     *
     * @param loginForm login form
     * @return member
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public Member login(LoginForm loginForm) throws IllegalArgumentException {
        List<String> emailList = mr.findAllEmails();

        String memberEmail = loginForm.getEmail();
        String memberPassword = loginForm.getPassword();

        if (!emailList.contains(memberEmail)) {
            throw new IllegalArgumentException("Not exist Email");
        }

        Member fm = mr.findOne(memberEmail);

        if (fm.getPassword().equals(memberPassword)) {
            log.info("Login Success : {}", fm);
            return fm;
        } else {
            throw new IllegalArgumentException("Wrong Password");
        }
    }

    /**
     * Update member information
     *
     * @param member member
     * @param form   update form
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void updateMember(Member member, MemberUpdateForm form) throws IllegalArgumentException {
        ensureNotNull(member, "Member");

        String newName = form.getName();
        String newPassword = form.getPassword();

        if (newName != null && duplicateName(newName)) {
            member.changeName(newName);
        }

        if (newPassword != null) {
            member.changePassword(newPassword);
        }

        mr.save(member);
        log.info("Update Member : {}", member);
    }

    /**
     * Find member
     *
     * @param id memberId
     * @return member
     * @throws IllegalArgumentException Argument is wrong
     */
    public Member searchOne(Long id) throws IllegalArgumentException {
        Member fm = mr.findOne(id);

        if (fm == null) {
            throw new IllegalArgumentException("Member Not Found");
        }

        return fm;
    }

    /**
     * Find memberList
     *
     * @return memberList
     */
    public List<Member> searchAll() {
        return mr.findAll();
    }

    /**
     * Find memberList by name
     *
     * @param name name
     * @return memberList
     */
    public List<Member> searchAllByName(String name) {
        return mr.findByName(name);
    }

    /**
     * Check duplicate email
     *
     * @param email email
     * @return is duplicate?
     */
    public boolean duplicateEmail(String email) {
        List<String> emails = mr.findAllEmails();
        return !emails.contains(email);
    }

    /**
     * Check duplicate name
     *
     * @param name name
     * @return is duplicate?
     */
    public boolean duplicateName(String name) {
        List<String> names = mr.findAllNames();
        return !names.contains(name);
    }

    /**
     * Upload member image
     *
     * @param member member
     * @param form   member image
     * @return image path
     * @throws IOException              Upload error
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public String upload(Member member, UploadMemberForm form) throws IOException, IllegalArgumentException {
        ensureNotNull(member, "Member");

        MultipartFile file = form.getFile();
        String name = file.getOriginalFilename();

        String key = "images/member/" + member.getId() + "/" + name;

        try (InputStream inputStream = file.getInputStream()) {
            s3Operations.upload(bucketName, key, inputStream,
                    ObjectMetadata.builder().contentType(file.getContentType()).build());
            member.changeImage(key);
            mr.save(member);

            log.info("Upload Member : {}", member);
            return key;
        } catch (IOException e) {
            log.error("Upload Member Image Error");
            throw new IOException(e.getMessage());
        }
    }

    @Transactional
    public void toggleSearched(Member member) {
        member.changeSearch();
        mr.save(member);
        log.info("Change Search : {}", member);
    }

    /**
     * Argument Check if Null
     *
     * @param obj  argument
     * @param name by log
     */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }
}
