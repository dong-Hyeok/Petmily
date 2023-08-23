import { shape, number, string, instanceOf, arrayOf } from 'prop-types';
import { priceToString } from 'utils/utils';

function PointLog({ logs }) {
  return (
    <div className="sticky right-0 top-[7.3rem] rounded-11xl bg-white min-w-[350px] h-[600px] max-h-[90vh] overflow-y-scroll shrink-0 flex flex-col basis-1/4 p-4 box-border items-center justify-start gap-[6px]">
      <div className="flex flex-col items-start justify-center gap-[17px] w-full">
        <div className="relative font-semibold">포인트 내역</div>
        <div className="relative bg-whitesmoke-200 w-full h-px" />
      </div>
      {logs.length > 0 ? (
        logs.map((log, idx) => {
          return (
            <div
              key={log.date}
              className="self-stretch flex flex-col items-start justify-start gap-[6px]"
            >
              <div className="self-stretch rounded-11xl bg-white flex flex-row p-3 items-start justify-start gap-[24px]">
                <div
                  className={`self-stretch relative max-h-full w-[1px] border-solid border-2 ${
                    log.pointType
                      ? 'bg-dodgerblue border-dodgerblue'
                      : 'bg-red border-red'
                  }`}
                  alt=""
                />
                <div className="w-[319px] flex flex-col items-start justify-start gap-[12px]">
                  <div className="self-stretch flex flex-row items-center justify-start gap-[6px]">
                    <div
                      className={`relative tracking-[0.05em] leading-[125%] font-semibold ${
                        log.pointType ? 'text-dodgerblue' : 'text-red'
                      }`}
                    >
                      {log.pointType
                        ? `+${priceToString(log.pointCost)}`
                        : `-${priceToString(log.pointCost)}`}
                    </div>
                    <div className="relative text-base tracking-[0.01em] leading-[125%] font-semibold">
                      포인트가 {log.pointType ? '지급' : '차감'}되었습니다.
                    </div>
                  </div>
                  <div className="self-stretch relative text-base tracking-[0.01em] leading-[125%] text-slategray">
                    <span>{log.pointContent}</span>
                    <span>{log.date}</span>
                  </div>
                </div>
              </div>
              {idx < logs.length - 1 && (
                <div className="relative bg-whitesmoke-200 w-full h-px" />
              )}
            </div>
          );
        })
      ) : (
        <div className="text-center">
          <p>사용 내역이 없습니다.</p>
        </div>
      )}
    </div>
  );
}

const logType = shape({
  type: true,
  point: number,
  message: string,
  date: instanceOf(Date),
});

PointLog.propTypes = {
  logs: arrayOf(logType),
};

export default PointLog;
