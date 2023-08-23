import { atom } from 'recoil';
import { recoilPersist } from 'recoil-persist';

const { persistAtom } = recoilPersist();
const productAtom = atom({
  key: 'products',
  default: null,
  effects_UNSTABLE: [persistAtom],
});

export default productAtom;
