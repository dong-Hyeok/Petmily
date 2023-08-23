import { useRef } from 'react';
import { string, number, func } from 'prop-types';
import { Player } from '@lottiefiles/react-lottie-player';

import { ReactComponent as StarCoin } from 'static/images/starCoin.svg';
import coin from 'static/animations/coin.json';

function GachaComponent({
  itemTitle,
  price,
  modalOpen,
  setGachaSelect,
  setHasMorePoint,
  point,
}) {
  const coinRef = useRef(null);
  const handleMouseEnter = e => {
    e.currentTarget.style.border = '2px solid dodgerblue';
    coinRef.current.play();
  };
  const handleMouseLeave = e => {
    e.currentTarget.style.border = '2px solid transparent';
    coinRef.current.stop();
  };

  return (
    <div
      role="presentation"
      className="self-stretch flex-1 rounded-11xl bg-white flex flex-col p-6 items-center justify-start gap-[12px] cursor-pointer"
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      onClick={() => {
        if (point < price) {
          setHasMorePoint(false);
        } else {
          modalOpen();
          setGachaSelect(itemTitle);
        }
      }}
    >
      {/* <StarCoin width={255} height={255} /> */}
      <Player loop src={coin} ref={coinRef} />
      <div className="self-stretch flex flex-row items-center justify-start">
        <StarCoin width={50} height={50} />
        <div className="relative tracking-[0.01em] leading-[125%] font-semibold text-dodgerblue">
          {price}
        </div>
      </div>
      <span className="relative w-full text-base ml-5 tracking-[0.01em] leading-[125%] font-bold">
        {itemTitle} 랜덤 뽑기
      </span>
    </div>
  );
}

GachaComponent.propTypes = {
  itemTitle: string,
  price: number,
  modalOpen: func,
  setGachaSelect: func,
  setHasMorePoint: func,
  point: number,
};

export default GachaComponent;
