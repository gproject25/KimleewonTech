package com.kimleewontech.common.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.kimleewontech.common.security.auth.AuthMember;
import com.kimleewontech.common.security.auth.AuthRole;
import com.kimleewontech.domain.admin.entity.AdminLevel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

	private static final String ROLE_CLAIM = "role";
	private static final String ADMIN_LEVEL_CLAIM = "adminLevel";

	private final SecretKey key;
	private final JwtProperties props;

	public JwtProvider(JwtProperties props) {
		this.props = props;
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(props.getSecret()));
	}

	public String createAccessToken(AuthMember member) {
		Date now = new Date();
		JwtBuilder builder = Jwts.builder()
			.subject(String.valueOf(member.getMemberId()))
			.claim(ROLE_CLAIM, member.getRole().name())
			.issuedAt(now)
			.expiration(new Date(now.getTime() + props.getAccessExpiration()));

		if (member.getAdminLevel() != null) {
			builder.claim(ADMIN_LEVEL_CLAIM, member.getAdminLevel().name());
		}
		return builder.signWith(key).compact();
	}

	public boolean isValid(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			log.debug("JWT 검증 실패: {}", e.getMessage());
			return false;
		}
	}

	public AuthMember getAuthMember(String token) {
		Claims claims = parseClaims(token);

		Long memberId = Long.valueOf(claims.getSubject());
		AuthRole role = AuthRole.valueOf(claims.get(ROLE_CLAIM, String.class));
		if (role == AuthRole.ADMIN) {
			String level = claims.get(ADMIN_LEVEL_CLAIM, String.class);
			return AuthMember.admin(memberId, AdminLevel.valueOf(level));
		}
		return AuthMember.user(memberId);
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}
