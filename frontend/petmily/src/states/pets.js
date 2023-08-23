import { atom } from 'recoil';

const petAtom = atom({
  key: 'pets',
  default: [],
});

export default petAtom;
