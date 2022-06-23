import React from 'react'

import './ContentContainer.less'

type Props = {
	children: React.PropsWithChildren<any>
	className?: string
}

const ContentContainer = ({ children, className = 'content-container' }: Props) => (
	<div className={className}>{children}</div>
)
export default ContentContainer
