import { atom } from 'recoil';

const searchpostsAtom = atom({
  key: 'searchposts',
  default: [],
});

export default searchpostsAtom;
