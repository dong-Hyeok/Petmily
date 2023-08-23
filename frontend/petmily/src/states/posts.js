import { atom } from 'recoil';

const postsAtom = atom({
  key: 'posts',
  default: [],
});

export default postsAtom;
