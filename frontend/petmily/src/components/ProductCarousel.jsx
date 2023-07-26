import React from 'react';

import { placeholderImage } from '../utils/utils';

const placeholderData = Array(4).fill('');
function ProductCarousel() {
  return (
    <div className="relative h-fit mt-[10rem] mb-[5rem] flex flex-col flex-1 text-[2.38rem] text-white min-w-[1320px] w-full">
      <div className="gap-5 flex flex-row min-w-[1320px] max-w-full">
        {placeholderData.map(() => {
          return (
            <div className="flex-1 flex w-full self-stretch">
              <img className="relative w-full" alt="" src={placeholderImage} />
            </div>
          );
        })}
      </div>
      <div className="relative top-[-5rem] left-0 brightness-125 flex flex-1 flex-col">
        <div
          className="relative flex flex-row justify-center items-center bg-dodgerblue [backdrop-filter:blur(100px)]
            rounded-tr-[100px] rounded-br-[100px] sm:w-[40rem] sm:h-[8rem] md:w-[30rem] md:h-[6rem] lg:w-[40rem] lg:h-[8rem] xl:w-[50rem] xl:h-[10rem]"
        >
          <div className="tracking-[0.01em] leading-[125%] font-semibold">
            인기 강아지 장난감 브랜드명
          </div>
        </div>
        <div
          className="flex flex-row justify-center gap-2 items-center relative bg-dodgerblue [backdrop-filter:blur(100px)]
            rounded-tr-[100px] rounded-br-[100px] sm:w-[40rem] sm:h-[8rem] md:w-[30rem] md:h-[6rem] lg:w-[40rem] lg:h-[8rem] xl:w-[50rem] xl:h-[10rem]"
        >
          <div className="tracking-[0.01em] leading-[125%] font-semibold">
            12900원
          </div>
          <div className="text-[1.5rem] tracking-[0.01em] leading-[125%] font-semibold">
            ~ 최저가
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProductCarousel;
