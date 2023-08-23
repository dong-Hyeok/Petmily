/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      flexGrow: {
        2: "2",
      },
      colors: {
        black: "#0F1419",
        blueviolet: "#9747ff",
        white: "#fff",
        red: "#F4245E",
        dodgerblue: "#1f90fe",
        lightblue: "#D9ECFF",
        gray: "#0f1419",
        darkgray: "#a6a7ab",
        slategray: "#5b7083",
        lightslategray: "#8899a6",
        lightgray: "#A6A7AB",
        gray2: "#EBEEF0",
        whitesmoke: {
          100: "#f8f8f8",
          200: "#ebeef0",
        },
        kakao: "#EFD800",
        naver: "#00A348",
        blue50: "rgba(216, 236, 255, 0.50)",
      },
      fontFamily: {
        pretendard: "Pretendard",
        "one-mobile-pop-otf": "'ONE Mobile POP OTF'",
      },
      borderRadius: {
        "11xl": "30px",
        "3xs": "10px",
        "31xl": "50px",
        "8xs": "5px",
        xl: "20px",
      },
    },
    fontSize: {
      xs: "0.7rem",
      sm: "0.9rem",
      lg: "1rem",
      "3xl": "1.38rem",
      "11xl": "1.88rem",
      "19xl": "38px",
      "2lg": "1.2rem",
      xl: "1.3rem",
      "5xl": "24px",
      base: "16px",
    },
    fontWeight: {
      medium: 500,
      semibold: 600,
      bold: 700,
    },
  },
  corePlugins: {
    preflight: false,
  },
};
