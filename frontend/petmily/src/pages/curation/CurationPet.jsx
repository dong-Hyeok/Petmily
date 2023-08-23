import { Suspense, useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';

import { useRecoilValue } from 'recoil';
import { RenderCuration } from 'components';
import CustomSelect from 'components/commons/CustomSelect';
import selectAtom from 'states/select';
import { curationsAtom } from 'states/curations';
import dogBanner from 'static/images/bannerDog.png';
import { category } from 'utils/utils';

function CurationPet() {
  const select = useRecoilValue(selectAtom);
  const location = useLocation();
  const { petType } = location.state;
  const [curations, setCurations] = useState([]);
  const globalCuration = useRecoilValue(curationsAtom);
  useEffect(() => {
    const fetchPetData = () => {
      const filterFirstElement = [];
      category.forEach(ele => {
        filterFirstElement.push(globalCuration[ele][0]);
      });
      setCurations(filterFirstElement);
    };
    fetchPetData();
  }, [globalCuration, petType]);

  const renderImage = () => {
    let returnImage;
    switch (petType) {
      case '강아지':
        returnImage = (
          <img
            className="relative w-full h-[250px] rounded-[20px] object-cover"
            alt=""
            src={dogBanner}
          />
        );
        break;
      case '고양이':
        returnImage = (
          <img
            className="relative w-full h-[250px] rounded-[20px] object-cover"
            alt=""
            src="https://petmily-pjt-bucket.s3.ap-northeast-2.amazonaws.com/static/catbanner.png"
          />
        );
        break;
      case '기타동물':
        returnImage = (
          <img
            className="relative w-full h-[250px] rounded-[20px] object-cover"
            alt=""
            src="https://petmily-pjt-bucket.s3.ap-northeast-2.amazonaws.com/static/etcbanner.png"
          />
        );
        break;
      default:
        break;
    }
    return returnImage;
  };

  return (
    <div className="absolute top-20 min-w-[1340px] max-w-full flex flex-1 flex-col items-center justify-center text-left text-[1.13rem] text-darkgray font-pretendard">
      <div className="min-w-[1340px] max-w-full relative text-[1.75rem] text-gray">
        <div className=" flex p-[40px] flex-col items-start justify-start text-[1.5rem] text-white">
          {renderImage()}
          <b className="absolute top-[220px] left-[48.5%] tracking-[0.01em] leading-[125%] text-white z-[10]">
            {petType}
          </b>
          <div className="h-20" />
          <b className="text-center self-stretch relative text-13xl tracking-[0.01em] leading-[125%] text-black">
            HOT TOPIC
          </b>
          <Suspense fallback={<p>글목록 로딩중...</p>}>
            <RenderCuration category="인기" renderData={curations} />
          </Suspense>
          <div className="h-10" />
          <div className="flex flex-row justify-around items-center w-full h-auto mt-20 mb-20">
            <div className="relative flex gap-5 justify-start flex-row w-full h-auto">
              <CustomSelect
                component="curationMore"
                select="카테고리"
                options={['건강', '미용', '식품', '입양', '전체']}
              />
              <CustomSelect
                component="curationMore"
                select="정렬"
                options={['최신순', '북마크 순']}
              />
            </div>
          </div>
          <Suspense fallback={<p>글목록 로딩중...</p>}>
            {['건강', '미용', '식품', '입양'].includes(select) ? (
              <RenderCuration
                category={select}
                renderData={globalCuration[select]}
              />
            ) : (
              <>
                <RenderCuration
                  pet={location.state.petType}
                  category="건강"
                  renderData={globalCuration['건강']}
                />
                <RenderCuration
                  category="미용"
                  renderData={globalCuration['미용']}
                />
                <RenderCuration
                  category="식품"
                  renderData={globalCuration['식품']}
                />
                <RenderCuration
                  category="입양"
                  renderData={globalCuration['입양']}
                />
              </>
            )}
          </Suspense>
        </div>
      </div>
    </div>
  );
}

export default CurationPet;
