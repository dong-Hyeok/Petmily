import { useEffect, useState, useCallback } from 'react';

import { useRecoilState, useRecoilValue } from 'recoil';
import { useNavigate } from 'react-router-dom';
import StorefrontIcon from '@mui/icons-material/Storefront';
import { Alert } from '@mui/material';

import authAtom from 'states/auth';
import {
  GachaComponent,
  PointLog,
  Inventory,
  PortalPopup,
  GachaModal,
  GachaLoadingModal,
} from 'components';
import { ReactComponent as StarCoin } from 'static/images/starCoin.svg';
import userAtom from 'states/users';
import useFetch from 'utils/fetch';
import { gachaButtons, priceToString } from 'utils/utils';

function CustomShop() {
  const navigate = useNavigate();
  const auth = useRecoilValue(authAtom);
  const fetchData = useFetch();
  const [user, setUser] = useRecoilState(userAtom);
  const [pointLogs, setPointLogs] = useState([]);
  const [inventoryItems, setInventoryItems] = useState([]);
  const [point, setPoint] = useState(0);

  const [gachaLoadingModalOpen, setGachaLoadingModalOpen] = useState(false);
  const [gachaModalOpen, setGachaModalOpen] = useState(false);
  const [gachaSelect, setGachaSelect] = useState('');
  const [gachaItem, setGachaItem] = useState(null);
  const [hasMorePoint, setHasMorePoint] = useState(true);
  useEffect(() => {
    if (gachaModalOpen || gachaLoadingModalOpen || !hasMorePoint) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
    return () => {
      document.body.style.overflow = 'auto';
    };
  }, [gachaModalOpen, gachaLoadingModalOpen, hasMorePoint]);

  useEffect(() => {
    if (!auth || !Object.keys(auth).length) {
      setUser(null);
      navigate('/login');
    }
    async function fetchPointLog() {
      try {
        const response = await fetchData.get(
          `/usagePoint?userEmail=${user.userEmail}`,
        );
        setPointLogs(response);
      } catch (error) {
        throw new Error(error);
      }
    }
    async function fetchPoint() {
      try {
        const response = await fetchData.get(
          `/userpoint?userEmail=${user.userEmail}`,
        );
        if (response.status === 200) {
          setPoint(0);
        } else {
          setPoint(response);
        }
      } catch (error) {
        throw new Error(error);
      }
    }
    async function fetchInventory() {
      try {
        const response = await fetchData.get(
          `/item/inventory?userEmail=${user.userEmail}`,
        );
        setInventoryItems(response);
      } catch (error) {
        throw new Error(error);
      }
    }
    fetchPointLog();
    fetchPoint();
    fetchInventory();
  }, [gachaModalOpen]);

  const openGachaLoadingModal = useCallback(() => {
    setGachaLoadingModalOpen(true);
  }, []);
  const closeGachaLoadingModal = useCallback(() => {
    setGachaLoadingModalOpen(false);
  }, []);
  const openGachaModal = useCallback(() => {
    setGachaModalOpen(true);
  }, []);
  const closeGachaModal = useCallback(() => {
    setGachaModalOpen(false);
  }, []);

  const closeErrorModal = useCallback(() => {
    setHasMorePoint(true);
  }, []);

  return (
    <div className="flex flex-row justify-center bg-inherit min-w-[1340px] h-full text-left text-11xl text-dodgerblue font-pretendard">
      <div className="top-24 relative px-9 py-5 min-w-[1340px] w-[96%] max-w-full flex flex-row items-start justify-center gap-4 text-xl text-gray">
        <PointLog logs={pointLogs} />
        <div className="rounded-11xl min-w-[700px] max-w-[1000px] bg-dodgerblue basis-[50%] flex flex-col items-center justify-start text-11xl text-white">
          <div className="self-stretch rounded-t-11xl rounded-b-none bg-dodgerblue flex flex-col p-6 items-start justify-start gap-[32px]">
            <div className="self-stretch flex flex-row items-center justify-between">
              <div className="flex flex-row items-center justify-center">
                <div className="self-stretch flex flex-row items-center justify-center gap-[12px]">
                  <StorefrontIcon fontSize="large" />
                  <b className="relative tracking-[0.01em] leading-[125%]">
                    상점
                  </b>
                </div>
              </div>
            </div>
            <div className="w-[59px] flex flex-col items-start justify-start gap-[7px] text-center text-base">
              <div className="overflow-hidden flex flex-row py-0 px-3 items-start justify-start">
                <b className="relative leading-[19px] whitespace-nowrap">
                  내 코인
                </b>
              </div>
              <div className="flex flex-row items-center justify-center gap-[12px] text-[40px]">
                <StarCoin />
                <b className="relative leading-[19px]">
                  {priceToString(point)}
                </b>
              </div>
            </div>
          </div>
          <div className="self-stretch basis-1/4 flex-1 rounded-11xl flex flex-col p-6 items-start justify-start gap-[24px] text-xl text-gray">
            <div className="self-stretch flex-1 flex flex-row items-start justify-start gap-[24px]">
              {gachaButtons[0].map(ele => (
                <GachaComponent
                  key={ele.itemTitle}
                  itemTitle={ele.itemTitle}
                  price={ele.price}
                  modalOpen={openGachaLoadingModal}
                  setGachaSelect={setGachaSelect}
                  setHasMorePoint={setHasMorePoint}
                  point={point}
                />
              ))}
            </div>
            <div className="self-stretch flex-1 flex flex-row items-start justify-start gap-[24px]">
              {gachaButtons[1].map(ele => (
                <GachaComponent
                  key={ele.itemTitle}
                  itemTitle={ele.itemTitle}
                  price={ele.price}
                  modalOpen={openGachaLoadingModal}
                  setGachaSelect={setGachaSelect}
                  setHasMorePoint={setHasMorePoint}
                  point={point}
                />
              ))}
            </div>
          </div>
        </div>
        <Inventory inventoryItems={inventoryItems} />
      </div>
      {gachaLoadingModalOpen && (
        <PortalPopup
          overlayColor="rgba(113, 113, 113, 0.4)"
          placement="Centered"
          onOutsideClick={closeGachaLoadingModal}
        >
          <GachaLoadingModal
            onClose={closeGachaLoadingModal}
            gachaOpen={openGachaModal}
            gachaSelect={gachaSelect}
            setGachaItem={setGachaItem}
          />
        </PortalPopup>
      )}
      {gachaModalOpen && (
        <PortalPopup
          overlayColor="rgba(113, 113, 113, 0.4)"
          placement="Centered"
          onOutsideClick={closeGachaModal}
        >
          <GachaModal onClose={closeGachaModal} gachaItem={gachaItem} />
        </PortalPopup>
      )}
      {!hasMorePoint && (
        <PortalPopup
          overlayColor="rgba(113, 113, 113, 0.4)"
          placement="Centered"
          onOutsideClick={closeErrorModal}
        >
          <Alert
            variant="filled"
            severity="error"
            className="flex justify-center items-center"
          >
            <p className="font-bold">보유한 포인트가 부족합니다.</p>
          </Alert>
        </PortalPopup>
      )}
    </div>
  );
}
export default CustomShop;
