import { atom } from 'recoil';
import { recoilPersist } from 'recoil-persist';

const { persistAtom } = recoilPersist();
const commentAtom = atom({
  key: 'comment',
  default: [],
  effects_UNSTABLE: [persistAtom],
});

export default commentAtom;
