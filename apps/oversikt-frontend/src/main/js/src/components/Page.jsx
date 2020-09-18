import React from 'react';
import {useParams} from "react-router-dom";
import {FetchAccessToken} from "./FetchAccessToken";

const Page = () => {
    const {name} = useParams()

    return (
        <>
            <FetchAccessToken key={name} name={name}/>
        </>
    )
};

export default Page