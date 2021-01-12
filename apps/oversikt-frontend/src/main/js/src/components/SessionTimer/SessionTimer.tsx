import React, { useEffect, useState } from "react";
import ProgressBar from "fremdriftslinje";

function getCookie(cname: string) {
  const name = cname + "=";
  const decodedCookie = decodeURIComponent(document.cookie);
  const ca = decodedCookie.split(";");
  for (let i = 0; i < ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) == " ") {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

const SessionTimer = () => {
  const calculateOffset = () => {
    const calcExpiry = parseInt(getCookie("sessionExpiry"));
    return calcExpiry - new Date().getTime() - serverTime + serverTime;
  };

  const [serverTime, setServerTime] = useState(
    parseInt(getCookie("serverTime"))
  );
  const [milliseconds, setMilliseconds] = useState(calculateOffset());
  const [total, setTotal] = useState(calculateOffset());
  const [now, setNow] = useState(100);

  useEffect(() => {
    setTotal(calculateOffset());
  }, [serverTime]);

  useEffect(() => {
    const calcServerTime = parseInt(getCookie("serverTime"));
    if (serverTime !== calcServerTime) {
      setServerTime(calcServerTime);
    }
    const value = (milliseconds / total) * 100;
    setNow(value);

    if (value > 0) {
      setTimeout(() => setMilliseconds(calculateOffset()), 1000);
    }
  }, [milliseconds]);

  return (
    <ProgressBar
      now={milliseconds < 0 ? 0 : Math.round(now)}
      status={milliseconds < 0 ? "error" : "inprogress"}
    >
      <p>
        {Math.round(milliseconds / 1000) < 0
          ? "Du har blitt logget ut"
          : "Blir logget ut om: " + Math.round(milliseconds / 1000) + "s"}
      </p>
    </ProgressBar>
  );
};
export default SessionTimer;
