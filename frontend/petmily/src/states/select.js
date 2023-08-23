import { atom } from 'recoil';
// import { recoilPersist } from 'recoil-persist';

// const { persistAtom } = recoilPersist();
const selectAtom = atom({
  key: 'select',
  default: '',
});

export default selectAtom;
