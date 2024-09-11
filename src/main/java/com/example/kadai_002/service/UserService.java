package com.example.kadai_002.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kadai_002.entity.Role;
import com.example.kadai_002.entity.User;
import com.example.kadai_002.form.SignupForm;
import com.example.kadai_002.form.UserEditForm;
import com.example.kadai_002.repository.RoleRepository;
import com.example.kadai_002.repository.UserRepository;
import com.stripe.model.PaymentMethod;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public User create(SignupForm signupForm) {
		User user = new User();
		Role role = roleRepository.findByName("ROLE_GENERAL");

		user.setName(signupForm.getName());
		user.setFurigana(signupForm.getFurigana());
		user.setDateOfBirth(signupForm.getDateOfBirth());
		user.setPhoneNumber(signupForm.getPhoneNumber());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
		user.setRole(role);
		user.setEnabled(false);
		user.setIsPaidMember(false);

		return userRepository.save(user);
	}

	@Transactional
	public void update(UserEditForm userEditForm) {
		User user = userRepository.getReferenceById(userEditForm.getId());

		user.setName(userEditForm.getName());
		user.setFurigana(userEditForm.getFurigana());
		user.setDateOfBirth(userEditForm.getDateOfBirth());
		user.setPhoneNumber(userEditForm.getPhoneNumber());
		user.setEmail(userEditForm.getEmail());

		userRepository.save(user);
	}

	// メールアドレスが登録済みかどうかをチェックする
	public boolean isEmailRegistered(String email) {
		User user = userRepository.findByEmail(email);
		return user != null;
	}

	public boolean isSamePassword(String password, String passwordConfirmation) {
		return password.equals(passwordConfirmation);
	}

	@Transactional
	public void enableUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}

	// メールアドレスが変更されたかどうかをチェックする
	public boolean isEmailChanged(UserEditForm userEditForm) {
		User currentUser = userRepository.getReferenceById(userEditForm.getId());
		return !userEditForm.getEmail().equals(currentUser.getEmail());
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Transactional
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	//ユーザーのロールを無料会員に変更し、有料会員から無料会員への変更を記録
	@Transactional
	public void changeUserRoleToFree(User user) {
		Role freeRole = roleRepository.findByName("ROLE_GENERAL");
		if (freeRole != null) {
			user.setRole(freeRole);
			user.setIsPaidMember(false);
			user.setStripeCustomerId(null);
			user.setStripeSubscriptionId(null);
			user.setCardLast4(null);
			user.setCardBrand(null);
			user.setCardExpMonth(null);
			user.setCardExpYear(null);
			userRepository.save(user);
		} else {
			throw new IllegalStateException("Free role not found");
		}
	}

	@Transactional
	public void updateUserCardInfo(User user, PaymentMethod paymentMethod) {
		if (paymentMethod != null && paymentMethod.getCard() != null) {
			PaymentMethod.Card card = paymentMethod.getCard();
			user.setCardLast4(card.getLast4());
			user.setCardBrand(card.getBrand());
			user.setCardExpMonth(card.getExpMonth().intValue());
			user.setCardExpYear(card.getExpYear().intValue());
			userRepository.save(user); // データベースにユーザー情報を保存
		}
	}
}