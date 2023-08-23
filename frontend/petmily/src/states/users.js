import { atom } from 'recoil';
import { recoilPersist } from 'recoil-persist';

const { persistAtom } = recoilPersist();
const userAtom = atom({
  key: 'loginState',
  default: {},
  effects_UNSTABLE: [persistAtom],
});

export default userAtom;
