package com.example.spring_basic.service;

import com.example.spring_basic.dto.LoginRequest;
import com.example.spring_basic.dto.LoginResponse;
import com.example.spring_basic.dto.LogoutRequest;
import com.example.spring_basic.dto.RefreshRequest;
import com.example.spring_basic.entity.RefreshToken;
import com.example.spring_basic.entity.User;
import com.example.spring_basic.exception.UserNotFoundException;
import com.example.spring_basic.jwt.JwtTokenProvider;
import com.example.spring_basic.repository.RefreshTokenRepository;
import com.example.spring_basic.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getEmail());

        RefreshToken refreshTokenEntity = new RefreshToken(refreshToken, user);

        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse refresh(RefreshRequest request){

        String refreshToken = request.getRefreshToken();

        if(!jwtTokenProvider.validateToken(refreshToken)){
            throw new RuntimeException("유효하지 않은 refresh token입니다.");
        }
        if(!"REFRESH".equals(jwtTokenProvider.getTokenType(refreshToken))) {
            throw new RuntimeException("refresh token이 아닙니다.");
        }

        RefreshToken savedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 refresh token입니다."));

        User user = savedRefreshToken.getUser();

        refreshTokenRepository.deleteByToken(refreshToken);

        String newAccessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getEmail());

        RefreshToken newRefreshTokenEntity = new RefreshToken(newRefreshToken, user);

        refreshTokenRepository.save(newRefreshTokenEntity);

        return new LoginResponse(newAccessToken, newRefreshToken);

    }

    @Transactional
    public void logout(LogoutRequest request){
        String refreshToken = request.getRefreshToken();
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}
