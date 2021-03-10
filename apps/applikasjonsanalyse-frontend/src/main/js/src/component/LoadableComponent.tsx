import React, { useState, useEffect } from 'react';
import Loading from './Loading';

const LoadableComponent = ({ onFetch, render }: any) => {
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState();
  useEffect(() => {
    onFetch().then((response: any) => {
      setData(response);
      setLoading(false);
    });
    // eslint-disable-next-line
  }, []);

  if (loading) {
    return <Loading />;
  }
  return render(data);
};
export default LoadableComponent;
