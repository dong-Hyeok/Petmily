import { atom } from 'recoil';
import { recoilPersist } from 'recoil-persist';

const { persistAtom } = recoilPersist();
const headerAtom = atom({
  key: 'headers',
  default: '',
  effects_UNSTABLE: [persistAtom],
});

export default headerAtom;
