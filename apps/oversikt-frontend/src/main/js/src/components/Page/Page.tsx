import React from 'react';

import './Page.less'

type Props = {
    title: string,
    children: React.ReactNode
}

const Page = ({title, children}: Props) => (
    <div className="page">
        <h1>{title}</h1>
        {children}
    </div>
)

export default Page;