import { atom } from 'recoil';
import { recoilPersist } from 'recoil-persist';

const { persistAtom } = recoilPersist();
const followAtom = atom({
  key: 'followatom',
  default: [],
  effects_UNSTABLE: [persistAtom],
});

export default followAtom;
