import { atom, selector } from 'recoil';
import { recoilPersist } from 'recoil-persist';

const { persistAtom } = recoilPersist();
const curationsAtom = atom({
  key: 'curations',
  default: null,
  effects_UNSTABLE: [persistAtom],
});

const addToCuration = selector({
  key: 'addToCuration',
  get: ({ get }) => {
    return get(curationsAtom);
  },
  set: ({ get, set }, { category, item }) => {
    const curations = get(curationsAtom);
    switch (category) {
      case '강아지':
        set(curationsAtom, {
          ...curations,
          dog: [...curations.dog, item],
        });
        break;
      case '고양이':
        set(curationsAtom, {
          ...curations,
          cat: [...curations.cat, item],
        });
        break;
      case '기타동물':
        set(curationsAtom, {
          ...curations,
          etc: [...curations.etc, item],
        });
        break;
      default:
        break;
    }
  },
});

export { curationsAtom, addToCuration };
