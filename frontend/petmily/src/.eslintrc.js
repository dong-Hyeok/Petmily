module.exports = {
  env: {
    browser: true,
    es6: true,
    node: true,
  },
  settings: {
    // import/resolver` 는 `eslint-plugin-import` 의 경로 설정 옵션
    'import/resolver': {
      node: {
        paths: ['src'],
      },
    },
  },
  plugins: ['react', 'simple-import-sort', 'import'],
  extends: ['eslint:recommended', 'airbnb', 'plugin:prettier/recommended'],
  rules: {
    'react/react-in-jsx-scope': 'off',
    'react/jsx-filename-extension': ['error', { extensions: ['.js', '.jsx'] }],
    'no-console': 0,

    'linebreak-style': [
      'error',
      // eslint-disable-next-line global-require
      require('os').EOL === '\r\n' ? 'windows' : 'unix',
    ],
    'prettier/prettier': ['error', { endOfLine: 'auto' }],
    'react/require-default-props': 'off',
    'jsx-a11y/label-has-associated-control': [
      2,
      {
        labelAttributes: ['htmlFor'],
      },
    ],
    'import/no-unresolved': 'off',
  },
};
