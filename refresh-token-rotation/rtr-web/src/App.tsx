import { useState } from 'react';
import axios from 'axios';

export default function AuthDemo() {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [response, setResponse] = useState(null);

  // 요청 데이터 상태
  const [signUpRequest, setSignUpRequest] = useState({
    email: 'example@gmail.com',
    password: 'password',
    name: '홍길동',
  });
  const [loginRequest, setLoginRequest] = useState({
    email: 'example@gmail.com',
    password: 'password',
  });
  const [changePasswordRequest, setChangePasswordRequest] = useState({
    currentPassword: 'password',
    newPassword: 'newpassword',
  });

  const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
  });

  // ---- API 호출 함수들 ----
  const signUp = async () => {
    try {
      const res = await api.post('/auth/sign-up', signUpRequest);
      setResponse(res.data);
    } catch (err) {
      setResponse(err.response?.data || err.message);
    }
  };

  const login = async () => {
    try {
      const res = await api.post('/auth/login', loginRequest);
      if (res.data.data.accessToken) {
        setAccessToken(res.data.data.accessToken);
      }
      setResponse(res.data);
    } catch (err) {
      setResponse(err.response?.data || err.message);
    }
  };

  const changePassword = async () => {
    try {
      const res = await api.post(
        '/auth/change-password',
        changePasswordRequest,
        { headers: { Authorization: `Bearer ${accessToken}` } }
      );
      setAccessToken(null);
      setResponse(res.data);
    } catch (err) {
      setResponse(err.response?.data || err.message);
    }
  };

  const refresh = async () => {
    try {
      const res = await api.post('/auth/refresh');
      if (res.data.data.accessToken) {
        setAccessToken(res.data.data.accessToken);
      }
      setResponse(res.data);
    } catch (err) {
      setResponse(err.response?.data || err.message);
    }
  };

  const logout = async () => {
    try {
      const res = await api.post(
        '/auth/logout',
        {},
        {
          headers: { Authorization: `Bearer ${accessToken}` },
        }
      );
      setResponse(res.data);
      setAccessToken(null);
    } catch (err) {
      setResponse(err.response?.data || err.message);
    }
  };

  const logoutAll = async () => {
    try {
      const res = await api.post(
        '/auth/logout/all',
        {},
        {
          headers: { Authorization: `Bearer ${accessToken}` },
        }
      );
      setResponse(res.data);
      setAccessToken(null);
    } catch (err) {
      setResponse(err.response?.data || err.message);
    }
  };

  const profile = async () => {
    try {
      const res = await api.get('/accounts/profile', {
        headers: accessToken ? { Authorization: `Bearer ${accessToken}` } : {},
      });
      setResponse(res.data);
    } catch (err) {
      setResponse(err.response?.data || err.message);
    }
  };

  const testApi = async () => {
    try {
      const res = await api.get('/test', {
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      setResponse(res.data);
    } catch (err) {
      setResponse(err.response?.data || err.message);
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Auth Demo</h2>

      {/* Sign Up */}
      <h3>회원가입</h3>
      <input
        placeholder="이메일"
        value={signUpRequest.email}
        onChange={(e) =>
          setSignUpRequest({ ...signUpRequest, email: e.target.value })
        }
      />
      <input
        placeholder="비밀번호"
        type="password"
        value={signUpRequest.password}
        onChange={(e) =>
          setSignUpRequest({ ...signUpRequest, password: e.target.value })
        }
      />
      <input
        placeholder="이름"
        value={signUpRequest.name}
        onChange={(e) =>
          setSignUpRequest({ ...signUpRequest, name: e.target.value })
        }
      />
      <button onClick={signUp}>회원가입</button>

      {/* Login */}
      <h3>로그인</h3>
      <input
        placeholder="이메일"
        value={loginRequest.email}
        onChange={(e) =>
          setLoginRequest({ ...loginRequest, email: e.target.value })
        }
      />
      <input
        placeholder="비밀번호"
        type="password"
        value={loginRequest.password}
        onChange={(e) =>
          setLoginRequest({ ...loginRequest, password: e.target.value })
        }
      />
      <button onClick={login}>로그인</button>

      {/* Change Password */}
      <h3>비밀번호 변경</h3>
      <input
        placeholder="현재 비밀번호"
        type="password"
        value={changePasswordRequest.currentPassword}
        onChange={(e) =>
          setChangePasswordRequest({
            ...changePasswordRequest,
            currentPassword: e.target.value,
          })
        }
      />
      <input
        placeholder="새 비밀번호"
        type="password"
        value={changePasswordRequest.newPassword}
        onChange={(e) =>
          setChangePasswordRequest({
            ...changePasswordRequest,
            newPassword: e.target.value,
          })
        }
      />
      <button onClick={changePassword}>비밀번호 변경</button>

      {/* Refresh Token */}
      <h3>토큰 재발급</h3>
      <button onClick={refresh}>토큰 재발급</button>

      {/* Logout */}
      <h3>로그아웃</h3>
      <button onClick={logout}>로그아웃</button>
      <button onClick={logoutAll}>모든 기기 로그아웃</button>

      {/* Profile */}
      <h3>프로필 조회</h3>
      <button onClick={profile}>프로필 조회</button>

      {/* Test */}
      <h3>테스트 API</h3>
      <button onClick={testApi}>테스트 API 호출</button>

      {/* 출력 */}
      <div style={{ marginTop: 20 }}>
        <strong>AccessToken:</strong> {accessToken}
      </div>
      <div style={{ marginTop: 20 }}>
        <strong>Response:</strong>
        <pre>{JSON.stringify(response, null, 2)}</pre>
      </div>
    </div>
  );
}
