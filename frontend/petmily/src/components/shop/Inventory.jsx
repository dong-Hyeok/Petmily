import { shape, string, number, arrayOf } from 'prop-types';
import { useRecoilState } from 'recoil';
import userAtom from 'states/users';
import useFetch from 'utils/fetch';

function Inventory({ inventoryItems }) {
  const inventoryFetch = useFetch();
  const [user, setUser] = useRecoilState(userAtom);
  const { userEmail, userBackground, userBadge, userRing } = user;

  const handleElementClick = async (elementId, itemType) => {
    try {
      const response = await inventoryFetch.put('/item/equipment', {
        userEmail,
        itemId: elementId,
        itemType,
      });
      if (!response.status) {
        if (itemType === 'ring') setUser({ ...user, userRing: response });
        if (itemType === 'badge') setUser({ ...user, userBadge: response });
        if (itemType === 'background')
          setUser({ ...user, userBackground: response });
      } else {
        if (itemType === 'ring') setUser({ ...user, userRing: null });
        if (itemType === 'badge') setUser({ ...user, userBadge: null });
        if (itemType === 'background')
          setUser({ ...user, userBackground: null });
      }
    } catch (error) {
      throw new Error(error);
    }
  };
  return (
    <div className="sticky top-[7.3rem] left-0 rounded-11xl bg-white min-w-[350px] basis-1/4 h-fit overflow-hidden shrink-0 flex flex-col py-4 px-0 box-border items-start justify-start gap-[12px] text-lg">
      <div className="self-stretch flex flex-col py-0 px-4 items-start justify-start gap-[17px] text-xl">
        <div className="relative font-semibold">내 보관함</div>
        <div className="relative bg-whitesmoke-200 w-[447px] h-px" />
      </div>
      <div className="self-stretch rounded-11xl bg-white flex flex-col py-0 px-3 items-start justify-start gap-[12px]">
        <div className="self-stretch flex flex-col py-1 px-4 items-start justify-start gap-[12px] text-dodgerblue">
          <div className="relative font-semibold">프로필 링</div>
          <div className="self-stretch relative bg-whitesmoke-200 h-px" />
        </div>
        {inventoryItems.ring ? (
          inventoryItems.ring.map(ele => {
            return (
              <div
                key={ele.itemName}
                className="self-stretch flex flex-col py-1 px-4 items-start justify-start gap-[24px]"
              >
                <div className="self-stretch flex flex-row items-start justify-between">
                  <div className="flex flex-row items-center justify-center gap-[12px]">
                    <div
                      className="relative w-[30px] h-[30px] object-cover border-solid border-4 rounded-[100px]"
                      style={{ borderColor: `#${ele.itemColor}` }}
                    />
                    <div className="relative font-semibold">{ele.itemName}</div>
                  </div>
                  {!(userRing && userRing?.itemId === ele.itemId) ? (
                    <div
                      role="presentation"
                      className="bg-dodgerblue hover:brightness-90 h-[30px] overflow-hidden flex flex-row py-[15px] px-5 box-border items-center justify-center text-center text-mini text-white rounded-xl cursor-pointer"
                      onClick={() => {
                        handleElementClick(ele.itemId, 'ring');
                      }}
                    >
                      <b className="relative tracking-[0.05em] leading-[19px]">
                        장착
                      </b>
                    </div>
                  ) : (
                    <div
                      role="presentation"
                      className="bg-white hover:brightness-90 h-[30px] overflow-hidden flex flex-row py-[15px] px-5 box-border items-center justify-center text-center text-mini text-dodgerblue rounded-xl cursor-pointer border-solid border-2 border-dodgerblue"
                      onClick={() => {
                        handleElementClick(null, 'ring');
                      }}
                    >
                      <b className="relative tracking-[0.05em] leading-[19px]">
                        해제
                      </b>
                    </div>
                  )}
                </div>
              </div>
            );
          })
        ) : (
          <div className="px-5">보유한 링이 없습니다.</div>
        )}
      </div>
      <div className="self-stretch rounded-11xl bg-white flex flex-col py-0 px-3 items-start justify-start gap-[12px]">
        <div className="self-stretch flex flex-col py-1 px-4 items-start justify-start gap-[12px] text-[dodgerblue]">
          <div className="relative font-semibold">뱃지</div>
          <div className="self-stretch relative bg-whitesmoke-200 h-px" />
        </div>
        {inventoryItems?.badge ? (
          inventoryItems?.badge.map(ele => {
            return (
              <div
                key={ele.itemName}
                className="self-stretch flex flex-col py-1 px-4 items-start justify-start gap-[24px]"
              >
                <div className="self-stretch flex flex-row items-start justify-between">
                  <div className="flex flex-row items-center justify-center gap-[12px]">
                    <img
                      src={ele.itemImg}
                      alt=""
                      className="relative w-[50px] h-[50px] object-cover rounded-[100px]"
                    />
                    <div className="relative font-semibold">{ele.itemName}</div>
                  </div>
                  {!(userBadge && userBadge?.itemId === ele.itemId) ? (
                    <div
                      role="presentation"
                      className="bg-dodgerblue hover:brightness-90 h-[30px] overflow-hidden flex flex-row py-[15px] px-5 box-border items-center justify-center text-center text-mini text-white rounded-xl cursor-pointer"
                      onClick={() => {
                        handleElementClick(ele.itemId, 'badge');
                      }}
                    >
                      <b className="relative tracking-[0.05em] leading-[19px]">
                        장착
                      </b>
                    </div>
                  ) : (
                    <div
                      role="presentation"
                      className="bg-white hover:brightness-90 h-[30px] overflow-hidden flex flex-row py-[15px] px-5 box-border items-center justify-center text-center text-mini text-dodgerblue rounded-xl cursor-pointer border-solid border-2 border-dodgerblue"
                      onClick={() => {
                        handleElementClick(null, 'badge');
                      }}
                    >
                      <b className="relative tracking-[0.05em] leading-[19px]">
                        해제
                      </b>
                    </div>
                  )}
                </div>
              </div>
            );
          })
        ) : (
          <div className="px-5">보유한 뱃지가 없습니다.</div>
        )}
      </div>
      <div className="self-stretch rounded-11xl bg-white flex flex-col py-0 px-3 items-start justify-start gap-[12px]">
        <div className="self-stretch flex flex-col py-1 px-4 items-start justify-start gap-[12px] text-dodgerblue">
          <div className="relative font-semibold">커버 이미지</div>
          <div className="self-stretch relative bg-whitesmoke-200 h-px" />
        </div>
        {inventoryItems.background ? (
          inventoryItems?.background.map(ele => {
            return (
              <div
                key={ele.itemName}
                className="self-stretch flex flex-col py-1 px-4 items-start justify-start gap-[24px]"
              >
                <div className="self-stretch flex flex-row items-start justify-between">
                  <div className="flex flex-row items-center justify-center gap-[12px]">
                    <img
                      src={ele.itemImg}
                      alt=""
                      className="relative w-[50px] h-[50px] object-cover rounded-[100px]"
                    />
                    <div className="relative font-semibold">{ele.itemName}</div>
                  </div>
                  {!(userBackground && userBackground.itemId === ele.itemId) ? (
                    <div
                      role="presentation"
                      className="bg-dodgerblue hover:brightness-90 h-[30px] overflow-hidden flex flex-row py-[15px] px-5 box-border items-center justify-center text-center text-mini text-white rounded-xl cursor-pointer"
                      onClick={() => {
                        handleElementClick(ele.itemId, 'background');
                      }}
                    >
                      <b className="relative tracking-[0.05em] leading-[19px]">
                        장착
                      </b>
                    </div>
                  ) : (
                    <div
                      role="presentation"
                      className="bg-white hover:brightness-90 h-[30px] overflow-hidden flex flex-row py-[15px] px-5 box-border items-center justify-center text-center text-mini text-dodgerblue rounded-xl cursor-pointer border-solid border-2 border-dodgerblue"
                      onClick={() => {
                        handleElementClick(null, 'background');
                      }}
                    >
                      <b className="relative tracking-[0.05em] leading-[19px]">
                        해제
                      </b>
                    </div>
                  )}
                </div>
              </div>
            );
          })
        ) : (
          <div className="px-5">보유한 커버이미지가 없습니다.</div>
        )}
      </div>
    </div>
  );
}
const items = shape({
  itemId: 0,
  itemType: string,
  itemName: string,
  itemImg: string,
  itemColor: string,
  itemRarity: string,
  inventoryList: arrayOf(shape({ inventoryId: number })),
});

Inventory.propTypes = {
  inventoryItems: arrayOf(items),
};

export default Inventory;
