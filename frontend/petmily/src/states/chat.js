import { atom } from 'recoil';

const chatAtom = atom({
  key: 'chatatom',
  default: [],
});

export default chatAtom;
