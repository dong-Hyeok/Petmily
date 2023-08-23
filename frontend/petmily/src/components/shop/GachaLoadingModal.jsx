import { useEffect } from 'react';
import { func, string } from 'prop-types';
import { useRecoilValue } from 'recoil';

import { Player } from '@lottiefiles/react-lottie-player';
import gachaLoading from 'static/animations/gachaLoading.json';
import userAtom from 'states/users';
import { SWAP } from 'utils/utils';
import useFetch from 'utils/fetch';

function GachaLoadingModal({ onClose, gachaOpen, gachaSelect, setGachaItem }) {
  const user = useRecoilValue(userAtom);
  const fetchGacha = useFetch();
  useEffect(() => {
    async function fetchData() {
      try {
        const selected = SWAP[gachaSelect];
        const response = await fetchGacha.post('/item/getRandom', {
          userEmail: user.userEmail,
          randomKind: selected,
        });
        setGachaItem(response);
        setTimeout(() => {
          onClose();
          gachaOpen();
        }, 1000);
      } catch (error) {
        onClose();
      }
    }
    fetchData();
  }, [gachaOpen, gachaSelect, onClose, setGachaItem]);

  return (
    <div className="relative rounded-[10px] bg-inherit w-[656px] h-[450px] max-w-full flex flex-col justify-center items-center max-h-full text-left text-xl text-darkgray font-pretendard">
      <Player loop src={gachaLoading} autoplay />
    </div>
  );
}

GachaLoadingModal.propTypes = {
  onClose: func,
  gachaOpen: func,
  gachaSelect: string,
  setGachaItem: func,
};

export default GachaLoadingModal;
