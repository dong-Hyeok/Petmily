import { atom } from 'recoil';

const popularsAtom = atom({
  key: 'populars',
  default: [],
});

export default popularsAtom;
