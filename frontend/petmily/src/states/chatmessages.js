import { atom } from 'recoil';

const chatmessagesAtom = atom({
  key: 'chatmessagesatom',
  default: [],
});

export default chatmessagesAtom;
