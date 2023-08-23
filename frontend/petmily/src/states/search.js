import { atom } from 'recoil';

const searchAtom = atom({
  key: 'search',
  default: [],
  //  ?? {
  //   accessToken: 'access',
  //   refreshToken: 'refresh',
  // },
});

export default searchAtom;
