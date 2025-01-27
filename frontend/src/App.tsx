function App() {
  // console.log(import.meta.env.VITE_CORE_FRONT_BASE_URL);
  // console.log(import.meta.env.VITE_CORE_API_BASE_URL);

  const socialLoginForKakaoUrl = `http://localhost:8090/oauth2/authorization/kakao`; // 카카오 로그인 요청 URL
  const redirectUrlAfterSocialLogin = "http://localhost:3000"; // 카카오 로그인 후 리다이렉트 URL

  return (
    <>
      <a
        href={`${socialLoginForKakaoUrl}?redirectUrl=${redirectUrlAfterSocialLogin}`}
      >
        <span>카카오 로그인</span>
      </a>
    </>
  );
}

export default App;
